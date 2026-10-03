package com.gulshan.doctor.appointment.Service.Implementation;

import com.gulshan.doctor.appointment.Entity.*;
import com.gulshan.doctor.appointment.Entity.Enums.AppointmentStatus;
import com.gulshan.doctor.appointment.Entity.Enums.PaymentStatus;
import com.gulshan.doctor.appointment.Entity.Projections.ScheduledAppointmentProjection;
import com.gulshan.doctor.appointment.Exceptions.*;
import com.gulshan.doctor.appointment.Repository.AppointmentRepository;
import com.gulshan.doctor.appointment.Repository.DoctorAvailabilityRepository;
import com.gulshan.doctor.appointment.Repository.DoctorRepository;
import com.gulshan.doctor.appointment.Repository.PatientRepository;
import com.gulshan.doctor.appointment.RequestDTO.AppointmentCreateRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.ScheduledAppointmentRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.AppointmentCancelResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.AppointmentCreateResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.ScheduledAppointmentResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.AppointmentServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentServiceInterface {


    private final AppointmentRepository appointmentRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;



    @Value("${frontEndUrl}")
    private String frontEndUrl;

    @Override
    @Transactional
    public AppointmentCreateResponseDto bookAppointment(AppointmentCreateRequestDto request) {

        if (request.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new InvalidAppointmentException("Appointment date cannot be in the past");
        }

        // 1. Find Doctor Availability
        DoctorAvailability availability =
                doctorAvailabilityRepository
                        .findById(request.getDoctorAvailabilityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Doctor availability not found"));

        // 2. Get Doctor
        Doctor doctor = availability.getDoctor();

        // 3. Get logged-in patient
        Patient patient = new Patient();
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            patient = patientRepository.findByUser_Id(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        }catch (Exception e){
            throw new RuntimeException("Unable to get logged-in patient: " + e.getMessage());
        }


        // 4. Validate appointment day
        if (!request.getAppointmentDate().getDayOfWeek().toString().equals(availability.getDayOfWeek().toString())) {
            throw new InvalidAppointmentException("Doctor is not available on this day");
        }

        // 5. Validate appointment time
        if (request.getAppointmentTime().isBefore(availability.getStartTime())
                || request.getAppointmentTime().isAfter(availability.getEndTime())) {

            throw new InvalidAppointmentException("Appointment time is outside doctor's availability");
        }


        //fetch Apointment
        Optional<Appointment> existingOpt =
                appointmentRepository.findActiveAppointment(
                        doctor.getDoctorId(),
                        request.getAppointmentDate(),
                        request.getAppointmentTime(),
                        AppointmentStatus.CONFIRMED.name(),
                        PaymentStatus.SUCCESS.name(),
                        AppointmentStatus.PENDING.name(),
                        PaymentStatus.PENDING.name(),
                        LocalDateTime.now()
                );

        if (existingOpt.isPresent()) {
            throw new DuplicateResourceException(
                    "This appointment slot is already booked or temporarily blocked"
            );
        }

        // 7. Create Appointment
        Appointment appointment = new Appointment();

        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        long totalMinutes = Duration.between(
                availability.getStartTime(),
                availability.getEndTime()
        ).toMinutes();

        long totalTokens = (int) (totalMinutes / doctor.getAvgConsultationTime());

        int tokenNumber = (int) (Duration.between
                (availability.getStartTime(), appointment.getAppointmentTime()).toMinutes())/ doctor.getAvgConsultationTime();


        if(tokenNumber<totalTokens){
            appointment.setTokenNo(tokenNumber+1);
        }else{
            throw new InvalidAppointmentException("Invalid appointment token");
        }

        appointment.setAppointmentStatus(AppointmentStatus.PENDING);
        appointment.setPaymentStatus(PaymentStatus.PENDING);
        appointment.setHoldExpiresAt(LocalDateTime.now().plusMinutes(5));

        // 8. Save Appointment
        try {
            Appointment savedAppointment = appointmentRepository.save(appointment);



        // 11. Create Response
        AppointmentCreateResponseDto response = new AppointmentCreateResponseDto();

        response.setAppointmentId(savedAppointment.getAppointmentId());
        response.setDoctorId(doctor.getDoctorId());
        response.setDoctorName(doctor.getUser().getName());
        response.setSpecialization(doctor.getSpecialization().getSpecializationName());
        response.setAppointmentDate(savedAppointment.getAppointmentDate());
        response.setAppointmentTime(savedAppointment.getAppointmentTime());
        response.setTokenNo(savedAppointment.getTokenNo());
        response.setTokenNo(savedAppointment.getTokenNo());
        response.setAppointmentStatus(savedAppointment.getAppointmentStatus());
        response.setPaymentStatus(savedAppointment.getPaymentStatus());
        return response;
        }catch (Exception e){
            throw  new RuntimeException("Error while saving appointment: " + e.getMessage());
        }

    }

    @Override
    @Transactional
    public AppointmentCancelResponseDto cancelAppointment(Long appointmentId) {

        // 1. Find appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        // 2. Get currently logged-in user's email
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 3. Check appointment belongs to current user
        if (!appointment.getPatient().getUser().getId().equals(user.getId())) {
            throw new UnAuthorisedException("You are not authorized to cancel this appointment");
        }

        // 4. Check appointment status
        if (appointment.getAppointmentStatus() != AppointmentStatus.CONFIRMED) {
            throw new InvalidAppointmentException("Only confirmed appointments can be cancelled");
        }

        // 5. Check payment status
        if (appointment.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new InvalidAppointmentException("Refund cannot be initiated because payment was not successful");
        }

        // 6. Cancel appointment
        appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
        appointment.setPaymentStatus(PaymentStatus.REFUND_PENDING);
        appointment.setHoldExpiresAt(null);
        Appointment savedAppointment = appointmentRepository.save(appointment);
// 11. Create Response
        AppointmentCancelResponseDto response = new AppointmentCancelResponseDto();

        response.setAppointmentId(savedAppointment.getAppointmentId());
        response.setDoctorId(appointment.getDoctor().getDoctorId());
        response.setDoctorName(appointment.getDoctor().getUser().getName());
        response.setSpecialization(appointment.getDoctor().getSpecialization().getSpecializationName());
        response.setAppointmentDate(savedAppointment.getAppointmentDate());
        response.setAppointmentTime(savedAppointment.getAppointmentTime());
        response.setTokenNo(savedAppointment.getTokenNo());
        response.setAppointmentStatus(savedAppointment.getAppointmentStatus());
        response.setPaymentStatus(savedAppointment.getPaymentStatus());
        return response;

    }

    @Override
    public List<ScheduledAppointmentResponseDto> scheduledAppointment(LocalDate date, LocalTime startTime, LocalTime endTime) {

        // Get currently logged-in user
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Find doctor using logged-in user's ID
        Doctor doctor = doctorRepository.findByUser_Id(user.getId()).orElseThrow(() ->
                        new ResourceNotFoundException("Doctor profile not found for current user"));

        Long doctorId = doctor.getDoctorId();

        // Find scheduled appointments
        List<ScheduledAppointmentProjection> appointments = appointmentRepository
                .findScheduledAppointments(doctorId, date, startTime, endTime);

        // Convert Projection -> Response DTO
        return appointments.stream().map(appointment -> {

                    ScheduledAppointmentResponseDto response = new ScheduledAppointmentResponseDto();
                    response.setAppointmentId(appointment.getAppointmentId());
                    response.setAppointmentDate(appointment.getAppointmentDate());
                    response.setAppointmentTime(appointment.getAppointmentTime());
                    response.setAppointmentStatus(AppointmentStatus.valueOf(appointment.getAppointmentStatus()));
                    response.setPaymentStatus(PaymentStatus.valueOf(appointment.getPaymentStatus()));
                    response.setPatientName(appointment.getPatientName());
                    response.setMobNumber(appointment.getMobNumber());
                    return response;
                }).toList();
    }

}
