package com.vahor.users_back;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordValidator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String hashedPassword = "$2a$12$vD7KRmRXvRLJ25Yow/uH4erP15QJdpeejfFMEvSWxUZidvOyrvb42";

        boolean matches = encoder.matches(rawPassword, hashedPassword);
        System.out.println("Password matches: " + matches);
    }
}
