package com.ssginc.showpinglive.service;


import com.ssginc.showpinglive.dto.object.MailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public interface MailService {
    String sendVerificationCode(String email);
    boolean verifyCode(String email, String code);
    void send(String to, String subject, String text);
}
