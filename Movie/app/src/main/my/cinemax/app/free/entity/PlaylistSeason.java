package my.cinemax.app.free.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaylistSeason {

    @SerializedName("Season")
    @Expose
    private Integer season;

    @SerializedName("SeasonPoster")
    @Expose
    private String seasonPoster;

    @SerializedName("Episodes")
    @Expose
    private List<PlaylistEpisode> episodes = null;

    public Integer getSeason() {
        return season;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    public String getSeasonPoster() {
        return seasonPoster;
    }

    public void setSeasonPoster(String seasonPoster) {
        this.seasonPoster = seasonPoster;
    }

    public List<PlaylistEpisode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<PlaylistEpisode> episodes) {
        this.episodes = episodes;
    }
}