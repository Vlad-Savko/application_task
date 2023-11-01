package com.application_task.app.db.dao.impl;

import com.application_task.app.db.connection.ConnectionPool;
import com.application_task.app.db.connection.ConnectionPoolImpl;
import com.application_task.app.db.dao.AbstractDao;
import com.application_task.app.db.dao.MovieDao;
import com.application_task.app.db.dto.MovieDto;
import com.application_task.app.entity.Author;
import com.application_task.app.entity.Movie;
import com.application_task.app.entity.Rental;
import com.application_task.app.exception.DatabaseException;
import com.application_task.app.exception.WrongRequestParamException;
import com.application_task.app.service.AuthorService;
import com.application_task.app.service.MovieAuthorService;
import com.application_task.app.service.RentalService;
import com.application_task.app.service.impl.AuthorServiceImpl;
import com.application_task.app.service.impl.MovieAuthorServiceImpl;
import com.application_task.app.service.impl.RentalServiceImpl;
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

public class MovieDaoImpl extends AbstractDao implements MovieDao {
    private final ConnectionPool connectionPool;
    private final RentalService rentalService;
    private final AuthorService authorService;
    private final MovieAuthorService movieAuthorService;

    public MovieDaoImpl(String user, String password) {
        this.rentalService = new RentalServiceImpl(user, password);
        this.authorService = new AuthorServiceImpl(user, password);
        this.movieAuthorService = new MovieAuthorServiceImpl(user, password);
        this.connectionPool = new ConnectionPoolImpl(user, password);
    }

    @Override
    public void create(Movie movie) throws DatabaseException {
        if (this.get(movie.id()).isEmpty()) {

            for (Author author : movie.authors()) {
                authorService.create(author);
            }
            movieAuthorService.bindMovieToAuthors(movie.id(),
                    movie.authors().stream()
                            .map(Author::id)
                            .collect(Collectors.toList()));

            createMovie(
                    movie.rental(),
                    movie.id(),
                    movie.name(),
                    movie.yearOfRelease()
            );
        }
    }

    @Override
    public long create(MovieDto movie) throws DatabaseException {
        if (this.get(movie.id()).isEmpty()) {
            createMovie(movie.rental(), movie.id(), movie.name(), movie.yearOfRelease());
            return movie.id();
        }
        return -1L;
    }

    @Override
    public Optional<Movie> get(long id) throws DatabaseException {
        String sqlCommandForGettingMovie = Constants.Sql.Movie.SELECT_ONE.formatted("ID", String.valueOf(id));
        String sqlCommandForGettingMovieAuthors = Constants.Sql.Author.GET_AUTHORS_FOR_MOVIE.formatted(id);
        String sqlCommandForGettingMovieRental = Constants.Sql.Rental.GET_RENTAL_FOR_MOVIE;

        try (Connection connection = connectionPool.getConnection()) {
            PreparedStatement moviePs = connection.prepareStatement(sqlCommandForGettingMovie);
            ResultSet movieResultSet = moviePs.executeQuery();
            PreparedStatement authorsPs = connection.prepareStatement(sqlCommandForGettingMovieAuthors);
            ResultSet authorsResultSet = authorsPs.executeQuery();


            List<Author> authors = new ArrayList<>();

            while (authorsResultSet.next()) {
                authors.add(new Author(
                        authorsResultSet.getLong(1),
                        authorsResultSet.getString(2),
                        authorsResultSet.getString(3),
                        authorsResultSet.getInt(4)
                ));
            }

            Movie movie = null;

            while (movieResultSet.next()) {
                long rentalId = movieResultSet.getLong(4);
                @SuppressWarnings(value = "all")
                PreparedStatement rentalPs = connection.prepareStatement(sqlCommandForGettingMovieRental.formatted(rentalId));
                ResultSet rentalResultSet = rentalPs.executeQuery();
                Rental rental = null;
                while (rentalResultSet.next()) {
                    rental = new Rental(
                            LocalDate.parse(rentalResultSet.getDate(1).toString()),
                            LocalDate.parse(rentalResultSet.getDate(2).toString())
                    );
                }
                movie = new Movie(
                        movieResultSet.getLong(1),
                        movieResultSet.getString(2),
                        movieResultSet.getInt(3),
                        authors,
                        rental
                );
            }
            return Optional.ofNullable(movie);

        } catch (SQLException e) {
            throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
        }

    }

    @Override
    public List<Movie> getAll() throws DatabaseException {
        List<Movie> movies;

        String sqlCommandForGettingMovies = Constants.Sql.Movie.SELECT_ALL;
        String sqlCommandForGettingAuthors = Constants.Sql.Author.GET_AUTHORS_FOR_MOVIE;
        String sqlCommandForGettingMovieRental = Constants.Sql.Rental.GET_RENTAL_FOR_MOVIE;
        try (Connection connection = connectionPool.getConnection()) {
            movies = getMovies(
                    connection,
                    sqlCommandForGettingMovies,
                    sqlCommandForGettingAuthors,
                    sqlCommandForGettingMovieRental
            );
        } catch (SQLException e) {
            throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
        }
        return movies;
    }


