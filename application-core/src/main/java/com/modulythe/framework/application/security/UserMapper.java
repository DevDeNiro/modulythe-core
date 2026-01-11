package com.modulythe.framework.application.security;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class UserMapper {

    private static final String CLAIM_SUB = "sub";
    private static final String CLAIM_GIVEN_NAME = "given_name";
    private static final String CLAIM_FAMILY_NAME = "family_name";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLES = "roles";

    @NonNull
    public AuthenticatedUser toAuthenticatedUser(@NonNull Map<String, Object> attributes) {
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
        Object roles = attributes.get(CLAIM_ROLES);
        if (roles instanceof List<?> list) {
            return list.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .toList();
        }
        return Collections.emptyList();
    }
}
