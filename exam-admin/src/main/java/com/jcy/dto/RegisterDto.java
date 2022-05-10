package com.jcy.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterDto {

    @NotBlank
    private String username;

    @NotBlank
    @Length(min = 5, max = 20)
    private String password;

    private String trueName;

}
