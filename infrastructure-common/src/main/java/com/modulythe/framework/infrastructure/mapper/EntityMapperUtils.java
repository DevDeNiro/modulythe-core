package com.modulythe.framework.infrastructure.mapper;

import com.modulythe.framework.domain.model.UniqueId;

import java.util.Optional;
import java.util.UUID;

public class EntityMapperUtils {

    public static UniqueId uuidToUniqueId(UUID uuid) {
        return uuid != null ? UniqueId.of(uuid.toString()) : null;
    }

    public static UUID uniqueIdToUUID(UniqueId uniqueId) {
        return uniqueId != null ? UUID.fromString(uniqueId.getValue()) : null;
    }

    public static String uniqueIdToString(UniqueId uniqueId) {
        return uniqueId != null ? uniqueId.getValue() : null;
    }

    public static UniqueId stringToUniqueId(String uid) {
        return uid != null ? UniqueId.of(uid) : null;
    }

    public static Optional<String> stringToOptional(String value) {
        return Optional.ofNullable(value);
    }
}