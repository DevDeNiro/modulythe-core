package com.modulythe.framework.application.security;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Represents an authenticated user in the system.
 * <p>
 * This record holds standard identity information and roles extracted from
 * the authentication token (JWT or Opaque).
 * </p>
 *
 * @param uuid      The unique identifier of the user (subject).
 * @param firstName The user's first name.
 * @param lastName  The user's last name.
 * @param email     The user's email address.
 * @param roles     The list of roles or authorities assigned to the user.
 */
public record AuthenticatedUser(
        String uuid,
        String firstName,
        String lastName,
        String email,
        List<String> roles
) implements Serializable {
    public AuthenticatedUser {
        Objects.requireNonNull(uuid, "uuid cannot be null");
        Objects.requireNonNull(firstName, "firstName cannot be null");
        Objects.requireNonNull(lastName, "lastName cannot be null");
        Objects.requireNonNull(email, "email cannot be null");
        Objects.requireNonNull(roles, "roles cannot be null");
    }
}
