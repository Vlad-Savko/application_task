package com.application_task.app.db.dto;

import com.application_task.app.entity.Rental;

public record MovieDto(Long id, String name, int yearOfRelease, Rental rental) {
}
