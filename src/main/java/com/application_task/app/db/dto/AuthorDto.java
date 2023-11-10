package com.application_task.app.db.dto;

import com.application_task.app.entity.Author;

import java.util.List;

/**
 * Represents an author entity dto, which has list of author related movies
 *
 * @see Author
 */
public record AuthorDto(Long id, String firstName, String lastName, int age, List<MovieDto> relatedMovies) {
    /**
     * Creates an author from this dto
     *
     * @return an {@link Author} from this dto
     * @see Author
     */
    public Author getAuthor() {
        return new Author(this.id, this.firstName, this.lastName, this.age);
    }

    public List<MovieDto> getRelatedMovies() {
        return this.relatedMovies;
    }
}
