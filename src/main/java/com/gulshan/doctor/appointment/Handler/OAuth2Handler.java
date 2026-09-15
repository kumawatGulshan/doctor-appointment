package com.gulshan.doctor.appointment.Handler;


import com.gulshan.doctor.appointment.Entity.User;
import com.gulshan.doctor.appointment.Service.JwtService;
import com.gulshan.doctor.appointment.Service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2Handler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtService jwtService;

    @Value("${deploy.env}")
    private String deployEnv;

    @Value("${frontEndUrl}")
    private String frontEndUrl;

    @Override
   public void onAuthenticationSuccess(HttpServletRequest request,HttpServletResponse response,
                                       Authentication authentication) throws IOException, ServletException{
       OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication; //Convert authentication to OAuth2 authentication
       DefaultOAuth2User oAuth2User =  (DefaultOAuth2User) oAuth2AuthenticationToken.getPrincipal(); //Get the authenticated user

        String email = oAuth2User.getAttributes().get("email").toString();
//        logger.info(email);
        User user =  userService.GetUserByEmail(email);
        if (user == null){
            User newUser = User.builder()
                    .name(oAuth2User.getAttributes().get("name").toString())
                    .email(email)
                    .build();
            user = userService.addUser(newUser);
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Cookie cookie = new Cookie("refreshToken",refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure("production".equals(deployEnv));
        response.addCookie(cookie);

        getRedirectStrategy().sendRedirect(request,response,frontEndUrl+"/home.html?token="+accessToken);
//        response.sendRedirect(frontEndUrl+"/home.html?token="+accessToken);



   }
}
