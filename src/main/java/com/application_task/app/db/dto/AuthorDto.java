package com.application_task.app.db.dto;

import com.application_task.app.entity.Author;

import java.util.List;

public record AuthorDto(Long id, String firstName, String lastName, int age, List<MovieDto> relatedMovies) {
    public Author getAuthor() {
        return new Author(this.id, this.firstName, this.lastName, this.age);
    }

    public List<MovieDto> getRelatedMovies() {
        return this.relatedMovies;
    }
}
