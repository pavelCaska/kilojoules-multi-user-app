package com.pc.kilojoules.controller;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;

@ControllerAdvice
public class EmptyToNullControllerAdvice {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new PropertyEditorSupport() {

            @Override
            public void setAsText(String text) {
                if (text.trim().isEmpty()) {
                    setValue(null);
                } else {
                    setValue(text);
                }
            }

            @Override
            public String getAsText() {
                return (getValue() == null) ? "" : getValue().toString();
            }
        });
    }
}