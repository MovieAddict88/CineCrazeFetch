package my.cinemax.app.free.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TMDBVideoResponse {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("results")
    @Expose
    private List<TMDBVideo> results;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<TMDBVideo> getResults() {
        return results;
    }

    public void setResults(List<TMDBVideo> results) {
        this.results = results;
    }
}