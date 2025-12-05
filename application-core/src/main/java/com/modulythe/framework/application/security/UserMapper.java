package com.modulythe.framework.application.security;

import com.modulythe.framework.application.AuthenticatedUser;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class UserMapper {

    @NonNull
    public AuthenticatedUser toAuthenticatedUser(@NonNull Map<String, Object> attributes) {
        return new AuthenticatedUser(
                getClaimOrDefault(attributes, "sub"),
                getClaimOrDefault(attributes, "given_name"),
                getClaimOrDefault(attributes, "family_name"),
                getClaimOrDefault(attributes, "email"),
                extractRoles(attributes)
        );
    }

    private String getClaimOrDefault(Map<String, Object> attributes, String claim) {
        Object value = attributes.get(claim);
        return value instanceof String string ? string : "";
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRoles(Map<String, Object> attributes) {
        Object roles = attributes.get("roles");
        if (roles instanceof List<?>) {
            return (List<String>) roles;
        }
        return Collections.emptyList();
    }
}
