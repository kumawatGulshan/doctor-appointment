package com.gulshan.doctor.appointment.Entity;

import com.gulshan.doctor.appointment.Entity.Enums.VideoConsultationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "video_consultation",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_video_consultation_appointment",
                        columnNames = "appointment_id"
                ),
                @UniqueConstraint(
                        name = "uk_video_consultation_room",
                        columnNames = "meeting_room_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class VideoConsultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "appointment_id",
            nullable = false,
            unique = true
    )
    private Appointment appointment;

    @Column(
            name = "meeting_room_id",
            nullable = false,
            unique = true,
            length = 36
    )
    private String meetingRoomId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VideoConsultationStatus status =
            VideoConsultationStatus.NOT_STARTED;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;
}