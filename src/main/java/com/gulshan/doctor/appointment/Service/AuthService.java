package com.gulshan.doctor.appointment.Service;


import com.gulshan.doctor.appointment.Entity.User;
import com.gulshan.doctor.appointment.Exceptions.DuplicateResourceException;
import com.gulshan.doctor.appointment.Repository.UserRepository;
import com.gulshan.doctor.appointment.RequestDTO.SignInRequestDto;
import com.gulshan.doctor.appointment.RequestDTO.SignUpRequestDTO;
import com.gulshan.doctor.appointment.ResposeDTO.SignInResponseDTO;
import com.gulshan.doctor.appointment.ResposeDTO.SignUpResponseDTO;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final SessionService sessionService;

    public SignUpResponseDTO signUp(SignUpRequestDTO signUpRequestDTO) {
        Optional<User> user = userRepository.findByEmail(signUpRequestDTO.getEmail());
        if(user.isPresent()){
            throw new DuplicateResourceException("User already exist with email " + signUpRequestDTO.getEmail());
        }
        User userToBeCreated = modelMapper.map(signUpRequestDTO,User.class);
        userToBeCreated.setPassword(passwordEncoder.encode(userToBeCreated.getPassword()));
        User createdUser =  userRepository.save(userToBeCreated);

        SignUpResponseDTO signUpResponseDTO = new SignUpResponseDTO();
        signUpResponseDTO.setMassage("user created successfully.");
        signUpResponseDTO.setEmail(createdUser.getEmail());
        signUpResponseDTO.setName(createdUser.getName());
        return signUpResponseDTO;
    }


    public SignInResponseDTO login(SignInRequestDto signInRequestDto) {
 
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequestDto.getEmail(),signInRequestDto.getPassword())
        );

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        sessionService.generateNewSession(user,refreshToken);

        return new SignInResponseDTO(user.getId(),accessToken,refreshToken);
    }

    public SignInResponseDTO refreshToken(String refreshToken) {
        sessionService.validateSession(refreshToken);
        Long userId = jwtService.getUserId(refreshToken);
        User user = userService.getUserById(userId);
        String accessToken = jwtService.generateAccessToken(user);
        return new SignInResponseDTO(userId,accessToken,refreshToken);
    }
}
