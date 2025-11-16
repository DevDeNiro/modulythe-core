package com.modulythe.framework.infrastructure.mapper;

import com.modulythe.framework.domain.model.UniqueId;
import org.mapstruct.Mapper;

import java.util.Optional;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface EntityMapperUtils {

    default UniqueId uuidToUniqueId(UUID uuid) {
        return uuid != null ? UniqueId.of(uuid.toString()) : null;
    }

    default UUID uniqueIdToUUID(UniqueId uniqueId) {
        return uniqueId != null ? UUID.fromString(uniqueId.getValue()) : null;
    }

    default String uniqueIdToString(UniqueId uniqueId) {
        return uniqueId != null ? uniqueId.getValue() : null;
    }

    default UniqueId stringToUniqueId(String uid) {
        return uid != null ? UniqueId.of(uid) : null;
    }

    default Optional<String> stringToOptional(String value) {
        return Optional.ofNullable(value);
    }
}