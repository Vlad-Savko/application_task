package com.application_task.app.util;

/**
 * Represents general constants and constants for server, SQL, entities, HTTP, database
 */
public final class Constants {
    public static final String CREATE_CONSTANT_CLASS_ERROR = "Constant class can not be created.";

    private Constants() {
        throw new AssertionError(CREATE_CONSTANT_CLASS_ERROR);
    }

    public final static class OfServer {
        private OfServer() {
            throw new AssertionError(CREATE_CONSTANT_CLASS_ERROR);
        }

        public static final String SERVER_STARTED = "Server has started on port %d";
        public static final String SERVER_STOPPED = "Server has stopped";
        public static final String MOVIE_CONTROLLER_CONTEXT_PATH = "/movies";
        public static final String AUTHOR_CONTROLLER_CONTEXT_PATH = "/authors";
        public static final String ITEMS_SEPARATED_BY_COMMA_REG_EXP = "(\\w*,\\w*)";
        public static final String INT_VALUE_REG_EXP = "[0-9]+";
        public static final String AGE_REG_EXP = "^(?:1[01][0-9]|120|1[7-9]|[2-9][0-9])$";
    }

    public final static class Sql {
        private Sql() {
            throw new AssertionError(CREATE_CONSTANT_CLASS_ERROR);
        }

        public static final class MovieAuthors {
            private MovieAuthors() {
                throw new AssertionError(CREATE_CONSTANT_CLASS_ERROR);
            }

            public static final String INSERT = """
                        INSERT into movies_authors("Movie ID", "Author ID") values (%d, %d)
                    """;
            public static final String DELETE_BY_MOVIE_ID = """
                        DELETE from movies_authors where "Movie ID"=%d;
                    """;
            public static final String DELETE_BY_AUTHOR_ID = """
                       DELETE from movies_authors where "Author ID"=%d;
                    """;
            public static final String DELETE = """
                        DELETE from movies_authors where "Movie ID"=%d AND "Author ID"=%d;\s
                    """;
        }

        public static final class Movie {
            private Movie() {
                throw new AssertionError(CREATE_CONSTANT_CLASS_ERROR);
            }

            public static final String INSERT = """
                        INSERT into movies("ID", "Name","YearOfRelease", "Rental ID") values
                        (%d, '%s', %d, %d);
                    """;
            public static final String GET_MOVIES_IDS_FOR_AUTHOR = """
                        SELECT movies."ID" from movies_authors
                        left join movies on movies_authors."Movie ID"=movies."ID"
                        where movies_authors."Author ID"=%d;
                    """;
            public static final String GET_MOVIES_FOR_AUTHOR = """
                        SELECT movies."ID", movies."Name", movies."YearOfRelease", movies."Rental ID"
                        from movies_authors left join movies on movies_authors."Movie ID"=movies."ID"
                        where movies_authors."Author ID"=%d;
                    """;
            public static final String SELECT_ALL = """
                        SELECT * from movies
                    """;
            public static final String SELECT_ONE = """
                        SELECT * from movies WHERE "%s"=%s
                    """;
            public static final String SELECT = """
                        SELECT * from movies WHERE
                    """;
            public static final String DELETE = """
                        DELETE from movies where "ID"=%d;
                    """;
            public static final String UPDATE = """
                        UPDATE movies set "Name"='%s', "YearOfRelease"=%d WHERE "ID"=%d;
                    """;
        }

        public static final class Author {
            private Author() {
                throw new AssertionError(CREATE_CONSTANT_CLASS_ERROR);
            }

            public static final String GET_AUTHORS_FOR_MOVIE = """
                        SELECT authors."ID", authors."First Name", authors."Last Name", authors."Age"\s
                        FROM movies_authors LEFT JOIN authors on authors."ID"="Author ID" where "Movie ID"=%d;
                    """;
            public static final String GET_AUTHORS_IDS_FOR_MOVIE = """
                        SELECT authors."ID" from movies_authors
                        left join authors on movies_authors."Author ID"=authors."ID"
                        where movies_authors."Movie ID"=%d;
                    """;
            public static final String GET_NUMBER_OF_AUTHORS = """
                        SELECT count(movies_authors."Author ID") from movies_authors where movies_authors."Movie ID"=%d;
                    """;
            public static final String INSERT_AUTHOR = """
                        INSERT into authors("ID", "First Name","Last Name", "Age") values
                        (%d, '%s', '%s', %d);
                    """;
            public static final String GET_AUTHOR = """
                        SELECT * from authors WHERE authors."ID"=%d;
                    """;
            public static final String SELECT_ALL = """
                        SELECT * from authors;
                    """;
            public static final String SELECT = """
                        SELECT * from authors WHERE
                    """;
            public static final String UPDATE_AUTHOR = """
                        UPDATE authors set "First Name"='%s', "Last Name"='%s', "Age"=%d WHERE "ID"=%d;\s
                    """;
            public static final String DELETE_AUTHOR = """
                        DELETE from authors WHERE "ID"=%d;
                    """;
        }

