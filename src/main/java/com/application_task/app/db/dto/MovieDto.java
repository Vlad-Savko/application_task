package com.application_task.app.db.dto;

import com.application_task.app.entity.Rental;

/**
 * Represents a movie entity dto
 *
 * @see com.application_task.app.entity.Movie
 */
public record MovieDto(Long id, String name, int yearOfRelease, Rental rental) {
}
