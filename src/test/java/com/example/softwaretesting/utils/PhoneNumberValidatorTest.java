package com.example.softwaretesting.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PhoneNumberValidatorTest {

    private PhoneNumberValidator underTest;
    @BeforeEach
    void setUp() {
        this.underTest = new PhoneNumberValidator();
    }

    @Test
    void itShouldValidatorTest() {
        //Given
        String phoneNumber = "+447000000000";
        //When
        boolean isValid = underTest.test(phoneNumber);
        //Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should fail when length is bigger than 13")
    void itShouldValidatorWhenIncorrectAndHasLengthBiggerThan13() {
        //Given
        String phoneNumber = "+4470000000090";
        //When
        boolean isValid = underTest.test(phoneNumber);
        //Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should fail when does not start +")
    void itShouldValidatorWhenDoesNotStartPlusSign() {
        //Given
        String phoneNumber = "4470000000";
        //When
        boolean isValid = underTest.test(phoneNumber);
        //Then
        assertThat(isValid).isFalse();
    }
}
