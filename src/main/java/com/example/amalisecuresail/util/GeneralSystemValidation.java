package com.example.amalisecuresail.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.springframework.stereotype.Service;

/**
 * Contains all validations to be performed in the application
 */
@Service
public class GeneralSystemValidation {

    /**
     * Validates the password complexity.
     *
     * @param password The password to validate
     */
    public void validatePassword(String password) {
        if (password.length() < 8 || !password.matches("^(?=.*[0-9])(?=.*[a-zA-Z]).*$")) {
            throw new IllegalArgumentException("Invalid password. Password must be at least 8 characters long and contain both letters and numbers.");
        }
    }

    /**
     * Validates the format and validity of the phone number.
     *
     * @param phoneNumber The phone number to validate
     */
    public void validatePhoneNumber(String phoneNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber parsedPhoneNumber = phoneNumberUtil.parse(phoneNumber, "GH");
            if (!phoneNumberUtil.isValidNumber(parsedPhoneNumber)) {
                throw new IllegalArgumentException("Invalid phone number.");
            }
        } catch (NumberParseException e) {
            throw new IllegalArgumentException("Invalid phone number format.");
        }
    }

    /**
     * Validates the email format not whether email exist.
     *
     * @param email The password to validate
     */
    public void validateEmail(String email) {
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email is invalid (@ is missing)");
        }
        else if (!email.substring(0, email.indexOf("@")).matches("^[a-zA-Z0-9]+([_.-][a-zA-Z0-9]+)*$"))
            throw new IllegalArgumentException("Email is invalid (prefix format is incorrect)");
        else if (!email.substring(email.indexOf("@")+1).matches("^[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]{2,})+$"))
            throw new IllegalArgumentException("Email is invalid (domain format is incorrect)");
    }

}
