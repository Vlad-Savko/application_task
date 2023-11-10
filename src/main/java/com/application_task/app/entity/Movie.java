package com.application_task.app.entity;

import java.util.List;

/**
 * Represents a movie entity
 */
public record Movie(Long id, String name, int yearOfRelease, List<Author> authors, Rental rental) {
}
