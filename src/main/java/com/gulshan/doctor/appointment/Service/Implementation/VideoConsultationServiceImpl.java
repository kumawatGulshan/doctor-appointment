
package com.gulshan.doctor.appointment.Service.Implementation;

import com.gulshan.doctor.appointment.Entity.Appointment;
import com.gulshan.doctor.appointment.Entity.User;
import com.gulshan.doctor.appointment.Entity.VideoConsultation;

import com.gulshan.doctor.appointment.Entity.Enums.AppointmentStatus;
import com.gulshan.doctor.appointment.Entity.Enums.AppointmentType;
import com.gulshan.doctor.appointment.Entity.Enums.PaymentStatus;
import com.gulshan.doctor.appointment.Entity.Enums.VideoConsultationStatus;

import com.gulshan.doctor.appointment.Exceptions.ResourceNotFoundException;
import com.gulshan.doctor.appointment.Exceptions.VideoConsultationException;

import com.gulshan.doctor.appointment.Repository.AppointmentRepository;
import com.gulshan.doctor.appointment.Repository.VideoConsultationRepository;

import com.gulshan.doctor.appointment.ResposeDTO.VideoConsultationJoinResponseDto;
import com.gulshan.doctor.appointment.ResposeDTO.VideoConsultationResponseDto;

