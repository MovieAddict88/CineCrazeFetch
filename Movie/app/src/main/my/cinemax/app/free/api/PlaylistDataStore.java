package my.cinemax.app.free.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.cinemax.app.free.entity.PlaylistCategory;
import my.cinemax.app.free.entity.PlaylistData;
import my.cinemax.app.free.entity.PlaylistEntry;
import my.cinemax.app.free.entity.Season;

public class PlaylistDataStore {
    
    private static PlaylistDataStore instance;
    private PlaylistData playlistData;
    private Map<Integer, PlaylistEntry> entryCache = new HashMap<>();
    
    public static PlaylistDataStore getInstance() {
        if (instance == null) {
            instance = new PlaylistDataStore();
        }
        return instance;
    }
    
    public void setPlaylistData(PlaylistData data) {
        this.playlistData = data;
        updateEntryCache();
    }
    
    private void updateEntryCache() {
        entryCache.clear();
        if (playlistData != null && playlistData.getCategories() != null) {
            for (PlaylistCategory category : playlistData.getCategories()) {
                if (category.getEntries() != null) {
                    for (PlaylistEntry entry : category.getEntries()) {
                        entryCache.put(entry.getTitle().hashCode(), entry);
                    }
                }
            }
        }
    }
    
    public PlaylistEntry getEntryById(Integer id) {
        return entryCache.get(id);
    }
    
    public List<Season> getSeasonsForEntry(Integer entryId) {
        PlaylistEntry entry = getEntryById(entryId);
        if (entry != null) {
            return PlaylistDataAdapter.convertToSeasons(entry);
        }
        return new ArrayList<>();
    }
    
    public PlaylistData getPlaylistData() {
        return playlistData;
    }
}