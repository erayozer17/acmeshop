package com.erayoezer.acmeshop.model.validator;

import com.erayoezer.acmeshop.model.dto.RegisterUserDto;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class RegisterUserDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return RegisterUserDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegisterUserDto registerUserDto = (RegisterUserDto) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fullName", "NotEmpty");

        // Add more custom validation logic here
        // For example, check if the email is already in use
        // if (userService.isEmailInUse(registerUserDto.getEmail())) {
        //     errors.rejectValue("email", "Duplicate.userForm.email");
        // }
    }
}