        public static final class Rental {
            private Rental() {
                throw new AssertionError(CREATE_CONSTANT_CLASS_ERROR);
            }

            public static final String GET_RENTAL_FOR_MOVIE = """
                        SELECT rentals."Start date", rentals."Finish date" from rentals where rentals."ID"=%d;
                    """;
            public static final String FIND_BY_DATES = """
                        SELECT rentals."ID" WHERE rentals."Start date"='%s' AND rentals."Finish date"='%s';
                    """;
            public static final String GET_RENTAL_ID_FOR_MOVIE = """
                       SELECT movies."Rental ID" from movies where movies."ID"=%d;
                    """;
            public static final String INSERT_RENTAL = """
                        INSERT into rentals("ID", "Start date","Finish date") values
                        (%d, '%s', '%s');
                    """;
            public static final String UPDATE_RENTAL = """
                        UPDATE rentals SET "Start date"='%s', "Finish date"='%s' WHERE "ID"=%d;
                    """;
            public static final String DELETE = """
                        DELETE from rentals where "ID"=%d
                    """;
        }
    }

    public final static class HttpStatus {
        private HttpStatus() {
            throw new AssertionError(CREATE_CONSTANT_CLASS_ERROR);
        }

        public static final int SERVER_INTERNAL_ERROR = 500;
        public static final int METHOD_NOT_ALLOWED = 405;
        public static final int NOT_FOUND = 404;
        public static final int WRONG_JSON_BODY = 400;
        public static final int OK = 200;
        public static final int CREATED = 201;
        public static final int DELETED = 204;
    }

    public final static class Message {
        private Message() {
            throw new AssertionError(CREATE_CONSTANT_CLASS_ERROR);
        }

        private static final String IO_ERROR = "I/O error";
        public static final String ERROR_STARTING_SERVER = String.format("Error starting server or %s!", IO_ERROR);
        public static final String ERROR_SENDING_RESPONSE = String.format("Error sending response or %s!", IO_ERROR);
        public static final String ERROR_NO_SUCH_MOVIE = "No movie with such an id was found";
        public static final String ERROR_NO_SUCH_AUTHOR = "No author with such an id was found!";
        public static final String ERROR_NO_SUCH_FILTER_PARAM = "No such filter parameter: %s!";
        public static final String ERROR_WRONG_REQUEST_BODY = "Wrong request body!";
        public static final String ERROR_WRONG_REQUEST_PARAM = "Wrong request parameter!";
        public static final String ERROR_WRONG_AGE = "Wrong age (must be less then 121 and more than 16)!";
        public static final String DATABASE_ERROR = "Database error!";
    }

    public final static class HttpMethod {
        private HttpMethod() {
            throw new AssertionError(CREATE_CONSTANT_CLASS_ERROR);
        }

        public static final String GET = "GET";
        public static final String POST = "POST";
        public static final String DELETE = "DELETE";
        public static final String PUT = "PUT";
    }

    public final static class Db {
        private Db() {
            throw new AssertionError(CREATE_CONSTANT_CLASS_ERROR);
        }

        public static final String CONNECTION_ERROR = "Unable to get connection to database!";
        public static final String SQL_DRIVER = "org.postgresql.Driver";
        public static final String DRIVER_ERROR = "PostgreSQL driver error!";
        public static final String DB_DATA_ERROR = "Error creating/getting data in/from database!";
    }

    public final static class Params {
        private Params() {
            throw new AssertionError(CREATE_CONSTANT_CLASS_ERROR);
        }

        public static final String NAME = "name";
        public static final String YEAR_OF_RELEASE = "yearOfRelease";
        public static final String MOVIE_ID = "movieId";
        public static final String AUTHOR_ID = "authorId";
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String AGE = "age";
    }

    public final static int ZERO = 0;
    public final static int THREE = 3;
    public final static String DB_URL_PROPERTY_KEY = "db.url";
    public final static String USER_PROPERTY_KEY = "db.username";
    public final static String PASSWORD_PROPERTY_KEY = "db.password";
}
