package com.application_task.app.db.dao.impl;

import com.application_task.app.db.connection.ConnectionPool;
import com.application_task.app.db.connection.ConnectionPoolImpl;
import com.application_task.app.db.dao.AuthorDao;
import com.application_task.app.db.dto.AuthorDto;
import com.application_task.app.db.dto.MovieDto;
import com.application_task.app.entity.Author;
import com.application_task.app.entity.Rental;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.exception.WrongRequestParamException;
import com.application_task.app.service.MovieAuthorService;
import com.application_task.app.service.impl.MovieAuthorServiceImpl;
import com.application_task.app.util.Constants;
import com.application_task.app.util.ParamValidator;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 */
public class AuthorDaoImpl implements AuthorDao {
    private final ConnectionPool connectionPool;
    private final MovieDaoImplHelper helper;
    private final MovieAuthorService movieAuthorService;

    /**
     * Constructs an {@link AuthorDao} with the needed services that will use datasource with the URL from properties file
     */
    public AuthorDaoImpl(String user, String password) {
        helper = new MovieDaoImplHelper(user, password);
        movieAuthorService = new MovieAuthorServiceImpl(user, password);
        connectionPool = new ConnectionPoolImpl(user, password);
    }

    /**
     * Constructs an {@link AuthorDao} with the needed services that will use datasource with the provided credentials
     */
    public AuthorDaoImpl(String user, String password, String databaseUrl) {
        helper = new MovieDaoImplHelper(user, password, databaseUrl);
        movieAuthorService = new MovieAuthorServiceImpl(user, password, databaseUrl);
        connectionPool = new ConnectionPoolImpl(user, password, databaseUrl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long create(Author author) throws DatabaseException {
        String sqlCommandForGettingAuthor = Constants.Sql.Author.GET_AUTHOR.formatted(author.id());

        try (Connection connection = connectionPool.getConnection()) {
            Author check = null;
            PreparedStatement ps = connection.prepareStatement(sqlCommandForGettingAuthor);
            ResultSet authorCheck = ps.executeQuery();

            while (authorCheck.next()) {
                check = new Author(
                        authorCheck.getLong(1),
                        authorCheck.getString(2),
                        authorCheck.getString(3),
                        authorCheck.getInt(4)
                );
            }
            if (check == null) {
                String sqlCommand = Constants.Sql.Author.INSERT_AUTHOR.formatted(
                        author.id(),
                        author.firstName(),
                        author.lastName(),
                        author.age());

                ps = connection.prepareStatement(sqlCommand);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
        }
        return author.id();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Author> get(long id) throws DatabaseException {

        String sqlCommandForGettingOneAuthor = Constants.Sql.Author.GET_AUTHOR.formatted(id);
        Author author = null;

        try (Connection connection = connectionPool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sqlCommandForGettingOneAuthor);
            ResultSet authorRs = ps.executeQuery();

            while (authorRs.next()) {
                author = new Author(
                        authorRs.getLong(1),
                        authorRs.getString(2),
                        authorRs.getString(3),
                        authorRs.getInt(4)
                );
            }
        } catch (SQLException e) {
            throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
        }
        return Optional.ofNullable(author);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<AuthorDto> getAsDto(long id) throws DatabaseException {
        AuthorDto author;
        String sqlCommandForGettingAuthors = Constants.Sql.Author.GET_AUTHOR.formatted(id);

        try (Connection connection = connectionPool.getConnection()) {
            PreparedStatement authorsPs = connection.prepareStatement(sqlCommandForGettingAuthors);
            ResultSet authorsResultSet = authorsPs.executeQuery();

            author = null;
            while (authorsResultSet.next()) {
                long authorId = authorsResultSet.getLong(1);

                String sqlCommandForGettingAuthorsMovies = Constants.Sql.Movie.GET_MOVIES_FOR_AUTHOR.formatted(authorId);

                PreparedStatement moviesPs = connection.prepareStatement(sqlCommandForGettingAuthorsMovies);
                ResultSet moviesResultSet = moviesPs.executeQuery();

                List<MovieDto> movies = new ArrayList<>();
                getMoviesDtos(moviesResultSet, connection, movies);
                author = new AuthorDto(authorId,
                        authorsResultSet.getString(2),
                        authorsResultSet.getString(3),
                        authorsResultSet.getInt(4),
                        movies);
            }
        } catch (SQLException e) {
            throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
        }
        return Optional.ofNullable(author);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AuthorDto> getWithFilters(Map<String, String> filters) throws WrongRequestParamException, DatabaseException {
        if (filters.isEmpty()) {
            return this.getAll();
        } else {
            if (checkFilters(filters)) {

                String sqlCommandForGettingFilteredAuthors = getSqlCommandForFiltering(filters);
                List<AuthorDto> authors;

                try (Connection connection = connectionPool.getConnection()) {
                    PreparedStatement authorsPs = connection.prepareStatement(sqlCommandForGettingFilteredAuthors);
                    ResultSet authorsResultSet = authorsPs.executeQuery();

                    authors = getAuthorDtos(authorsResultSet, connection);

                    if (filters.containsKey(Constants.Params.MOVIE_ID)) {
                        long id;
                        try {
                            id = Long.parseLong(filters.get(Constants.Params.MOVIE_ID));
                        } catch (NumberFormatException e) {
                            throw new WrongRequestParamException(Constants.Message.ERROR_WRONG_REQUEST_PARAM);
                        }
                        String sqlCommandForFilteringMovies = Constants.Sql.Author.GET_AUTHORS_IDS_FOR_MOVIE
                                .formatted(id);

                        authors = getFilteredAuthorDtos(connection, sqlCommandForFilteringMovies, authors);
                    }

                } catch (SQLException e) {
                    throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
                }
                return authors;
            } else {
                throw new WrongRequestParamException(Constants.Message.ERROR_WRONG_REQUEST_PARAM);
            }
        }
    }

    /**
     * Retrieves filtered authors from the database if such exist
     *
     * @param connection                   connection to the database
     * @param sqlCommandForFilteringMovies {@link String} which has the value of SQL command for retrieving needed authors
     * @param authors                      {@link List} of authors to fill with
     * @return {@code List} of filtered authors as dtos
     * @throws SQLException if database error occurs
     * @see SQLException
     */
    @NotNull
    @SuppressWarnings("all")
    private List<AuthorDto> getFilteredAuthorDtos(Connection connection, String sqlCommandForFilteringMovies, List<AuthorDto> authors) throws SQLException {
        PreparedStatement authorsIds = connection.prepareStatement(sqlCommandForFilteringMovies);
        ResultSet moviesIdsRs = authorsIds.executeQuery();

        List<Long> authorsIdsList = new ArrayList<>();

        while (moviesIdsRs.next()) {
            authorsIdsList.add(moviesIdsRs.getLong(1));
        }

        authors = authors.stream()
                .filter(author -> authorsIdsList.contains(author.id()))
                .toList();
        return authors;
    }

    /**
     * Retrieves authors from the database if such exist
     *
     * @param authorsResultSet {@link ResultSet} of authors to fill with related movies
     * @param connection       connection to the database
     * @return {@code List} of authors as dtos
     * @throws SQLException if database error occurs
     * @see SQLException
     */
    @NotNull
    private List<AuthorDto> getAuthorDtos(ResultSet authorsResultSet, Connection connection) throws SQLException {
        List<MovieDto> movies;
        List<AuthorDto> authors = new ArrayList<>();
        while (authorsResultSet.next()) {
            long authorId = authorsResultSet.getLong(1);

            String sqlCommandForGettingAuthorsMovies = Constants.Sql.Movie.GET_MOVIES_FOR_AUTHOR.formatted(authorId);

            PreparedStatement moviesPs = connection.prepareStatement(sqlCommandForGettingAuthorsMovies);
            ResultSet moviesResultSet = moviesPs.executeQuery();

            movies = new ArrayList<>();
            getMoviesDtos(moviesResultSet, connection, movies);
            authors.add(new AuthorDto(authorId,
                    authorsResultSet.getString(2),
                    authorsResultSet.getString(3),
                    authorsResultSet.getInt(4),
                    movies));
        }
        return authors;
    }

    private boolean checkFilters(Map<String, String> filters) {
        return ParamValidator.checkAuthorParams(filters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AuthorDto> getAll() throws DatabaseException {
        List<AuthorDto> authors;

        String sqlCommandForGettingAuthors = Constants.Sql.Author.SELECT_ALL;

        try (Connection connection = connectionPool.getConnection()) {
            PreparedStatement authorsPs = connection.prepareStatement(sqlCommandForGettingAuthors);
            ResultSet authorsResultSet = authorsPs.executeQuery();

            authors = getAuthorDtos(authorsResultSet, connection);
        } catch (SQLException e) {
            throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
        }
        return authors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Author author) throws DatabaseException {

        Optional<Author> originalOptional = this.get(author.id());
        if (originalOptional.isPresent()) {
            if (!author.equals(originalOptional.get())) {
                try (Connection connection = connectionPool.getConnection()) {
                    String sqlCommandForUpdatingAuthor = Constants.Sql.Author.UPDATE_AUTHOR.formatted(
                            author.firstName(),
                            author.lastName(),
                            author.age(),
                            author.id()
                    );
                    PreparedStatement ps = connection.prepareStatement(sqlCommandForUpdatingAuthor);
                    ps.execute();
                } catch (SQLException e) {
                    throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(AuthorDto authorDto) throws DatabaseException {
        Optional<AuthorDto> authorToUpdate = this.getAsDto(authorDto.id());
        if (authorToUpdate.isPresent()) {
            this.update(authorDto.getAuthor());

            List<MovieDto> originalMovies = authorToUpdate.get().relatedMovies();
            List<MovieDto> newMovies = authorDto.relatedMovies();
            List<Long> originalIds = originalMovies.stream().map(MovieDto::id).toList();
            List<Long> newMoviesIds = newMovies.stream().map(MovieDto::id).toList();

            List<MovieDto> moviesToUpdate = newMovies.stream()
                    .filter(movie -> originalIds.contains(movie.id()))
                    .filter(movie -> !originalMovies.contains(movie))
                    .toList();
            List<MovieDto> moviesToInsert = newMovies.stream()
                    .filter(movie -> !originalIds.contains(movie.id()))
                    .filter(movie -> !moviesToUpdate.contains(movie))
                    .toList();
            List<MovieDto> moviesToDelete = originalMovies.stream()
                    .filter(movie -> !newMoviesIds.contains(movie.id()))
                    .toList();


            for (MovieDto movie : moviesToUpdate) {
                helper.updateMovie(movie);
            }
            List<Long> moviesToInsertIds = new ArrayList<>();
            for (MovieDto movie : moviesToInsert) {
                moviesToInsertIds.add(helper.createMovie(movie));
            }
            if (!moviesToInsertIds.isEmpty()) {
                movieAuthorService.bindAuthorToMovies(authorDto.id(), moviesToInsertIds);
            }
            for (MovieDto movie : moviesToDelete) {
                helper.deleteMovie(movie.id());
            }
            movieAuthorService.unbindMoviesFromAuthor(authorDto.id(),
                    moviesToDelete.stream()
                            .map(MovieDto::id)
                            .collect(Collectors.toList()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<AuthorDto> delete(long id) throws DatabaseException {
        Optional<AuthorDto> author = this.getAsDto(id);
        if (author.isPresent()) {
            String sqlCommandForDeletingAuthor = Constants.Sql.Author.DELETE_AUTHOR.formatted(id);
            String sqlCommandForDeletingAuthorMovies = Constants.Sql.MovieAuthors.DELETE_BY_AUTHOR_ID.formatted(id);
            String sqlCommandForGettingMoviesIds = Constants.Sql.Movie.GET_MOVIES_IDS_FOR_AUTHOR.formatted(id);
            try (Connection connection = connectionPool.getConnection()) {
                PreparedStatement ps = connection.prepareStatement(sqlCommandForDeletingAuthor);
                ps.execute();

                ps = connection.prepareStatement(sqlCommandForGettingMoviesIds);
                ResultSet moviesIds = ps.executeQuery();
                List<Long> moviesIdsList = new ArrayList<>();

                while (moviesIds.next()) {
                    moviesIdsList.add(moviesIds.getLong(1));
                }
                for (Long movieId : moviesIdsList) {
                    if (checkOneAuthor(movieId)) {
                        helper.deleteMovie(movieId);
                    }
                }

                ps = connection.prepareStatement(sqlCommandForDeletingAuthorMovies);
                ps.execute();

            } catch (SQLException e) {
                throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
            }
        }
        return author;
    }

    /**
     * Retrieves movies from the database if such exist
     *
     * @param moviesResultSet {@link ResultSet} of movies
     * @param connection      connection to the database
     * @param movies          {@link List} of movies as dtos to fill with rental
     * @throws SQLException if database error occurs
     * @see SQLException
     */
    private void getMoviesDtos(ResultSet moviesResultSet, Connection connection, List<MovieDto> movies) throws SQLException {
        while (moviesResultSet.next()) {
            String sqlCommandForGettingMovieRental = Constants.Sql.Rental.GET_RENTAL_FOR_MOVIE.formatted(moviesResultSet.getLong(4));
            PreparedStatement rentalPs = connection.prepareStatement(sqlCommandForGettingMovieRental);
            ResultSet rentalResultSet = rentalPs.executeQuery();
            Rental rental = null;

            while (rentalResultSet.next()) {
                rental = new Rental(LocalDate.parse(rentalResultSet.getString(1)),
                        LocalDate.parse(rentalResultSet.getString(2)));
            }
            movies.add(new MovieDto(
                    moviesResultSet.getLong(1),
                    moviesResultSet.getString(2),
                    moviesResultSet.getInt(3),
                    rental
            ));
        }
    }

    /**
     * Checks if movie has only one author
     *
     * @param movieId id of movie to check
     * @return {@code true} if the movie has only one author
     * @throws DatabaseException if database error occurs
     * @see DatabaseException
     */
    private boolean checkOneAuthor(Long movieId) throws DatabaseException {
        try (Connection connection = connectionPool.getConnection()) {
            String sqlCommandForGettingNumberOfAuthors = Constants.Sql.Author.GET_NUMBER_OF_AUTHORS.formatted(movieId);
            PreparedStatement ps = connection.prepareStatement(sqlCommandForGettingNumberOfAuthors);
            ResultSet numberOfAuthors = ps.executeQuery();
            int count = 0;

            while (numberOfAuthors.next()) {
                count = numberOfAuthors.getInt(1);
            }

            return count == 1;

        } catch (SQLException e) {
            throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
        }

    }

    /**
     * Builds a string for SQL SELECT with filters
     *
     * @param filters {@link Map} of filters to add to string
     * @return {@link String} which has the value of SQL command for selecting authors with filters
     */
    private static String getSqlCommandForFiltering(Map<String, String> filters) {
        StringBuilder sqlCommand = new StringBuilder(Constants.Sql.Author.SELECT);
        filters.forEach((key, value) -> {
            if (key.length() > 3) {
                key = key.replaceFirst("Name", " Name");
            }
            if (!key.equals(Constants.Params.MOVIE_ID)) {
                key = String.format("%c%s", Character.toUpperCase(key.charAt(0)), key.substring(1));
                sqlCommand.append(String.format(" \"%s\"='%s' AND", key, value));
            }
        });
        return sqlCommand.substring(0, sqlCommand.length() - 4);
    }
}
