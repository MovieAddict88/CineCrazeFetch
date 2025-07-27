package my.cinemax.app.free.util;

import my.cinemax.app.free.api.TMDBClient;
import my.cinemax.app.free.entity.Data;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.Source;
import my.cinemax.app.free.entity.TMDBGenre;
import my.cinemax.app.free.entity.TMDBMovie;
import my.cinemax.app.free.entity.TMDBMovieDetail;
import my.cinemax.app.free.entity.TMDBTvShow;
import my.cinemax.app.free.entity.TMDBTvShowDetail;
import my.cinemax.app.free.entity.TMDBVideo;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to convert TMDB data to app's existing format
 */
public class TMDBDataConverter {

    public static Poster convertTMDBMovieToPoster(TMDBMovie tmdbMovie) {
        Poster poster = new Poster();
        poster.setId(tmdbMovie.getId());
        poster.setTitle(tmdbMovie.getTitle());
        poster.setDescription(tmdbMovie.getOverview());
        poster.setImage(TMDBClient.getImageUrl(tmdbMovie.getPosterPath()));
        poster.setCover(TMDBClient.getImageUrl(tmdbMovie.getBackdropPath()));
        poster.setYear(tmdbMovie.getReleaseDate());
        poster.setRating(tmdbMovie.getVoteAverage() != null ? tmdbMovie.getVoteAverage().floatValue() : 0.0f);
        poster.setType("movie");
        poster.setImdb(String.valueOf(tmdbMovie.getId()));
        poster.setComment(true);
        poster.setPlayas("movie");
        poster.setDownloadas("movie");
        
        // Convert genres
        if (tmdbMovie.getGenreIds() != null) {
            List<Genre> genres = new ArrayList<>();
            for (Integer genreId : tmdbMovie.getGenreIds()) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genres.add(genre);
            }
            poster.setGenres(genres);
        }
        
