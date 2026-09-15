package com.gulshan.doctor.appointment.Controller;


import com.gulshan.doctor.appointment.RequestDTO.SignInRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.SignUpRequestDTO;
import com.gulshan.doctor.appointment.ResposeDTO.SignInResponseDTO;
import com.gulshan.doctor.appointment.ResposeDTO.SignUpResponseDTO;
import com.gulshan.doctor.appointment.Service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {



    private final AuthService authService;

    @Value("${deploy.env}")
    private String deployEnv;

    @PostMapping("/register")
    public ResponseEntity<SignUpResponseDTO> signUp(@RequestBody @Valid SignUpRequestDTO signUpRequestDTO){
        SignUpResponseDTO signUpResponseDTO =  authService.signUp(signUpRequestDTO);
        return ResponseEntity.ok(signUpResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<SignInResponseDTO> login(@RequestBody  @Valid SignInRequestDto signInRequestDto, HttpServletRequest request, HttpServletResponse response){
        SignInResponseDTO signInResponseDTO =  authService.login(signInRequestDto);

        Cookie cookie = new Cookie("refreshToken",signInResponseDTO.getRefreshToken());
        cookie.setHttpOnly(true);  //JavaScript cannot read the cookie.
        cookie.setSecure("production".equals(deployEnv));  //cookie is sent only over HTTPS.
        response.addCookie(cookie);
        return ResponseEntity.ok(signInResponseDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<SignInResponseDTO> refreshToken(HttpServletRequest request){
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("Refresh token not found inside the Cookies."));
        SignInResponseDTO signInResponseDTO = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(signInResponseDTO);
    }

}
