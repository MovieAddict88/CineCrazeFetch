package my.cinemax.app.free.api;

import my.cinemax.app.free.entity.PlaylistData;
import retrofit2.Call;
import retrofit2.http.GET;

public interface PlaylistApiService {
    
    @GET("playlist.json")
    Call<PlaylistData> getPlaylistData();
}