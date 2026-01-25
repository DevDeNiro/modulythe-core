package com.modulythe.framework.application.security;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Mapper component to convert raw attributes (from JWT or Opaque token) into an {@link AuthenticatedUser}.
 * <p>
 * Handles extraction of claims like sub, given_name, family_name, email, and roles.
 * </p>
 */
@Component
public class UserMapper {

    private static final String CLAIM_SUB = "sub";
    private static final String CLAIM_GIVEN_NAME = "given_name";
    private static final String CLAIM_FAMILY_NAME = "family_name";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_REALM_ACCESS = "realm_access";

    /**
     * Converts a map of token attributes to an AuthenticatedUser domain object.
     *
     * @param attributes The map of claims/attributes from the security token.
     * @return A populated {@link AuthenticatedUser}.
     * @throws NullPointerException if attributes map is null.
     */
    public AuthenticatedUser toAuthenticatedUser(Map<String, Object> attributes) {
        Objects.requireNonNull(attributes, "attributes cannot be null");
        return new AuthenticatedUser(
                getClaimOrDefault(attributes, CLAIM_SUB),
                getClaimOrDefault(attributes, CLAIM_GIVEN_NAME),
                getClaimOrDefault(attributes, CLAIM_FAMILY_NAME),
                getClaimOrDefault(attributes, CLAIM_EMAIL),
                extractRoles(attributes)
        );
    }

    private String getClaimOrDefault(Map<String, Object> attributes, String claim) {
        Object value = attributes.get(claim);
        return value instanceof String string ? string : "";
    }

    private List<String> extractRoles(Map<String, Object> attributes) {
        // 1. Try "roles" (Standard / Custom)
        Object roles = attributes.get(CLAIM_ROLES);
        if (roles instanceof List<?> list) {
            return list.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .toList();
        }

        // 2. Try "realm_access" -> "roles" (Keycloak Standard)
        Object realmAccess = attributes.get(CLAIM_REALM_ACCESS);
        if (realmAccess instanceof Map<?, ?> map) {
            Object realmRoles = map.get(CLAIM_ROLES);
            if (realmRoles instanceof List<?> list) {
                return list.stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .toList();
            }
        }

        return Collections.emptyList();
    }
}
