package my.cinemax.app.free.api;

import java.util.ArrayList;
import java.util.List;

import my.cinemax.app.free.entity.Actor;
import my.cinemax.app.free.entity.Category;
import my.cinemax.app.free.entity.Channel;
import my.cinemax.app.free.entity.Data;
import my.cinemax.app.free.entity.Episode;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.PlaylistCategory;
import my.cinemax.app.free.entity.PlaylistData;
import my.cinemax.app.free.entity.PlaylistEntry;
import my.cinemax.app.free.entity.PlaylistEpisode;
import my.cinemax.app.free.entity.PlaylistSeason;
import my.cinemax.app.free.entity.PlaylistServer;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.Season;
import my.cinemax.app.free.entity.Slide;
import my.cinemax.app.free.entity.Source;

public class PlaylistDataAdapter {

    public static Data convertToAppData(PlaylistData playlistData) {
        Data data = new Data();
        
        List<Poster> allPosters = new ArrayList<>();
        List<Channel> allChannels = new ArrayList<>();
        List<Slide> slides = new ArrayList<>();

        if (playlistData.getCategories() != null) {
            for (PlaylistCategory category : playlistData.getCategories()) {
                if (category.getEntries() != null) {
                    for (PlaylistEntry entry : category.getEntries()) {
                        
                        // Check if it's Live TV (Channel) or Movies/TV Series (Poster)
                        if ("Live TV".equals(category.getMainCategory())) {
                            Channel channel = convertToChannel(entry, category);
                            allChannels.add(channel);
                        } else {
                            Poster poster = convertToPoster(entry, category);
                            allPosters.add(poster);
                            
                            // Add first few items as slides for featured content
                            if (slides.size() < 5) {
                                Slide slide = convertToSlide(entry);
                                slides.add(slide);
                            }
                        }
                    }
                }
            }
        }

        data.setPosters(allPosters);
        data.setChannels(allChannels);
        data.setSlides(slides);
        
        // Initialize other collections to empty lists to prevent null reference exceptions
        data.setActors(new ArrayList<>());
        data.setGenres(new ArrayList<>());
        
        return data;
    }

    public static Poster convertToPoster(PlaylistEntry entry, PlaylistCategory category) {
        Poster poster = new Poster();
        
        poster.setId(entry.getTitle().hashCode()); // Generate ID from title
        poster.setTitle(entry.getTitle());
        poster.setDescription(entry.getDescription());
        poster.setImage(entry.getPoster());
        poster.setCover(entry.getThumbnail());
        poster.setYear(entry.getYear() != null ? entry.getYear().toString() : "");
        poster.setDuration(entry.getDuration());
        // Handle rating that can be either Integer or String
        float rating = 0f;
        if (entry.getRating() != null) {
            try {
                if (entry.getRating() instanceof Number) {
                    rating = ((Number) entry.getRating()).floatValue();
                } else if (entry.getRating() instanceof String) {
                    String ratingStr = (String) entry.getRating();
                    // Try to parse as number, if it fails, extract numbers or set to 0
                    try {
                        rating = Float.parseFloat(ratingStr);
                    } catch (NumberFormatException e) {
                        // For ratings like "TV-Y7", "PG-13", etc., set to 0
                        rating = 0f;
                    }
                }
            } catch (Exception e) {
                rating = 0f;
            }
        }
        poster.setRating(rating);
        poster.setClassification(entry.getSubCategory());
        
        // Set type based on category
        if ("TV Series".equals(category.getMainCategory())) {
            poster.setType("serie");
        } else {
            poster.setType("movie");
        }
        
        // Convert servers to sources
        List<Source> sources = new ArrayList<>();
        if (entry.getServers() != null) {
            for (PlaylistServer server : entry.getServers()) {
                Source source = new Source();
                source.setId(server.getName().hashCode());
                source.setTitle(server.getName());
                source.setQuality(server.getName());
                source.setUrl(server.getUrl());
                source.setType("video");
                source.setExternal(false);
                sources.add(source);
            }
        }
        poster.setSources(sources);
        
        // Convert seasons if TV series
        if (entry.getSeasons() != null && !entry.getSeasons().isEmpty()) {
            // Note: Seasons would need to be handled separately in season endpoints
            // For now, we'll mark it as a series
            poster.setType("serie");
        }
        
        // Create genres from subcategory
        List<Genre> genres = new ArrayList<>();
        if (entry.getSubCategory() != null && !entry.getSubCategory().isEmpty()) {
            String[] genreNames = entry.getSubCategory().split(",");
            for (String genreName : genreNames) {
                Genre genre = new Genre();
                genre.setId(genreName.trim().hashCode());
                genre.setTitle(genreName.trim());
                genres.add(genre);
            }
        }
        poster.setGenres(genres);
        
        return poster;
    }

    public static Channel convertToChannel(PlaylistEntry entry, PlaylistCategory category) {
        Channel channel = new Channel();
        
        channel.setId(entry.getTitle().hashCode());
        channel.setTitle(entry.getTitle());
        channel.setDescription(entry.getDescription());
        channel.setImage(entry.getPoster());
        channel.setRating(entry.getRating() != null ? entry.getRating().floatValue() : 0f);
        
        // Convert servers to sources
        List<Source> sources = new ArrayList<>();
        if (entry.getServers() != null) {
            for (PlaylistServer server : entry.getServers()) {
                Source source = new Source();
                source.setId(server.getName().hashCode());
                source.setTitle(server.getName());
                source.setQuality(server.getName());
                source.setUrl(server.getUrl());
                source.setType("stream");
                source.setExternal(false);
                sources.add(source);
            }
        }
        channel.setSources(sources);
        
        return channel;
    }

    private static Slide convertToSlide(PlaylistEntry entry) {
        Slide slide = new Slide();
        
        slide.setId(entry.getTitle().hashCode());
        slide.setTitle(entry.getTitle());
        slide.setImage(entry.getPoster());
        slide.setType("poster");
        
        return slide;
    }

    public static List<Season> convertToSeasons(PlaylistEntry entry) {
        List<Season> seasons = new ArrayList<>();
        
        if (entry.getSeasons() != null) {
            for (PlaylistSeason playlistSeason : entry.getSeasons()) {
                Season season = new Season();
                season.setId(playlistSeason.getSeason());
                season.setTitle("Season " + playlistSeason.getSeason());
                
                List<Episode> episodes = new ArrayList<>();
                if (playlistSeason.getEpisodes() != null) {
                    for (PlaylistEpisode playlistEpisode : playlistSeason.getEpisodes()) {
                        Episode episode = new Episode();
                        episode.setId(playlistEpisode.getEpisode());
                        episode.setTitle(playlistEpisode.getTitle());
                        episode.setDescription(playlistEpisode.getDescription());
                        episode.setDuration(playlistEpisode.getDuration());
                        episode.setImage(playlistEpisode.getThumbnail());
                        
                        // Convert episode servers to sources
                        List<Source> episodeSources = new ArrayList<>();
                        if (playlistEpisode.getServers() != null) {
                            for (PlaylistServer server : playlistEpisode.getServers()) {
                                Source source = new Source();
                                source.setId(server.getName().hashCode());
                                source.setTitle(server.getName());
                                source.setQuality(server.getName());
                                source.setUrl(server.getUrl());
                                source.setType("video");
                                source.setExternal(false);
                                episodeSources.add(source);
                            }
                        }
                        episode.setSources(episodeSources);
                        
                        episodes.add(episode);
                    }
                }
                season.setEpisodes(episodes);
                seasons.add(season);
            }
        }
        
        return seasons;
    }
}