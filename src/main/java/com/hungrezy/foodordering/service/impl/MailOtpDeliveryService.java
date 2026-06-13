package com.hungrezy.foodordering.service.impl;

import com.hungrezy.foodordering.exception.ServiceUnavailableException;
import com.hungrezy.foodordering.service.OtpDeliveryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailOtpDeliveryService implements OtpDeliveryService {
    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final String mailHost;

    public MailOtpDeliveryService(JavaMailSender mailSender,
                                  @Value("${app.mail.from}") String fromAddress,
                                  @Value("${spring.mail.host:}") String mailHost) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.mailHost = mailHost;
    }

    @Override
    public void sendEmailVerificationCode(String email, String code) {
        if (mailHost == null || mailHost.isBlank()) {
            throw new ServiceUnavailableException("Email delivery is not configured");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(email);
        message.setSubject("Your Hungrezy verification code");
        message.setText("""
                Your Hungrezy verification code is %s.

                This code expires in 10 minutes. If you did not request this code, you can ignore this email.
                """.formatted(code));

        try {
            mailSender.send(message);
        } catch (MailException ex) {
            throw new ServiceUnavailableException("Unable to send verification code. Please try again later.");
        }
    }
}
