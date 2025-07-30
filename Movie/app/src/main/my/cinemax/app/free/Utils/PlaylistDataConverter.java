package my.cinemax.app.free.Utils;

import java.util.ArrayList;
import java.util.List;

import my.cinemax.app.free.entity.Channel;
import my.cinemax.app.free.entity.Data;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.PlaylistCategory;
import my.cinemax.app.free.entity.PlaylistData;
import my.cinemax.app.free.entity.PlaylistEntry;
import my.cinemax.app.free.entity.PlaylistServer;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.Season;
import my.cinemax.app.free.entity.Source;

public class PlaylistDataConverter {

    public static Data convertPlaylistDataToAppData(PlaylistData playlistData) {
        Data appData = new Data();
        
        List<Channel> channels = new ArrayList<>();
        List<Poster> movies = new ArrayList<>();
        List<Poster> series = new ArrayList<>();
        List<Genre> genres = new ArrayList<>();
        
        if (playlistData != null && playlistData.getCategories() != null) {
            for (PlaylistCategory category : playlistData.getCategories()) {
                String mainCategory = category.getMainCategory();
                
                if ("Live TV".equals(mainCategory)) {
                    // Convert Live TV entries to Channels
                    if (category.getEntries() != null) {
                        for (PlaylistEntry entry : category.getEntries()) {
                            Channel channel = convertEntryToChannel(entry);
                            channels.add(channel);
                        }
                    }
                } else if ("Movies".equals(mainCategory)) {
                    // Convert Movie entries to Posters
                    if (category.getEntries() != null) {
                        for (PlaylistEntry entry : category.getEntries()) {
                            Poster movie = convertEntryToMovie(entry);
                            movies.add(movie);
                        }
                    }
                } else if ("TV Series".equals(mainCategory)) {
                    // Convert TV Series entries to Posters with seasons
                    if (category.getEntries() != null) {
                        for (PlaylistEntry entry : category.getEntries()) {
                            Poster seriesPoster = convertEntryToSeries(entry);
                            series.add(seriesPoster);
                        }
                    }
                }
                
                // Create genres from subcategories
                if (category.getSubCategories() != null) {
                    for (String subCategory : category.getSubCategories()) {
                        Genre genre = new Genre();
                        genre.setTitle(subCategory);
                        genre.setId(genres.size() + 1);
                        genres.add(genre);
                    }
                }
            }
        }
        
        // Combine movies and series into posters list
        List<Poster> allPosters = new ArrayList<>();
        allPosters.addAll(movies);
        allPosters.addAll(series);
        
        appData.setChannels(channels);
        appData.setPosters(allPosters);
        appData.setGenres(genres);
        
        return appData;
    }
    
    private static Channel convertEntryToChannel(PlaylistEntry entry) {
        Channel channel = new Channel();
        channel.setId(generateId(entry.getTitle()));
        channel.setTitle(entry.getTitle());
        channel.setDescription(entry.getDescription());
        channel.setImage(entry.getPoster());
        channel.setRating(entry.getRating() != null ? entry.getRating().floatValue() : 0.0f);
        
        // Convert servers to sources
        if (entry.getServers() != null) {
            List<Source> sources = new ArrayList<>();
            for (PlaylistServer server : entry.getServers()) {
                Source source = new Source();
                source.setTitle(server.getName());
                source.setUrl(server.getUrl());
                source.setQuality(server.getName());
                sources.add(source);
            }
            channel.setSources(sources);
        }
        
        return channel;
    }
    
    private static Poster convertEntryToMovie(PlaylistEntry entry) {
        Poster movie = new Poster();
        movie.setId(generateId(entry.getTitle()));
        movie.setTitle(entry.getTitle());
        movie.setDescription(entry.getDescription());
        movie.setImage(entry.getPoster());
        movie.setCover(entry.getThumbnail());
        movie.setRating(entry.getRating() != null ? entry.getRating().floatValue() : 0.0f);
        movie.setDuration(entry.getDuration());
        movie.setYear(entry.getYear() != null ? entry.getYear().toString() : "");
        movie.setType("movie");
        
        // Convert servers to sources
        if (entry.getServers() != null) {
            List<Source> sources = new ArrayList<>();
            for (PlaylistServer server : entry.getServers()) {
                Source source = new Source();
                source.setTitle(server.getName());
                source.setUrl(server.getUrl());
                source.setQuality(server.getName());
                sources.add(source);
            }
            movie.setSources(sources);
        }
        
        return movie;
    }
    
    private static Poster convertEntryToSeries(PlaylistEntry entry) {
        Poster series = new Poster();
        series.setId(generateId(entry.getTitle()));
        series.setTitle(entry.getTitle());
        series.setDescription(entry.getDescription());
        series.setImage(entry.getPoster());
        series.setCover(entry.getThumbnail());
        series.setRating(entry.getRating() != null ? entry.getRating().floatValue() : 0.0f);
        series.setYear(entry.getYear() != null ? entry.getYear().toString() : "");
        series.setType("serie");
        
        // Convert seasons
        if (entry.getSeasons() != null) {
            List<Season> seasons = new ArrayList<>();
            for (my.cinemax.app.free.entity.PlaylistSeason playlistSeason : entry.getSeasons()) {
                Season season = new Season();
                season.setId(playlistSeason.getSeason());
                season.setTitle("Season " + playlistSeason.getSeason());
                
                // Convert episodes
                if (playlistSeason.getEpisodes() != null) {
                    List<my.cinemax.app.free.entity.Episode> episodes = new ArrayList<>();
                    for (my.cinemax.app.free.entity.PlaylistEpisode playlistEpisode : playlistSeason.getEpisodes()) {
                        my.cinemax.app.free.entity.Episode episode = new my.cinemax.app.free.entity.Episode();
                        episode.setId(playlistEpisode.getEpisode());
                        episode.setTitle(playlistEpisode.getTitle());
                        episode.setDescription(playlistEpisode.getDescription());
                        episode.setDuration(playlistEpisode.getDuration());
                        episode.setImage(playlistEpisode.getThumbnail());
                        
                        // Convert episode servers to sources
                        if (playlistEpisode.getServers() != null) {
                            List<Source> sources = new ArrayList<>();
                            for (PlaylistServer server : playlistEpisode.getServers()) {
                                Source source = new Source();
                                source.setTitle(server.getName());
                                source.setUrl(server.getUrl());
                                source.setQuality(server.getName());
                                sources.add(source);
                            }
                            episode.setSources(sources);
                        }
                        
                        episodes.add(episode);
                    }
                    season.setEpisodes(episodes);
                }
                
                seasons.add(season);
            }
            // Note: The existing Poster class doesn't have a seasons field, so we'll need to handle this differently
        }
        
        return series;
    }
    
    private static int generateId(String title) {
        return title != null ? title.hashCode() : 0;
    }
}