    @Override
    public void update(Movie movie) throws DatabaseException {
        long movieId = movie.id();

        Optional<Movie> movieToUpdate = this.get(movieId);
        if (movieToUpdate.isPresent()) {
            Movie original = movieToUpdate.get();
            try (Connection connection = connectionPool.getConnection()) {
                if (!compareRentals(original, movie)) {
                    String sqlCommandForGettingRentalId = Constants.Sql.Rental.GET_RENTAL_ID_FOR_MOVIE.formatted(movieId);
                    long rentalId = getRentalId(connection, sqlCommandForGettingRentalId);
                    String sqlCommandForUpdatingMovieRental = Constants.Sql.Rental.UPDATE_RENTAL.formatted(
                            movie.rental().start(),
                            movie.rental().finish(),
                            rentalId);

                    PreparedStatement ps = connection.prepareStatement(sqlCommandForUpdatingMovieRental);
                    ps.execute();
                }
                List<Author> originalAuthors = original.authors();
                List<Author> movieAuthors = movie.authors();
                List<Long> originalIds = originalAuthors.stream().map(Author::id).toList();
                List<Long> moviesToUpdateIds = movieAuthors.stream().map(Author::id).toList();

                List<Author> authorsToUpdate = movieAuthors.stream()
                        .filter(author -> originalIds.contains(author.id()))
                        .filter(author -> !originalAuthors.contains(author))
                        .toList();
                List<Author> authorsToInsert = movieAuthors.stream()
                        .filter(author -> !originalIds.contains(author.id()))
                        .filter(author -> !authorsToUpdate.contains(author))
                        .toList();
                List<Author> authorsToDelete = originalAuthors.stream()
                        .filter(author -> !moviesToUpdateIds.contains(author.id()))
                        .toList();


                for (Author author : authorsToUpdate) {
                    authorService.update(author);
                }
                List<Long> authorsToInsertIds = new ArrayList<>();
                for (Author author : authorsToInsert) {
                    authorsToInsertIds.add(authorService.create(author));
                }
                if (!authorsToInsertIds.isEmpty()) {
                    movieAuthorService.bindMovieToAuthors(movieId, authorsToInsertIds);
                }
                List<Long> authorsToUnbindIds = new ArrayList<>();
                for (Author author : authorsToDelete) {
                    authorsToUnbindIds.add(author.id());
                }
                movieAuthorService.unbindAuthorsFromMovie(movieId, authorsToUnbindIds);

                String sqlCommandForUpdatingMovie = Constants.Sql.Movie.UPDATE.formatted(
                        movie.name(),
                        movie.yearOfRelease(),
                        movieId);
                PreparedStatement ps = connection.prepareStatement(sqlCommandForUpdatingMovie);
                ps.execute();
            } catch (SQLException e) {
                throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
            }
        }
    }


    @Override
    @SuppressWarnings("all")
    public Optional<Movie> delete(long id) throws DatabaseException {

        String sqlCommandForGettingRentalId = Constants.Sql.Rental.GET_RENTAL_ID_FOR_MOVIE.formatted(id);
        Optional<Movie> movie = this.get(id);
        if (movie.isPresent()) {
            try (Connection connection = connectionPool.getConnection()) {
                long rentalId = getRentalId(connection, sqlCommandForGettingRentalId);
                PreparedStatement ps;

                String sqlCommandForDeletingMovie =
                        Constants.Sql.Movie.DELETE.formatted(id);
                ps = connection.prepareStatement(sqlCommandForDeletingMovie);
                ps.execute();

                String sqlCommandForDeletingMovieRental =
                        Constants.Sql.Rental.DELETE.formatted(rentalId);
                ps = connection.prepareStatement(sqlCommandForDeletingMovieRental);
                ps.execute();

                String sqlCommandForDeletingMoviesAuthors =
                        Constants.Sql.MovieAuthors.DELETE_BY_MOVIE_ID.formatted(id);
                ps = connection.prepareStatement(sqlCommandForDeletingMoviesAuthors);
                ps.execute();
            } catch (SQLException e) {
                throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
            }
        }
        return movie;
    }