        return poster;
    }

    public static Poster convertTMDBTvShowToPoster(TMDBTvShow tmdbTvShow) {
        Poster poster = new Poster();
        poster.setId(tmdbTvShow.getId());
        poster.setTitle(tmdbTvShow.getName());
        poster.setDescription(tmdbTvShow.getOverview());
        poster.setImage(TMDBClient.getImageUrl(tmdbTvShow.getPosterPath()));
        poster.setCover(TMDBClient.getImageUrl(tmdbTvShow.getBackdropPath()));
        poster.setYear(tmdbTvShow.getFirstAirDate());
        poster.setRating(tmdbTvShow.getVoteAverage() != null ? tmdbTvShow.getVoteAverage().floatValue() : 0.0f);
        poster.setType("serie");
        poster.setImdb(String.valueOf(tmdbTvShow.getId()));
        poster.setComment(true);
        poster.setPlayas("serie");
        poster.setDownloadas("serie");
        
        // Convert genres
        if (tmdbTvShow.getGenreIds() != null) {
            List<Genre> genres = new ArrayList<>();
            for (Integer genreId : tmdbTvShow.getGenreIds()) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genres.add(genre);
            }
            poster.setGenres(genres);
        }
        
        return poster;
    }

    public static Poster convertTMDBMovieDetailToPoster(TMDBMovieDetail tmdbMovieDetail) {
        Poster poster = new Poster();
        poster.setId(tmdbMovieDetail.getId());
        poster.setTitle(tmdbMovieDetail.getTitle());
        poster.setDescription(tmdbMovieDetail.getOverview());
        poster.setImage(TMDBClient.getImageUrl(tmdbMovieDetail.getPosterPath()));
        poster.setCover(TMDBClient.getImageUrl(tmdbMovieDetail.getBackdropPath()));
        poster.setYear(tmdbMovieDetail.getReleaseDate());
        poster.setRating(tmdbMovieDetail.getVoteAverage() != null ? tmdbMovieDetail.getVoteAverage().floatValue() : 0.0f);
        poster.setType("movie");
        poster.setImdb(String.valueOf(tmdbMovieDetail.getId()));
        poster.setComment(true);
        poster.setPlayas("movie");
        poster.setDownloadas("movie");
        poster.setDuration(tmdbMovieDetail.getRuntime() != null ? tmdbMovieDetail.getRuntime().toString() + " min" : "");
        
        // Convert genres
        if (tmdbMovieDetail.getGenres() != null) {
            List<Genre> genres = new ArrayList<>();
            for (TMDBGenre tmdbGenre : tmdbMovieDetail.getGenres()) {
                Genre genre = new Genre();
                genre.setId(tmdbGenre.getId());
                genre.setName(tmdbGenre.getName());
                genres.add(genre);
            }
            poster.setGenres(genres);
        }
        
        return poster;
    }

    public static Poster convertTMDBTvShowDetailToPoster(TMDBTvShowDetail tmdbTvShowDetail) {
        Poster poster = new Poster();
        poster.setId(tmdbTvShowDetail.getId());
        poster.setTitle(tmdbTvShowDetail.getName());
        poster.setDescription(tmdbTvShowDetail.getOverview());
        poster.setImage(TMDBClient.getImageUrl(tmdbTvShowDetail.getPosterPath()));
        poster.setCover(TMDBClient.getImageUrl(tmdbTvShowDetail.getBackdropPath()));
        poster.setYear(tmdbTvShowDetail.getFirstAirDate());
        poster.setRating(tmdbTvShowDetail.getVoteAverage() != null ? tmdbTvShowDetail.getVoteAverage().floatValue() : 0.0f);
        poster.setType("serie");
        poster.setImdb(String.valueOf(tmdbTvShowDetail.getId()));
        poster.setComment(true);
        poster.setPlayas("serie");
        poster.setDownloadas("serie");
        
        // Set duration based on episode runtime
        if (tmdbTvShowDetail.getEpisodeRunTime() != null && !tmdbTvShowDetail.getEpisodeRunTime().isEmpty()) {
            poster.setDuration(tmdbTvShowDetail.getEpisodeRunTime().get(0).toString() + " min");
        }
        
        // Convert genres
        if (tmdbTvShowDetail.getGenres() != null) {
            List<Genre> genres = new ArrayList<>();
            for (TMDBGenre tmdbGenre : tmdbTvShowDetail.getGenres()) {
                Genre genre = new Genre();
                genre.setId(tmdbGenre.getId());
                genre.setName(tmdbGenre.getName());
                genres.add(genre);
            }
            poster.setGenres(genres);
        }
        
        return poster;
    }

    public static List<Source> convertTMDBVideosToSources(List<TMDBVideo> tmdbVideos) {
        List<Source> sources = new ArrayList<>();
        if (tmdbVideos != null) {
            for (TMDBVideo tmdbVideo : tmdbVideos) {
                if ("YouTube".equals(tmdbVideo.getSite()) && "Trailer".equals(tmdbVideo.getType())) {
                    Source source = new Source();
                    source.setId(tmdbVideo.getId());
                    source.setTitle(tmdbVideo.getName());
                    source.setType("youtube");
                    source.setUrl("https://www.youtube.com/watch?v=" + tmdbVideo.getKey());
                    source.setQuality("HD");
                    sources.add(source);
                }
            }
        }
        return sources;
    }

    public static Genre convertTMDBGenreToGenre(TMDBGenre tmdbGenre) {
        Genre genre = new Genre();
        genre.setId(tmdbGenre.getId());
        genre.setName(tmdbGenre.getName());
        return genre;
    }

    public static Data createHomeData(List<TMDBMovie> popularMovies, List<TMDBMovie> topRatedMovies, 
                                    List<TMDBTvShow> popularTvShows, List<TMDBTvShow> topRatedTvShows) {
        Data data = new Data();
        
        // Convert movies to posters
        List<Poster> moviePosters = new ArrayList<>();
        if (popularMovies != null) {
            for (TMDBMovie movie : popularMovies) {
                moviePosters.add(convertTMDBMovieToPoster(movie));
            }
        }
        data.setPosters(moviePosters);
        
        return data;
    }
}