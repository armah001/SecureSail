package com.example.amalisecuresail.service;


import com.example.amalisecuresail.dto.MailResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for sending emails with attachments.
 */
public interface EmailService {
    /**
     * Sends an email with optional file attachments.
     *
     * @param file   Array of files to attach (can be null or empty).
     * @param to     Primary recipient's email address.
     * @param cc     Array of CC recipients' email addresses (can be null or empty).
     * @param subject Email subject.
     * @param body    Email body text
     * @return ResponseEntity with MailResponse indicating the operation's success or failure.
     */

    ResponseEntity<MailResponse> sendMail(MultipartFile[] file, String to, String[] cc, String subject, String body) ;

    ResponseEntity<MailResponse> sendMail(MultipartFile[] file, String to, String[] cc, String subject, String body, String verificationUrl) ;

    String buildEmailContent(String header, String text, String verificationUrl);

    String composeEmailBody( );
}
