package com.memzocards.service;

import com.memzocards.model.AppUser;
import com.memzocards.model.PasswordResetToken;
import com.memzocards.repository.AppUserRepository;
import com.memzocards.repository.PasswordResetTokenRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository appUserRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public boolean register(String email, String rawPassword) {
        String normalizedEmail = email.trim().toLowerCase();
        if (appUserRepository.existsByEmail(normalizedEmail)) {
            return false;
        }
        AppUser user = new AppUser(normalizedEmail, passwordEncoder.encode(rawPassword));
        appUserRepository.save(user);
        return true;
    }

    @Transactional
    public Optional<String> createResetTokenForEmail(String email) {
        return appUserRepository.findByEmail(email.trim().toLowerCase())
                .map(user -> {
                    String token = UUID.randomUUID().toString();
                    PasswordResetToken resetToken = new PasswordResetToken(
                            token,
                            user,
                            LocalDateTime.now().plusMinutes(30)
                    );
                    passwordResetTokenRepository.save(resetToken);
                    return token;
                });
    }

    @Transactional
    public boolean resetPassword(String token, String newRawPassword) {
        Optional<PasswordResetToken> tokenOptional = passwordResetTokenRepository.findByToken(token);
        if (tokenOptional.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOptional.get();
        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            return false;
        }

        AppUser user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newRawPassword));
        appUserRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByEmail(username.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.withUsername(appUser.getEmail())
                .password(appUser.getPassword())
                .roles("USER")
                .build();
    }
}
