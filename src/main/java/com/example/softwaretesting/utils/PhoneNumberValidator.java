package com.example.softwaretesting.utils;

import org.springframework.stereotype.Component;

import java.util.function.Predicate;


@Component
public class PhoneNumberValidator implements Predicate<String> {

    @Override
    public boolean test(String s) {
        return s.startsWith("+44") && s.length() == 13;
    }
}