import com.gulshan.doctor.appointment.Service.Interface.VideoConsultationServiceInterface;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoConsultationServiceImpl
        implements VideoConsultationServiceInterface {

    private final AppointmentRepository appointmentRepository;

    private final VideoConsultationRepository videoConsultationRepository;

    private final JaasJwtService jaasJwtService;

    @Value("${jaas.app-id}")
    private String jaasAppId;


    // --------------------------------------------------
    // Create meeting for video appointment
    // --------------------------------------------------

    @Override
    @Transactional
    public void createMeetingForAppointment(Appointment appointment) {

        if (appointment.getAppointmentType() != AppointmentType.VIDEO) {
            return;
        }

        if (videoConsultationRepository.existsByAppointment_AppointmentId(
                appointment.getAppointmentId())) {
            return;
        }

        VideoConsultation consultation = new VideoConsultation();

        consultation.setAppointment(appointment);
        consultation.setMeetingRoomId(UUID.randomUUID().toString());
        consultation.setStatus(VideoConsultationStatus.NOT_STARTED);

        videoConsultationRepository.save(consultation);
    }


    // --------------------------------------------------
    // Get meeting details
    // Meeting URL is NOT returned here
    // --------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public VideoConsultationResponseDto getMeetingDetails(Long appointmentId) {

        Appointment appointment = getAppointment(appointmentId);

        validateParticipant(appointment);
        validateVideoAppointment(appointment);
        validateAppointmentEligibility(appointment);

        VideoConsultation consultation = getConsultation(appointmentId);

        validateNotCompleted(consultation);

        // No validateJoinTime() here.
        // Users can view meeting details before the joining window.

        return mapToResponse(consultation);
    }


    // --------------------------------------------------
    // Join consultation
    // Meeting URL is returned here
    // --------------------------------------------------

    @Override
    @Transactional
    public VideoConsultationJoinResponseDto joinConsultation(Long appointmentId) {

        Appointment appointment = getAppointment(appointmentId);

        validateParticipant(appointment);
        validateVideoAppointment(appointment);
        validateAppointmentEligibility(appointment);
        validateJoinTime(appointment);

        VideoConsultation consultation = getConsultation(appointmentId);

        validateNotCompleted(consultation);

        if (consultation.getStatus() == VideoConsultationStatus.NOT_STARTED) {

            consultation.setStatus(VideoConsultationStatus.IN_PROGRESS);
            consultation.setStartedAt(LocalDateTime.now());

            videoConsultationRepository.save(consultation);
        }

        // Synchronize Appointment status with VideoConsultation status
        if (appointment.getAppointmentStatus() == AppointmentStatus.CONFIRMED) {

            appointment.setAppointmentStatus(AppointmentStatus.IN_PROGRESS);

            appointmentRepository.save(appointment);
        }

        User currentUser = getCurrentUser();

        Long doctorUserId = appointment.getDoctor()
                .getUser()
                .getId();

        boolean isDoctor = doctorUserId.equals(currentUser.getId());

        String token = jaasJwtService.generateToken(
                currentUser,
                consultation.getMeetingRoomId(),
                isDoctor
        );

        return mapToJoinResponse(consultation, token);
    }
    // --------------------------------------------------
    // End consultation
    // Only assigned doctor can end it
    // --------------------------------------------------

    @Override
    @Transactional
    public VideoConsultationResponseDto endConsultation(Long appointmentId) {

        Appointment appointment = getAppointment(appointmentId);

        validateVideoAppointment(appointment);

        User currentUser = getCurrentUser();

        Long doctorUserId = appointment.getDoctor()
                .getUser()
                .getId();

        if (!doctorUserId.equals(currentUser.getId())) {

            throw new VideoConsultationException(
                    HttpStatus.FORBIDDEN,
                    "Only the assigned doctor can end this consultation"
            );
        }

        VideoConsultation consultation = getConsultation(appointmentId);

        if (consultation.getStatus() == VideoConsultationStatus.COMPLETED) {

            throw new VideoConsultationException(
                    HttpStatus.CONFLICT,
                    "Consultation has already been completed"
            );
        }

        if (consultation.getStatus() != VideoConsultationStatus.IN_PROGRESS) {

            throw new VideoConsultationException(
                    HttpStatus.CONFLICT,
                    "Consultation has not started yet"
            );
        }

        // NEW: Validate scheduled appointment start time
        validateAppointmentStartTime(appointment);

        consultation.setStatus(VideoConsultationStatus.COMPLETED);
        consultation.setEndedAt(LocalDateTime.now());

        appointment.setAppointmentStatus(AppointmentStatus.COMPLETED);

        consultation = videoConsultationRepository.save(consultation);

        appointmentRepository.save(appointment);

        return mapToResponse(consultation);
    }

    // --------------------------------------------------
    // Helper methods
    // --------------------------------------------------

    private Appointment getAppointment(Long appointmentId) {

        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with ID: " + appointmentId
                ));
    }


    private VideoConsultation getConsultation(Long appointmentId) {

        return videoConsultationRepository
                .findByAppointment_AppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Video consultation not found for appointment ID: "
                                + appointmentId
                ));
    }


    private User getCurrentUser() {

        var authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof User)) {

            throw new VideoConsultationException(
                    HttpStatus.UNAUTHORIZED,
                    "User is not authenticated"
            );
        }

        return (User) authentication.getPrincipal();
    }


    private void validateParticipant(Appointment appointment) {

        User currentUser = getCurrentUser();

        Long currentUserId = currentUser.getId();

        Long doctorUserId = appointment.getDoctor()
                .getUser()
                .getId();

        Long patientUserId = appointment.getPatient()
                .getUser()
                .getId();

        boolean isDoctor = doctorUserId.equals(currentUserId);
        boolean isPatient = patientUserId.equals(currentUserId);

        if (!isDoctor && !isPatient) {

            throw new VideoConsultationException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to access this consultation"
            );
        }
    }


    private void validateVideoAppointment(Appointment appointment) {

        if (appointment.getAppointmentType() != AppointmentType.VIDEO) {

            throw new VideoConsultationException(
                    HttpStatus.BAD_REQUEST,
                    "This is not a video consultation appointment"
            );
        }
    }


    private void validateAppointmentEligibility(Appointment appointment) {

        if (appointment.getAppointmentStatus() != AppointmentStatus.CONFIRMED
                && appointment.getAppointmentStatus() != AppointmentStatus.IN_PROGRESS) {

            throw new VideoConsultationException(
                    HttpStatus.CONFLICT,
                    "Appointment is not confirmed or in progress"
            );
        }

        if (appointment.getPaymentStatus() != PaymentStatus.SUCCESS) {

            throw new VideoConsultationException(
                    HttpStatus.CONFLICT,
                    "Payment has not been completed"
            );
        }
    }
    private void validateNotCompleted(VideoConsultation consultation) {

        if (consultation.getStatus() == VideoConsultationStatus.COMPLETED) {

            throw new VideoConsultationException(
                    HttpStatus.CONFLICT,
                    "This consultation has already ended"
            );
        }
    }


    /*
     * Participants can join 10 minutes before appointment time.
     * avgConsultationTime is not an automatic ending deadline.
     */

    private void validateJoinTime(Appointment appointment) {

        LocalDateTime appointmentDateTime = LocalDateTime.of(
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime()
        );

        LocalDateTime allowedJoinTime =
                appointmentDateTime.minusMinutes(10);

        if (LocalDateTime.now().isBefore(allowedJoinTime)) {

            throw new VideoConsultationException(
                    HttpStatus.FORBIDDEN,
                    "You can join the consultation only 10 minutes before the appointment"
            );
        }
    }


    // --------------------------------------------------
    // Response mapping WITHOUT meeting URL
    // --------------------------------------------------

    private VideoConsultationResponseDto mapToResponse(
            VideoConsultation consultation) {

        Appointment appointment = consultation.getAppointment();

        return new VideoConsultationResponseDto(
                appointment.getAppointmentId(),
                consultation.getMeetingRoomId(),
                consultation.getStatus(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                consultation.getStartedAt(),
                consultation.getEndedAt()
        );
    }


    // --------------------------------------------------
    // Response mapping WITH meeting URL
    // --------------------------------------------------

    private VideoConsultationJoinResponseDto mapToJoinResponse(
            VideoConsultation consultation,
            String token) {

        Appointment appointment = consultation.getAppointment();

        String meetingUrl = "https://8x8.vc/"
                + jaasAppId
                + "/"
                + consultation.getMeetingRoomId();

        return new VideoConsultationJoinResponseDto(
                appointment.getAppointmentId(),
                consultation.getMeetingRoomId(),
                meetingUrl,
                token,
                consultation.getStatus(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                consultation.getStartedAt(),
                consultation.getEndedAt()
        );
    }


    private void validateAppointmentStartTime(Appointment appointment) {

        LocalDateTime appointmentDateTime = LocalDateTime.of(
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime()
        );

        if (LocalDateTime.now().isBefore(appointmentDateTime)) {

            throw new VideoConsultationException(
                    HttpStatus.FORBIDDEN,
                    "You cannot end the consultation before the scheduled appointment start time"
            );
        }
    }
}
