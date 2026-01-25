package com.modulythe.framework.domain.common.pagination;


import com.modulythe.framework.domain.ddd.BaseValueObject;

import java.util.List;
import java.util.Objects;

/**
 * Represents the sorting criteria for a pagination request.
 * <p>
 * This class contains a list of orders, where each order specifies a property to sort by
 * and a direction (ascending or descending).
 * </p>
 */
@SuppressWarnings("java:S2160") // "false positive"
public final class SortModel extends BaseValueObject<SortModel> {

    private static final SortModel EMPTY_SORT_MODEL = new SortModel(List.of());

    private final List<Order> orders;

    private SortModel(List<Order> orders) {
        super(SortModel.class);
        this.orders = List.copyOf(orders);
        validate(this);
    }

    public static SortModel by(List<Order> orders) {
        return orders == null || orders.isEmpty()
                ? EMPTY_SORT_MODEL
                : new SortModel(orders);
    }

    public static SortModel empty() {
        return EMPTY_SORT_MODEL;
    }

    public boolean isEmpty() {
        return orders.isEmpty();
    }

    public List<Order> getOrders() {
        return orders;
    }

    @Override
    protected List<Object> attributesToIncludeInEqualityCheck() {
        return List.of(orders);
    }

    public enum Direction {
        ASC,
        DESC;

        public boolean isAscending() {
            return this == ASC;
        }

        public boolean isDescending() {
            return this == DESC;
        }
    }

    public static class Order extends BaseValueObject<Order> {
        private final String property;
        private final Direction direction;

        private Order(String property, Direction direction) {
            super(Order.class);
            this.property = property;
            this.direction = direction;
            validate(this);
        }

        public static Order by(String property, Direction direction) {
            return new Order(property, direction);
        }

        public String getProperty() {
            return property;
        }

        public Direction getDirection() {
            return direction;
        }

        @Override
        protected List<Object> attributesToIncludeInEqualityCheck() {
            return List.of(property, direction);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            Order order = (Order) o;
            return Objects.equals(property, order.property) && direction == order.direction;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), property, direction);
        }

        @Override
        public String toString() {
            return "Order{" +
                    "property='" + property + '\'' +
                    ", direction=" + direction +
                    '}';
        }
    }
}