package my.cinemax.app.free.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaylistData {
    @SerializedName("Categories")
    @Expose
    private List<PlaylistCategory> categories = null;

    public List<PlaylistCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<PlaylistCategory> categories) {
        this.categories = categories;
    }
}