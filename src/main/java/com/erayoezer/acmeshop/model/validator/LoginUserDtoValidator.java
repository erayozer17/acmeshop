package com.erayoezer.acmeshop.model.validator;

import com.erayoezer.acmeshop.model.dto.LoginUserDto;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class LoginUserDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return LoginUserDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LoginUserDto loginUserDto = (LoginUserDto) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");

        // Add more custom validation logic here
        // For example, check if the user exists and if the password is correct
        // if (!userService.isCredentialsValid(loginUserDto.getEmail(), loginUserDto.getPassword())) {
        //     errors.rejectValue("password", "Invalid.userForm.password");
        // }
    }
}
