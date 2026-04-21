package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import src.repository.user.UserEntity;
import src.service.user.model.DefaultUserStatus;
import src.service.user.model.UserRole;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * V-14 – Account lockout after repeated failed login attempts (OWASP A07 / CWE-307).
 *
 * Without lockout, a single attacker or botnet can attempt unlimited credential-stuffing
 * attacks against a specific account regardless of the IP-level rate limit.
 *
 * Fix: UserEntity tracks failedLoginAttempts and lockedUntil. After 5 failures, the
 * account is locked for 15 minutes. Spring Security's isAccountNonLocked() check in
 * DaoAuthenticationProvider automatically returns 401 (LockedException) during the window.
 *
 * On successful authentication, both fields are reset to zero/null.
 */
@DisplayName("V-14 – Account lockout after 5 failed login attempts")
class AccountLockoutSecurityTest {

    private UserEntity buildUser() {
        UserEntity user = UserEntity.userBuilder()
                .emailAddress("user@example.com")
                .name("Test")
                .surname("User")
                .phoneNumber("5551234567")
                .password("$2a$10$hash")
                .authority(UserRole.CUSTOMER)
                .status(DefaultUserStatus.VERIFIED)
                .build();
        user.setIsDeleted(false);
        return user;
    }

    @Test
    @DisplayName("isAccountNonLocked() returns true when lockedUntil is null (no lockout)")
    void noLockout_isAccountNonLocked_returnsTrue() {
        UserEntity user = buildUser();
        user.setLockedUntil(null);
        assertThat(user.isAccountNonLocked()).isTrue();
    }

    @Test
    @DisplayName("isAccountNonLocked() returns false when lockedUntil is in the future")
    void activeLockout_isAccountNonLocked_returnsFalse() {
        UserEntity user = buildUser();
        user.setLockedUntil(LocalDateTime.now().plusMinutes(15));
        assertThat(user.isAccountNonLocked())
                .as("Account should be locked while lockedUntil is in the future")
                .isFalse();
    }

    @Test
    @DisplayName("isAccountNonLocked() returns true after lockout window expires")
    void expiredLockout_isAccountNonLocked_returnsTrue() {
        UserEntity user = buildUser();
        user.setLockedUntil(LocalDateTime.now().minusSeconds(1)); // expired just now
        assertThat(user.isAccountNonLocked())
                .as("Account must be unlocked once the lockout window has passed")
                .isTrue();
    }

    @Test
    @DisplayName("isAccountNonLocked() returns false when status is BLOCKED regardless of lockedUntil")
    void blockedStatus_isAccountNonLocked_returnsFalse() {
        UserEntity user = buildUser();
        user.setStatus(DefaultUserStatus.BLOCKED);
        user.setLockedUntil(null); // no temporary lockout, but permanently blocked
        assertThat(user.isAccountNonLocked())
                .as("BLOCKED status must always lock the account")
                .isFalse();
    }

    @Test
    @DisplayName("UserEntity has failedLoginAttempts field (default 0)")
    void userEntity_hasFailedLoginAttemptsField() throws NoSuchFieldException {
        java.lang.reflect.Field field = UserEntity.class.getDeclaredField("failedLoginAttempts");
        assertThat(field).isNotNull();
        assertThat(field.getType()).isEqualTo(int.class);
    }

    @Test
    @DisplayName("UserEntity has lockedUntil field (LocalDateTime, nullable)")
    void userEntity_hasLockedUntilField() throws NoSuchFieldException {
        java.lang.reflect.Field field = UserEntity.class.getDeclaredField("lockedUntil");
        assertThat(field).isNotNull();
        assertThat(field.getType()).isEqualTo(LocalDateTime.class);
    }

    @Test
    @DisplayName("failedLoginAttempts increments correctly via setter")
    void failedLoginAttempts_incrementsCorrectly() {
        UserEntity user = buildUser();
        assertThat(user.getFailedLoginAttempts()).isEqualTo(0);
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(1);
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(2);
    }

    @Test
    @DisplayName("Resetting lockout clears both failedLoginAttempts and lockedUntil")
    void resetLockout_clearsBothFields() {
        UserEntity user = buildUser();
        user.setFailedLoginAttempts(5);
        user.setLockedUntil(LocalDateTime.now().plusMinutes(10));

        // Simulate successful login reset
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);

        assertThat(user.getFailedLoginAttempts()).isEqualTo(0);
        assertThat(user.getLockedUntil()).isNull();
        assertThat(user.isAccountNonLocked()).isTrue();
    }
}
