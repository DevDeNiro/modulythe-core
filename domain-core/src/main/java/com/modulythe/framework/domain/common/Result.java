package com.modulythe.framework.domain.common;

/**
 * A generic class that represents the result of an operation.
 * An operation can either be a success, containing a value, or a failure,
 * containing a {@link Notification} with details about the errors.
 *
 * @param <T> The type of the value returned on success.
 */
public class Result<T> {
    private final T value;
    private final Notification notification;

    private Result(T value, Notification notification) {
        this.value = value;
        this.notification = notification;
    }

    /**
     * Creates a success result with a value.
     *
     * @param value The value to be wrapped in the result.
     * @param <T>   The type of the value.
     * @return A new {@code Result} instance representing success.
     */
    public static <T> Result<T> success(T value) {
        return new Result<>(value, new Notification());
    }

    /**
     * Creates a failure result with a notification containing errors.
     *
     * @param notification The notification containing error details.
     * @param <T>          The type of the value.
     * @return A new {@code Result} instance representing failure.
     */
    public static <T> Result<T> failure(Notification notification) {
        return new Result<>(null, notification);
    }

    /**
     * Checks if the result represents a successful operation.
     *
     * @return {@code true} if the operation was successful (no errors), {@code false} otherwise.
     */
    public boolean isSuccess() {
        return !notification.hasErrors();
    }

    /**
     * Gets the value of the result. This should only be called if {@link #isSuccess()} is true.
     *
     * @return The result value, or {@code null} if the operation failed.
     */
    public T getValue() {
        return value;
    }

    /**
     * Gets the notification containing error details.
     *
     * @return The {@link Notification} instance.
     */
    public Notification getNotification() {
        return notification;
    }
}
