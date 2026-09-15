package com.gulshan.doctor.appointment.ResposeDTO;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponseDTO {
    private String massage;
    private String name;
    private String email;

}
