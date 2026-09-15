package com.gulshan.doctor.appointment.Repository;


import com.gulshan.doctor.appointment.Entity.Session;
import com.gulshan.doctor.appointment.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session,Long> {
    List<Session> findUserSessionsByUser(User user);

    Optional<Session> findByRefreshToken(String refreshToken);
}
