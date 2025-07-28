package my.cinemax.app.free.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaylistEntry {

    @SerializedName("Title")
    @Expose
    private String title;

    @SerializedName("SubCategory")
    @Expose
    private String subCategory;

    @SerializedName("Country")
    @Expose
    private String country;

    @SerializedName("Description")
    @Expose
    private String description;

    @SerializedName("Poster")
    @Expose
    private String poster;

    @SerializedName("Thumbnail")
    @Expose
    private String thumbnail;

    @SerializedName("Rating")
    @Expose
    private Object rating; // Can be Integer or String

    @SerializedName("Duration")
    @Expose
    private String duration;

    @SerializedName("Year")
    @Expose
    private Integer year;

    @SerializedName("Servers")
    @Expose
    private List<PlaylistServer> servers = null;

    @SerializedName("Seasons")
    @Expose
    private List<PlaylistSeason> seasons = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Object getRating() {
        return rating;
    }

    public void setRating(Object rating) {
        this.rating = rating;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<PlaylistServer> getServers() {
        return servers;
    }

    public void setServers(List<PlaylistServer> servers) {
        this.servers = servers;
    }

    public List<PlaylistSeason> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<PlaylistSeason> seasons) {
        this.seasons = seasons;
    }
}