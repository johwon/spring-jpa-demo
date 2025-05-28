package com.example.jpa.user.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInputPassword {
    
    @NotBlank(message = "현재 비밀번호는 필수 항목입니다.")
    private String password;

    @NotBlank(message = "신규 비밀번호는 필수 항목입니다.")
    @Size(min = 4, max = 20, message = "4~20자 사이로 입력해 주세요.")
    private String newPassword;
}
