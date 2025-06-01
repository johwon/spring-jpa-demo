package com.example.jpa.user.model;


import lombok.*;

import javax.validation.constraints.NotBlank;

@ToString
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLogin {

    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
