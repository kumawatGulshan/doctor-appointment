package com.gulshan.doctor.appointment.Service;


import com.gulshan.doctor.appointment.Entity.Session;
import com.gulshan.doctor.appointment.Entity.User;
import com.gulshan.doctor.appointment.Repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final int sessionLimit = 2;

    public void generateNewSession(User user, String refreshToken){
        List<Session> userSessionList =  sessionRepository.findUserSessionsByUser(user);
        if(userSessionList.size() == sessionLimit){
            userSessionList.sort(Comparator.comparing(Session::getLastUsedAt));
            Session leastRecentlyUsedSession = userSessionList.getFirst();
            sessionRepository.delete(leastRecentlyUsedSession);
        }

        Session newSession = Session.builder()
                .refreshToken(refreshToken)
                .user(user)
                .lastUsedAt(LocalDateTime.now())
                .build();
        sessionRepository.save(newSession);
    }

    public void validateSession(String refreshToken){
        Session session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new SessionAuthenticationException("Session not found for this refreshToken : "+refreshToken));
        session.setLastUsedAt(LocalDateTime.now());
         Session savedSession = sessionRepository.save(session);
    }
}
