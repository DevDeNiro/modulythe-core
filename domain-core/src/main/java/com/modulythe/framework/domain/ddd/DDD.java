package com.modulythe.framework.domain.ddd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Entry point for DDD annotations.
 */
public @interface DDD {

    /*
            ##############################
                     # DOMAIN #
            ##############################
     */

    /**
     * Marks a class as an Aggregate Root in the domain (DDD).
     * <p>
     * Example:
     * <pre>{@code
     *     @DDD.BaseAggregateRoot(description = "Aggregate representing a customer")
     *     public class Customer { ... }
     * }</pre>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface BaseAggregateRoot {
        /**
         * A brief description of the aggregate's role in the domain.
         */
        String description() default "";
    }

    /**
     * Marks a class as a Domain Event in DDD.
     */
    public @interface DomainEvent {
        /**
         * A brief description of the event's purpose.
         */
        String description() default "";
    }

    /**
     * Marks a class as a Domain Entity in DDD.
     * <p>
     * Example:
     * <pre>{@code
     *     @DDD.DomainEntity(description = "Entity representing a user")
     *     public class User { ... }
     * }</pre>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface DomainEntity {
        /**
         * A brief description of the entity's role in the domain.
         */
        String description() default "";
    }

    /**
     * Marks a class as a Value Object in DDD.
     * <p>
     * Example:
     * <pre>{@code
     *     @DDD.ValueObject(description = "Value object representing an address")
     *     public class Address { ... }
     * }</pre>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface ValueObject {
        /**
         * A brief description of the value object's purpose.
         */
        String description() default "";

        int version() default 1;
    }

    /**
     * Marks a class as a Domain Service in DDD.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface DomainService {
        /**
         * A brief description of the service's responsibility.
         */
        String description() default "";

        int version() default 1;
    }

    /**
     * Marks a class as a Policy in the domain.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface DomainPolicy {

        String description() default "";

        int version() default 1;
    }

    /**
     * Marks an interface as a Domain Repository in DDD.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface DomainRepository {
        /**
         * A brief description of what the repository manages.
         */
        String description() default "";

        int version() default 1;
    }

    /*
            ##############################
                 # INFRASTRUCTURE #
            ##############################
     */

    /**
     * Marks the concrete implementation of a Domain Repository.
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RepositoryAdapter {
        /**
         * A brief description of the implementation details (e.g., "JPA implementation").
         */
        String description() default "";
    }

    /**
     * Marks an interface as an Infrastructure Service (e.g., for database access, external API calls).
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface InfrastructureService {
        /**
         * A brief description of the service's purpose.
         */
        String description() default "";
    }

    /*
            ##############################
                 # APPLICATION #
            ##############################
     */

    /**
     * Marks an interface as an Application Service (Application layer).
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ApplicationService {
        /**
         * A brief description of the service's responsibility.
         */
        String description() default "";

    }

    /**
     * Marks the concrete implementation of an Application Service.
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ApplicationServiceImpl {
        /**
         * A brief description of the implementation details.
         */
        String description() default "";
    }

}