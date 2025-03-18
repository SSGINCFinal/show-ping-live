package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.entity.Member;
import com.ssginc.showpinglive.entity.MemberRole;
import com.ssginc.showpinglive.repository.MemberRepository;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import com.warrenstrange.googleauth.IGoogleAuthenticator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

public interface AdminAccountService extends CommandLineRunner {
    void run(String... args);
    Member createAdminAccount();
}
