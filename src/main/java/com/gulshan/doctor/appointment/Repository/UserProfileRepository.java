package com.gulshan.doctor.appointment.Repository;

import com.gulshan.doctor.appointment.Entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile,Long> {
    Optional<UserProfile> findByUser_IdAndDeletedFalse(long userId);

    List<UserProfile> findByDeletedFalse();

}
