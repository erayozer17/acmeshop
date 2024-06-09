package com.erayoezer.acmeshop.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ModelAndView handleCustomException(CustomException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("status", ex.getStatus().value());
        modelAndView.addObject("message", ex.getMessage());
        modelAndView.addObject("isAuthenticated", ex.getAuthenticated());
        modelAndView.setStatus(ex.getStatus());
        return modelAndView;
    }
}
