package com.gulshan.doctor.appointment.ResposeDTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignInResponseDTO {

    private Long id;
    private String accessToken;
    private String refreshToken;


}
