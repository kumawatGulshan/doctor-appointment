
package com.gulshan.doctor.appointment.Service.Implementation;

import com.gulshan.doctor.appointment.Entity.*;
import com.gulshan.doctor.appointment.Entity.Enums.AppointmentStatus;
import com.gulshan.doctor.appointment.Entity.Enums.AppointmentType;
import com.gulshan.doctor.appointment.Entity.Enums.DoctorClinicStatus;
import com.gulshan.doctor.appointment.Entity.Enums.PaymentStatus;
import com.gulshan.doctor.appointment.Entity.Projections.ScheduledAppointmentProjection;
import com.gulshan.doctor.appointment.Exceptions.*;
import com.gulshan.doctor.appointment.Repository.*;
import com.gulshan.doctor.appointment.RequestDTO.AppointmentCreateRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.AppointmentCancelResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.AppointmentCreateResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.AppointmentStatusResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.ScheduledAppointmentResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.AppointmentServiceInterface;
import com.gulshan.doctor.appointment.Service.Interface.VideoConsultationServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    private final DoctorClinicRepository doctorClinicRepository;
    private final VideoConsultationServiceInterface videoConsultationServiceInterface;

    @Override
    @Transactional
    public AppointmentCreateResponseDto bookAppointment(
            AppointmentCreateRequestDto request) {

        // 1. Capture current time
        LocalDateTime now = LocalDateTime.now();

        // 2. Validate appointment date
        if (request.getAppointmentDate().isBefore(now.toLocalDate())) {
            throw new InvalidAppointmentException(
                    "Appointment date cannot be in the past"
            );
        }

        // 3. Find Doctor Availability
        DoctorAvailability availability =
                doctorAvailabilityRepository
                        .findById(request.getDoctorAvailabilityId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Doctor availability not found"
                                ));

        // 4. Get Doctor
        Doctor doctor = availability.getDoctor();

        // 5. Validate appointment type
        AppointmentType appointmentType = request.getAppointmentType();

        if (appointmentType == null) {
            throw new InvalidAppointmentException(
                    "Appointment type is required"
            );
        }

        // 6. Validate consultation preference
        // Independent doctor: check Doctor entity preferences
        if (availability.getClinic() == null) {

            if (appointmentType == AppointmentType.PHYSICAL
                    && !Boolean.TRUE.equals(
                    doctor.getPhysicalConsultationEnabled())) {

                throw new InvalidAppointmentException(
                        "This doctor does not provide physical consultations"
                );
            }

            if (appointmentType == AppointmentType.VIDEO
                    && !Boolean.TRUE.equals(
                    doctor.getVideoConsultationEnabled())) {

                throw new InvalidAppointmentException(
                        "This doctor does not provide video consultations"
                );
            }

        } else {

            // Clinic-based doctor: check DoctorClinic preferences
            DoctorClinic doctorClinic = doctorClinicRepository
                    .findByDoctor_DoctorIdAndClinic_IdAndAssociationStatus(
                            doctor.getDoctorId(),
                            availability.getClinic().getId(),
                            DoctorClinicStatus.ACTIVE
                    )
                    .orElseThrow(() ->
                            new InvalidAppointmentException(
                                    "Doctor does not have an active association with this clinic"
                            )
                    );

            if (appointmentType == AppointmentType.PHYSICAL
                    && !Boolean.TRUE.equals(
                    doctorClinic.getPhysicalConsultationEnabled())) {

                throw new InvalidAppointmentException(
                        "This clinic does not allow physical consultations with this doctor"
                );
            }

            if (appointmentType == AppointmentType.VIDEO
                    && !Boolean.TRUE.equals(
                    doctorClinic.getVideoConsultationEnabled())) {

                throw new InvalidAppointmentException(
                        "This clinic does not allow video consultations with this doctor"
                );
            }
        }

        // 7. Validate consultation duration
        if (doctor.getAvgConsultationTime() == null
                || doctor.getAvgConsultationTime() <= 0) {

            throw new InvalidAppointmentException(
                    "Doctor consultation duration is not configured correctly"
            );
        }

        int duration = doctor.getAvgConsultationTime();

        // 8. Get logged-in patient
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Patient patient = patientRepository.findByUser_Id(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient not found"));

        // 9. Validate appointment day
        if (!request.getAppointmentDate().getDayOfWeek().name()
                .equals(availability.getDayOfWeek().name())) {

            throw new InvalidAppointmentException(
                    "Doctor is not available on this day"
            );
        }

        // 10. Calculate appointment slot end time
        LocalTime appointmentStart = request.getAppointmentTime();

        LocalTime appointmentEnd =
                appointmentStart.plusMinutes(duration);

        // 11. Validate complete slot is within availability
        if (appointmentStart.isBefore(availability.getStartTime())
                || appointmentEnd.isAfter(availability.getEndTime())) {

            throw new InvalidAppointmentException(
                    "Appointment slot is outside doctor's availability"
            );
        }

        // 12. Validate selected time matches a generated slot
        long minutesFromStart = Duration.between(
                availability.getStartTime(),
                appointmentStart
        ).toMinutes();

        if (minutesFromStart % duration != 0) {

            throw new InvalidAppointmentException(
                    "Please select a valid appointment slot"
            );
        }

        // 13. Validate appointment is in the future
        LocalDateTime appointmentDateTime =
                request.getAppointmentDate().atTime(appointmentStart);

        if (!appointmentDateTime.isAfter(now)) {

            throw new InvalidAppointmentException(
                    "Appointment time must be in the future"
            );
        }

        // 14. Check whether the selected slot is already booked
        Optional<Appointment> existingOpt =
                appointmentRepository.findActiveAppointment(
                        doctor.getDoctorId(),
                        request.getAppointmentDate(),
                        appointmentStart,
                        AppointmentStatus.CONFIRMED.name(),
                        PaymentStatus.SUCCESS.name(),
                        AppointmentStatus.PENDING.name(),
                        PaymentStatus.PENDING.name(),
                        now
                );

        if (existingOpt.isPresent()) {

            throw new DuplicateResourceException(
                    "This appointment slot is already booked or temporarily blocked"
            );
        }

        // 15. Calculate token number from slot position
        int tokenNumber = (int) (minutesFromStart / duration) + 1;

        // 16. Create Appointment
        Appointment appointment = new Appointment();

        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setClinic(availability.getClinic());

        // Set consultation type
        appointment.setAppointmentType(appointmentType);

        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(appointmentStart);
        appointment.setTokenNo(tokenNumber);

        appointment.setAppointmentStatus(AppointmentStatus.PENDING);
        appointment.setPaymentStatus(PaymentStatus.PENDING);
        appointment.setHoldExpiresAt(now.plusMinutes(5));

        // 17. Save Appointment
        Appointment savedAppointment =
                appointmentRepository.save(appointment);


        // 18. Create Video Consultation Meeting if appointment type is VIDEO
        if (savedAppointment.getAppointmentType() == AppointmentType.VIDEO) {

            videoConsultationServiceInterface.createMeetingForAppointment(
                    savedAppointment
            );
        }

        // 19. Create Response
        AppointmentCreateResponseDto response =
                new AppointmentCreateResponseDto();

        response.setAppointmentId(savedAppointment.getAppointmentId());
        response.setDoctorId(doctor.getDoctorId());
        response.setDoctorName(doctor.getUser().getName());

        response.setSpecialization(
                doctor.getSpecialization().getSpecializationName()
        );

        response.setAppointmentDate(savedAppointment.getAppointmentDate());
        response.setAppointmentTime(savedAppointment.getAppointmentTime());
        response.setTokenNo(savedAppointment.getTokenNo());

        response.setAppointmentStatus(
                savedAppointment.getAppointmentStatus()
        );

        response.setPaymentStatus(
                savedAppointment.getPaymentStatus()
        );

        response.setAppointmentType(
                savedAppointment.getAppointmentType()
        );

        return response;
    }

    @Override
    @Transactional
    public AppointmentCancelResponseDto cancelAppointment(Long appointmentId) {

        // 1. Find appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found with id: " + appointmentId
                        ));

        // 2. Get currently logged-in user
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // 3. Check appointment belongs to current user
        if (!appointment.getPatient().getUser().getId().equals(user.getId())) {

            throw new UnAuthorisedException(
                    "You are not authorized to cancel this appointment"
            );
        }

        // 4. Check appointment status
        if (appointment.getAppointmentStatus() != AppointmentStatus.CONFIRMED) {

            throw new InvalidAppointmentException(
                    "Only confirmed appointments can be cancelled"
            );
        }

        // 5. Check payment status
        if (appointment.getPaymentStatus() != PaymentStatus.SUCCESS) {

            throw new InvalidAppointmentException(
                    "Refund cannot be initiated because payment was not successful"
            );
        }

        // 6. Cancel appointment
        appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
        appointment.setPaymentStatus(PaymentStatus.REFUND_PENDING);
        appointment.setHoldExpiresAt(null);

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        // 7. Create Response
        AppointmentCancelResponseDto response =
                new AppointmentCancelResponseDto();

        response.setAppointmentId(savedAppointment.getAppointmentId());
        response.setDoctorId(appointment.getDoctor().getDoctorId());
        response.setDoctorName(appointment.getDoctor().getUser().getName());

        response.setSpecialization(
                appointment.getDoctor().getSpecialization().getSpecializationName()
        );

        response.setAppointmentDate(savedAppointment.getAppointmentDate());
        response.setAppointmentTime(savedAppointment.getAppointmentTime());
        response.setTokenNo(savedAppointment.getTokenNo());

        response.setAppointmentStatus(
                savedAppointment.getAppointmentStatus()
        );

        response.setPaymentStatus(
                savedAppointment.getPaymentStatus()
        );

        // Added: return consultation type
        response.setAppointmentType(
                savedAppointment.getAppointmentType()
        );

        return response;
    }

    @Override
    public List<ScheduledAppointmentResponseDto> scheduledAppointment(
            Long clinicId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Doctor doctor = doctorRepository.findByUser_Id(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor profile not found for current user"
                        ));

        Long doctorId = doctor.getDoctorId();

        // Fetch appointments based on clinicId
        List<ScheduledAppointmentProjection> appointments;

        if (clinicId != null) {

            // Validate active doctor-clinic association
            doctorClinicRepository
                    .findByDoctor_DoctorIdAndClinic_IdAndAssociationStatus(
                            doctorId,
                            clinicId,
                            DoctorClinicStatus.ACTIVE
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Active doctor-clinic association not found"
                            ));

            appointments = appointmentRepository
                    .findScheduledAppointmentsByClinic(
                            doctorId,
                            clinicId,
                            date,
                            startTime,
                            endTime
                    );

        } else {

            // Individual doctor schedule
            appointments = appointmentRepository.findScheduledAppointments(
                    doctorId,
                    date,
                    startTime,
                    endTime
            );
        }

        return appointments.stream().map(appointment -> {

            ScheduledAppointmentResponseDto response =
                    new ScheduledAppointmentResponseDto();

            response.setAppointmentId(appointment.getAppointmentId());
            response.setAppointmentDate(appointment.getAppointmentDate());
            response.setAppointmentTime(appointment.getAppointmentTime());

            response.setAppointmentStatus(
                    AppointmentStatus.valueOf(appointment.getAppointmentStatus())
            );

            response.setPaymentStatus(
                    PaymentStatus.valueOf(appointment.getPaymentStatus())
            );

            // Added: map consultation type from projection
            response.setAppointmentType(
                    AppointmentType.valueOf(appointment.getAppointmentType())
            );

            response.setPatientName(appointment.getPatientName());
            response.setMobNumber(appointment.getMobNumber());

            return response;

        }).toList();
    }


    @Override
    @Transactional
    public AppointmentStatusResponseDto startPhysicalConsultation(
            Long appointmentId) {

        // 1. Find appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found"
                        )
                );

        // 2. Get logged-in user
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // 3. Find logged-in doctor's profile
        Doctor doctor = doctorRepository.findByUser_Id(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor profile not found"
                        )
                );

        // 4. Verify assigned doctor
        if (!appointment.getDoctor().getDoctorId()
                .equals(doctor.getDoctorId())) {

            throw new UnAuthorisedException(
                    "You are not authorized to start this consultation"
            );
        }

        // 5. Verify appointment type
        if (appointment.getAppointmentType() != AppointmentType.PHYSICAL) {

            throw new InvalidAppointmentException(
                    "This API is only for physical appointments"
            );
        }

        // 6. Verify payment
        if (appointment.getPaymentStatus() != PaymentStatus.SUCCESS) {

            throw new InvalidAppointmentException(
                    "Only successfully paid appointments can be started"
            );
        }

        // 7. Verify appointment status
        if (appointment.getAppointmentStatus()
                != AppointmentStatus.CONFIRMED) {

            throw new InvalidAppointmentException(
                    "Only confirmed appointments can be started"
            );
        }

        // 8. Start consultation
        appointment.setAppointmentStatus(AppointmentStatus.IN_PROGRESS);

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        // 9. Prepare response
        return mapToAppointmentStatusResponse(savedAppointment);
    }

    @Override
    @Transactional
    public AppointmentStatusResponseDto endPhysicalConsultation(
            Long appointmentId) {

        // 1. Find appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found"
                        )
                );

        // 2. Get logged-in user
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // 3. Find logged-in doctor's profile
        Doctor doctor = doctorRepository.findByUser_Id(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor profile not found"
                        )
                );

        // 4. Verify assigned doctor
        if (!appointment.getDoctor().getDoctorId()
                .equals(doctor.getDoctorId())) {

            throw new UnAuthorisedException(
                    "You are not authorized to end this consultation"
            );
        }

        // 5. Verify appointment type
        if (appointment.getAppointmentType() != AppointmentType.PHYSICAL) {

            throw new InvalidAppointmentException(
                    "This API is only for physical appointments"
            );
        }

        // 6. Verify payment
        if (appointment.getPaymentStatus() != PaymentStatus.SUCCESS) {

            throw new InvalidAppointmentException(
                    "Only successfully paid appointments can be completed"
            );
        }

        // 7. Verify appointment status
        if (appointment.getAppointmentStatus()
                != AppointmentStatus.IN_PROGRESS) {

            throw new InvalidAppointmentException(
                    "Only appointments in progress can be completed"
            );
        }

        // 8. Complete consultation
        appointment.setAppointmentStatus(AppointmentStatus.COMPLETED);

        Appointment savedAppointment =
                appointmentRepository.save(appointment);

        // 9. Prepare response
        return mapToAppointmentStatusResponse(savedAppointment);
    }

    private AppointmentStatusResponseDto mapToAppointmentStatusResponse(
            Appointment appointment) {

        AppointmentStatusResponseDto response =
                new AppointmentStatusResponseDto();

        response.setAppointmentId(appointment.getAppointmentId());
        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setAppointmentTime(appointment.getAppointmentTime());
        response.setAppointmentType(appointment.getAppointmentType());
        response.setAppointmentStatus(appointment.getAppointmentStatus());
        response.setPaymentStatus(appointment.getPaymentStatus());

        return response;
    }
}



//stripe listen --api-key sk_test_51UK4jILjL6HkLle6GbR00ABksobuc --events checkout.session.completed --forward-to http://localhost:8080/payment/webhook