    @Override
    @SuppressWarnings("all")
    public List<Movie> getWithFilters(Map<String, String> filters) throws WrongRequestParamException, DatabaseException {
        if (filters.isEmpty()) {
            return this.getAll();
        } else {
            if (checkFilters(filters)) {
                List<Movie> movies;
                String sqlCommandForGettingMoviesFiltered = getSqlCommandForFiltering(filters);
                String sqlCommandForGettingAuthors = Constants.Sql.Author.GET_AUTHORS_FOR_MOVIE;
                String sqlCommandForGettingMovieRental = Constants.Sql.Rental.GET_RENTAL_FOR_MOVIE;

                try (Connection connection = connectionPool.getConnection()) {
                    movies = getMovies(
                            connection,
                            sqlCommandForGettingMoviesFiltered,
                            sqlCommandForGettingAuthors,
                            sqlCommandForGettingMovieRental);

                    if (filters.containsKey(Constants.Params.AUTHOR_ID)) {
                        long id;
                        try {
                            id = Long.parseLong(filters.get(Constants.Params.AUTHOR_ID));
                        } catch (NumberFormatException e) {
                            throw new WrongRequestParamException(Constants.Message.ERROR_WRONG_REQUEST_PARAM);
                        }
                        String sqlCommandForFilteringMovies = Constants.Sql.Movie
                                .GET_MOVIES_IDS_FOR_AUTHOR
                                .formatted(id);

                        PreparedStatement moviesIds = connection.prepareStatement(sqlCommandForFilteringMovies);
                        ResultSet moviesIdsRs = moviesIds.executeQuery();

                        List<Long> moviesIdsList = new ArrayList<>();

                        while (moviesIdsRs.next()) {
                            moviesIdsList.add(moviesIdsRs.getLong(1));
                        }

                        movies = movies.stream()
                                .filter(movie -> moviesIdsList.contains(movie.id()))
                                .toList();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return movies;
            } else {
                throw new WrongRequestParamException(Constants.Message.ERROR_WRONG_REQUEST_PARAM);
            }
        }
    }


    private void createMovie(Rental rental, Long movieId, String movieName, int movieYearOfRelease) throws DatabaseException {
        long rentalId = rentalService.create(rental);
        String sqlCommand = Constants.Sql.Movie.INSERT.formatted(movieId, movieName, movieYearOfRelease, rentalId);
        try (Connection connection = connectionPool.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sqlCommand);
            ps.execute();
        } catch (SQLException e) {
            throw new DatabaseException(Constants.Db.DB_DATA_ERROR);
        }
    }

    @NotNull
    private List<Movie> getMovies(
            Connection connection,
            String sqlCommandForGettingMovies,
            String sqlCommandForGettingAuthors,
            String sqlCommandForGettingMovieRental
    ) throws SQLException {
        return getMoviesList(
                connection,
                sqlCommandForGettingMovies,
                sqlCommandForGettingAuthors,
                sqlCommandForGettingMovieRental
        );
    }

    @NotNull
    private List<Movie> getMoviesList(
            Connection connection,
            String sqlCommandForGettingMoviesFiltered,
            String sqlCommandForGettingAuthors,
            String sqlCommandForGettingMovieRental
    ) throws SQLException {
        List<Movie> movies;
        List<Author> authors;
        Rental rental = null;
        PreparedStatement moviesPs = connection.prepareStatement(sqlCommandForGettingMoviesFiltered);
        ResultSet moviesResultSet = moviesPs.executeQuery();
        movies = new ArrayList<>();

        while (moviesResultSet.next()) {
            long movieId = moviesResultSet.getLong(1), rentalId = moviesResultSet.getLong(4);
            PreparedStatement authorsPs = connection.prepareStatement(sqlCommandForGettingAuthors.formatted(movieId));
            PreparedStatement rentalPs = connection.prepareStatement(sqlCommandForGettingMovieRental.formatted(rentalId));

            ResultSet authorsResultSet = authorsPs.executeQuery();
            ResultSet rentalResult = rentalPs.executeQuery();
            authors = new ArrayList<>();
            while (authorsResultSet.next()) {
                authors.add(new Author(
                        authorsResultSet.getLong(1),
                        authorsResultSet.getString(2),
                        authorsResultSet.getString(3),
                        authorsResultSet.getInt(4)
                ));
            }
            while (rentalResult.next()) {
                rental = new Rental(LocalDate.parse(rentalResult.getString(1)),
                        LocalDate.parse(rentalResult.getString(2)));
            }
            movies.add(new Movie(
                    movieId,
                    moviesResultSet.getString(2),
                    moviesResultSet.getInt(3),
                    authors,
                    rental
            ));
        }
        return movies;
    }

    private boolean checkFilters(Map<String, String> filters) {
        return ParamValidator.checkMovieParams(filters);
    }

    private static String getSqlCommandForFiltering(Map<String, String> filters) {
        StringBuilder sqlCommand = new StringBuilder(Constants.Sql.Movie.SELECT);
        filters.forEach((key, value) -> {
            if (!key.equals(Constants.Params.AUTHOR_ID)) {
                key = String.format("%c%s", Character.toUpperCase(key.charAt(0)), key.substring(1));
                sqlCommand.append(String.format(" \"%s\"='%s' AND", key, value));
            }
        });
        return sqlCommand.substring(0, sqlCommand.length() - 4);
    }

    private boolean compareRentals(Movie original, Movie movie) {
        return original.rental()
                .equals(movie.rental());
    }
}
