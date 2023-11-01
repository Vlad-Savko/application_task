DROP TABLE IF EXISTS public.authors;
DROP TABLE IF EXISTS public.movies;
DROP TABLE IF EXISTS public.movies_authors;
DROP TABLE IF EXISTS public.rentals;


CREATE TABLE IF NOT EXISTS public.authors
(
    "ID" serial NOT NULL,
    "First Name" character varying(100) NOT NULL,
    "Last Name" character varying(100) NOT NULL,
    "Age" integer NOT NULL,
    PRIMARY KEY ("ID")
    );

CREATE TABLE IF NOT EXISTS public.movies
(
    "ID" serial NOT NULL,
    "Name" character varying(100) NOT NULL,
    "YearOfRelease" integer NOT NULL,
    "Rental ID" bigint NOT NULL,
    PRIMARY KEY ("ID")
    );

CREATE TABLE IF NOT EXISTS public.movies_authors
(
    "ID" serial NOT NULL,
    "Movie ID" bigint NOT NULL,
    "Author ID" bigint NOT NULL,
    PRIMARY KEY ("ID")
    );

CREATE TABLE IF NOT EXISTS public.rentals
(
    "ID" bigint NOT NULL,
    "Start date" date NOT NULL,
    "Finish date" date NOT NULL,
    PRIMARY KEY ("ID")
    );