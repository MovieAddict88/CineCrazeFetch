package my.cinemax.app.free.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaylistEpisode {

    @SerializedName("Episode")
    @Expose
    private Integer episode;

    @SerializedName("Title")
    @Expose
    private String title;

    @SerializedName("Duration")
    @Expose
    private String duration;

    @SerializedName("Description")
    @Expose
    private String description;

    @SerializedName("Thumbnail")
    @Expose
    private String thumbnail;

    @SerializedName("Servers")
    @Expose
    private List<PlaylistServer> servers = null;

    public Integer getEpisode() {
        return episode;
    }

    public void setEpisode(Integer episode) {
        this.episode = episode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public List<PlaylistServer> getServers() {
        return servers;
    }

    public void setServers(List<PlaylistServer> servers) {
        this.servers = servers;
    }
}