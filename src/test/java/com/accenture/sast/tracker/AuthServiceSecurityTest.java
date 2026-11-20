package com.accenture.sast.tracker;

import com.accenture.sast.tracker.model.AppUser;
import com.accenture.sast.tracker.repository.AppUserRepository;
import com.accenture.sast.tracker.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceSecurityTest {

    @Test
    void storesAdaptivePasswordHashAndAuthenticatesWithIt() {
        AppUserRepository repository = mock(AppUserRepository.class);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        AuthService authService = new AuthService(repository, encoder);
        when(repository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser savedUser = authService.register("user", "correct-password");
        when(repository.findByUsername("user")).thenReturn(Optional.of(savedUser));

        assertNotEquals("correct-password", savedUser.getPasswordHash());
        assertTrue(encoder.matches("correct-password", savedUser.getPasswordHash()));
        assertTrue(authService.authenticate("user", "correct-password"));
        assertFalse(authService.authenticate("user", "wrong-password"));
    }
}
