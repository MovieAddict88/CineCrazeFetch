package my.cinemax.app.free.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaylistCategory {
    @SerializedName("MainCategory")
    @Expose
    private String mainCategory;

    @SerializedName("SubCategories")
    @Expose
    private List<String> subCategories = null;

    @SerializedName("Entries")
    @Expose
    private List<PlaylistEntry> entries = null;

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public List<String> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<String> subCategories) {
        this.subCategories = subCategories;
    }

    public List<PlaylistEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<PlaylistEntry> entries) {
        this.entries = entries;
    }
}