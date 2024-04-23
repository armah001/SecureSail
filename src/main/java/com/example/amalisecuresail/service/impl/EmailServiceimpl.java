package com.example.amalisecuresail.service.impl;


import com.example.amalisecuresail.dto.MailResponse;
import com.example.amalisecuresail.exception.MailServiceException;
import com.example.amalisecuresail.service.EmailService;
import com.example.amalisecuresail.util.GeneralSystemValidation;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of the EmailService for sending emails.
 */
@Service
@RequiredArgsConstructor
public class EmailServiceimpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final GeneralSystemValidation generalSystemValidation;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public String convertToHtml(String text) {
        // Replace special characters with HTML entities
        text = text.replace("&", "&amp;")
                .replace("\n", "<br/>"); // Convert newline characters to <br/> tags

        // Convert *word* to <strong>word</strong> (bold)
        text = text.replaceAll("\\*(.*?)\\*", "<strong>$1</strong>");

        // Convert _word_ to <em>word</em> (italics)
        text = text.replaceAll("\\_(.*?)\\_", "<em>$1</em>");

        return text;
    }




    @Override
    public String buildEmailContent(String header, String text, String verificationUrl) {
        Context context = new Context();
        context.setVariable("header", header);
        context.setVariable("text", convertToHtml(text));

        if (verificationUrl != null) {
            context.setVariable("verificationUrl", verificationUrl);
        }

        String buttonText = null;
        if (header.toLowerCase().contains("verification")) {
            buttonText = "VERIFY";
        } else if (header.toLowerCase().contains("reset")) {
            buttonText = "RESET";
        }else if(header.toLowerCase().contains("faq")){
            buttonText = "READ MORE";
        }
        context.setVariable("buttonText", buttonText);

        return templateEngine.process("generalEmailTemplate", context);
    }

    public String buildEmailContent(String header, String text) {
        return buildEmailContent(header, text, null);
    }

    @Override
    public ResponseEntity<MailResponse> sendMail(MultipartFile[] file, String to, String[] cc, String subject, String body) {
        return sendMail(file, to, cc, subject, body, null);
    }

    public ResponseEntity<MailResponse> sendMail(MultipartFile[] file, String to, String[] cc, String subject, String body, String verificationUrl) {
        try {
            List<String> missingFields = new ArrayList<>();

            if (ObjectUtils.isEmpty(to)) {
                missingFields.add("to");
            }

            if (ObjectUtils.isEmpty(subject)) {
                missingFields.add("subject");
            }

            if (ObjectUtils.isEmpty(body)) {
                missingFields.add("body");
            }

            if (!missingFields.isEmpty()) {
                throw new MailServiceException(missingFields);
            }

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            generalSystemValidation.validateEmail(to);
            if (cc != null) {
                for (String ccEmail : cc) {
                    generalSystemValidation.validateEmail(ccEmail);
                }
            }

            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(to);
            assert cc != null;
            mimeMessageHelper.setCc(cc);
            mimeMessageHelper.setSubject(subject);

            if (file != null) {
                for (MultipartFile multipartFile : file) {
                    mimeMessageHelper.addAttachment(
                            Objects.requireNonNull(multipartFile.getOriginalFilename()),
                            new ByteArrayResource(multipartFile.getBytes()));
                }
            }

            // Get the email content as HTML
            String emailContent = buildEmailContent(subject, body, verificationUrl);
            mimeMessageHelper.setText(emailContent, true);

            javaMailSender.send(mimeMessage);

            return ResponseEntity.status(HttpStatus.OK).body(new MailResponse("Mail sent successfully", 200, "OK", "/mail/send"));

        } catch (MailServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MailResponse(e.getMessage(), e.getStatus(), e.getError(), e.getPath()));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MailResponse(ex.getMessage(), 400, "BAD REQUEST", "/mail/send"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String composeEmailBody( ) {
        return """
                Hello\s

                This is a reminder for your upcoming project:

                Project Name: Service Finder Provider
                Project Category: Software Engineering\s
                Client On Project: Amalitech\s
                Please try your best to meet the deadlines.
                Thank you!""";
    }


}