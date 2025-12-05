package com.modulythe.framework.application;

import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.List;

public record AuthenticatedUser(
        @NonNull String uuid,
        @NonNull String firstName,
        @NonNull String lastName,
        @NonNull String email,
        @NonNull List<String> roles
) implements Serializable {
}