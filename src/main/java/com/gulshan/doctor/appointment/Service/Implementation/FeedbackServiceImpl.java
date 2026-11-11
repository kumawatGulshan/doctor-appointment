
package com.gulshan.doctor.appointment.Service.Implementation;

import com.gulshan.doctor.appointment.Entity.*;
import com.gulshan.doctor.appointment.Entity.Enums.AppointmentStatus;
import com.gulshan.doctor.appointment.Exceptions.DuplicateResourceException;
import com.gulshan.doctor.appointment.Exceptions.InvalidAppointmentException;
import com.gulshan.doctor.appointment.Exceptions.ResourceNotFoundException;
import com.gulshan.doctor.appointment.Exceptions.UnAuthorisedException;
import com.gulshan.doctor.appointment.Repository.AppointmentRepository;
import com.gulshan.doctor.appointment.Repository.FeedbackRepository;
import com.gulshan.doctor.appointment.Repository.PatientRepository;
import com.gulshan.doctor.appointment.RequestDTO.FeedbackRequestDto;
import com.gulshan.doctor.appointment.ResposeDTO.FeedbackResponseDto;
import com.gulshan.doctor.appointment.Service.Interface.FeedbackServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackServiceInterface {

    private final FeedbackRepository feedbackRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    private Patient getCurrentPatient() {
        User user = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        return patientRepository.findByUser_Id(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient profile not found"));
    }

    @Override
    @Transactional
    public FeedbackResponseDto createFeedback(
            Long appointmentId,
            FeedbackRequestDto request) {

        Patient patient = getCurrentPatient();

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Appointment not found"));

        if (!appointment.getPatient().getPatientId()
                .equals(patient.getPatientId())) {
            throw new UnAuthorisedException(
                    "You can only submit feedback for your own appointments");
        }

        if (appointment.getAppointmentStatus() != AppointmentStatus.COMPLETED) {
            throw new InvalidAppointmentException(
                    "Feedback can only be submitted after consultation is completed");
        }

        if (feedbackRepository.existsByAppointment_AppointmentId(appointmentId)) {
            throw new DuplicateResourceException(
                    "Feedback already exists for this appointment");
        }

        Feedback feedback = new Feedback();
        feedback.setAppointment(appointment);
        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());

        Feedback savedFeedback = feedbackRepository.save(feedback);

        return mapToResponse(savedFeedback);
    }

    @Override
    @Transactional
    public FeedbackResponseDto updateFeedback(
            Long feedbackId,
            FeedbackRequestDto request) {

        Patient patient = getCurrentPatient();

        Feedback feedback = feedbackRepository
                .findByFeedbackIdAndAppointment_Patient_PatientId(
                        feedbackId, patient.getPatientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Feedback not found"));

        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());

        return mapToResponse(feedbackRepository.save(feedback));
    }

    @Override
    @Transactional
    public void deleteFeedback(Long feedbackId) {

        Patient patient = getCurrentPatient();

        Feedback feedback = feedbackRepository
                .findByFeedbackIdAndAppointment_Patient_PatientId(
                        feedbackId, patient.getPatientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Feedback not found"));

        feedbackRepository.delete(feedback);
    }

    @Override
    @Transactional
    public List<FeedbackResponseDto> getDoctorFeedback(Long doctorId) {

        return feedbackRepository
                .findByAppointment_Doctor_DoctorIdOrderByCreatedAtDesc(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public Double getDoctorAverageRating(Long doctorId) {

        Double averageRating =
                feedbackRepository.findAverageRatingByDoctorId(doctorId);

        return averageRating == null ? 0.0 : averageRating;
    }

    private FeedbackResponseDto mapToResponse(Feedback feedback) {

        Appointment appointment = feedback.getAppointment();
        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();

        FeedbackResponseDto response = new FeedbackResponseDto();

        response.setFeedbackId(feedback.getFeedbackId());
        response.setAppointmentId(appointment.getAppointmentId());

        response.setPatientId(patient.getPatientId());
        response.setPatientName(patient.getUser().getName());

        response.setDoctorId(doctor.getDoctorId());
        response.setDoctorName(doctor.getUser().getName());

        response.setRating(feedback.getRating());
        response.setComment(feedback.getComment());
        response.setCreatedAt(feedback.getCreatedAt());
        response.setUpdatedAt(feedback.getUpdatedAt());

        return response;
    }
}
