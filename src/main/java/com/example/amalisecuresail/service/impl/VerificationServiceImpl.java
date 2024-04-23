package com.example.amalisecuresail.service.impl;


import com.example.amalisecuresail.entity.User;
import com.example.amalisecuresail.repository.UserRepository;
import com.example.amalisecuresail.service.VerificationService;
import com.example.amalisecuresail.util.EncryptionUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class VerificationServiceImpl implements VerificationService {

    private final UserRepository userRepository;

    public VerificationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void verifyUser(String encryptedUuid) {
        try {

            String uuid = EncryptionUtil.decrypt(encryptedUuid);
            System.out.println("Decrypted UUID: " + uuid); // Add this line

            User user = userRepository.findByUuid(uuid).orElseThrow(() -> new EntityNotFoundException("User not found"));

            if (user != null && !user.isVerified()) {
                user.setVerified(true);
                userRepository.save(user);
            }

        } catch (NoSuchElementException e) {
            throw new ValidationException("User not found or already verified", e);
        } catch (Exception e) {
            System.err.println("Exception occurred during verification: " + e.getMessage());
            e.printStackTrace();
            throw new ValidationException("Verification failed", e);
        }
    }

}
