package com.application_task.app.entity;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a rental entity
 */
public record Rental(LocalDate start, LocalDate finish) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rental rental = (Rental) o;
        return Objects.equals(start, rental.start) && Objects.equals(finish, rental.finish);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, finish);
    }
}
