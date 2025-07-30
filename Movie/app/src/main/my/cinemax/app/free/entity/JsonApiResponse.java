package my.cinemax.app.free.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Response class for the free JSON API
 * This matches the structure of free_movie_api.json
 */
public class JsonApiResponse {
    
    @SerializedName("api_info")
    @Expose
    private ApiInfo apiInfo;
    
    @SerializedName("home")
    @Expose
    private HomeData home;
    
    @SerializedName("movies")
    @Expose
    private List<Poster> movies;
    
    @SerializedName("channels")
    @Expose
    private List<Channel> channels;
    
    @SerializedName("actors")
    @Expose
    private List<Actor> actors;
    
    @SerializedName("genres")
    @Expose
    private List<Genre> genres;
    
    @SerializedName("categories")
    @Expose
    private List<Category> categories;
    
    @SerializedName("countries")
    @Expose
    private List<Country> countries;
    
    @SerializedName("subscription_plans")
    @Expose
    private List<Plan> subscriptionPlans;
    
    @SerializedName("video_sources")
    @Expose
    private VideoSources videoSources;
    
    // Getters and Setters
    public ApiInfo getApiInfo() {
        return apiInfo;
    }
    
    public void setApiInfo(ApiInfo apiInfo) {
        this.apiInfo = apiInfo;
    }
    
    public HomeData getHome() {
        return home;
    }
    
    public void setHome(HomeData home) {
        this.home = home;
    }
    
    public List<Poster> getMovies() {
        return movies;
    }
    
    public void setMovies(List<Poster> movies) {
        this.movies = movies;
    }
    
    public List<Channel> getChannels() {
        return channels;
    }
    
    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }
    
    public List<Actor> getActors() {
        return actors;
    }
    
    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }
    
    public List<Genre> getGenres() {
        return genres;
    }
    
    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
    
    public List<Category> getCategories() {
        return categories;
    }
    
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
    
    public List<Country> getCountries() {
        return countries;
    }
    
    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }
    
    public List<Plan> getSubscriptionPlans() {
        return subscriptionPlans;
    }
    
    public void setSubscriptionPlans(List<Plan> subscriptionPlans) {
        this.subscriptionPlans = subscriptionPlans;
    }
    
    public VideoSources getVideoSources() {
        return videoSources;
    }
    
    public void setVideoSources(VideoSources videoSources) {
        this.videoSources = videoSources;
    }
    
    // Inner classes for JSON structure
    public static class ApiInfo {
        @SerializedName("version")
        @Expose
        private String version;
        
        @SerializedName("description")
        @Expose
        private String description;
        
        @SerializedName("last_updated")
        @Expose
        private String lastUpdated;
        
        @SerializedName("total_movies")
        @Expose
        private Integer totalMovies;
        
        @SerializedName("total_channels")
        @Expose
        private Integer totalChannels;
        
        @SerializedName("total_actors")
        @Expose
        private Integer totalActors;
        
        // Getters and Setters
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
        
        public Integer getTotalMovies() { return totalMovies; }
        public void setTotalMovies(Integer totalMovies) { this.totalMovies = totalMovies; }
        
        public Integer getTotalChannels() { return totalChannels; }
        public void setTotalChannels(Integer totalChannels) { this.totalChannels = totalChannels; }
        
        public Integer getTotalActors() { return totalActors; }
        public void setTotalActors(Integer totalActors) { this.totalActors = totalActors; }
    }
    
    public static class HomeData {
        @SerializedName("slides")
        @Expose
        private List<Slide> slides;
        
        @SerializedName("featured_movies")
        @Expose
        private List<Poster> featuredMovies;
        
        @SerializedName("channels")
        @Expose
        private List<Channel> channels;
        
        @SerializedName("actors")
        @Expose
        private List<Actor> actors;
        
        @SerializedName("genres")
        @Expose
        private List<Genre> genres;
        
        // Getters and Setters
        public List<Slide> getSlides() { return slides; }
        public void setSlides(List<Slide> slides) { this.slides = slides; }
        
        public List<Poster> getFeaturedMovies() { return featuredMovies; }
        public void setFeaturedMovies(List<Poster> featuredMovies) { this.featuredMovies = featuredMovies; }
        
        public List<Channel> getChannels() { return channels; }
        public void setChannels(List<Channel> channels) { this.channels = channels; }
        
        public List<Actor> getActors() { return actors; }
        public void setActors(List<Actor> actors) { this.actors = actors; }
        
        public List<Genre> getGenres() { return genres; }
        public void setGenres(List<Genre> genres) { this.genres = genres; }
    }
    
    public static class VideoSources {
        @SerializedName("big_buck_bunny")
        @Expose
        private VideoSource bigBuckBunny;
        
        @SerializedName("elephants_dream")
        @Expose
        private VideoSource elephantsDream;
        
        @SerializedName("live_streams")
        @Expose
        private LiveStreams liveStreams;
        
        // Getters and Setters
        public VideoSource getBigBuckBunny() { return bigBuckBunny; }
        public void setBigBuckBunny(VideoSource bigBuckBunny) { this.bigBuckBunny = bigBuckBunny; }
        
        public VideoSource getElephantsDream() { return elephantsDream; }
        public void setElephantsDream(VideoSource elephantsDream) { this.elephantsDream = elephantsDream; }
        
        public LiveStreams getLiveStreams() { return liveStreams; }
        public void setLiveStreams(LiveStreams liveStreams) { this.liveStreams = liveStreams; }
    }
    
    public static class VideoSource {
        @SerializedName("title")
        @Expose
        private String title;
        
        @SerializedName("description")
        @Expose
        private String description;
        
        @SerializedName("urls")
        @Expose
        private VideoUrls urls;
        
        @SerializedName("duration")
        @Expose
        private String duration;
        
        @SerializedName("size")
        @Expose
        private String size;
        
        @SerializedName("license")
        @Expose
        private String license;
        
        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public VideoUrls getUrls() { return urls; }
        public void setUrls(VideoUrls urls) { this.urls = urls; }
        
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
        
        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }
        
        public String getLicense() { return license; }
        public void setLicense(String license) { this.license = license; }
    }
    
    public static class VideoUrls {
        @SerializedName("1080p")
        @Expose
        private String p1080;
        
        @SerializedName("720p")
        @Expose
        private String p720;
        
        @SerializedName("480p")
        @Expose
        private String p480;
        
        // Getters and Setters
        public String getP1080() { return p1080; }
        public void setP1080(String p1080) { this.p1080 = p1080; }
        
        public String getP720() { return p720; }
        public void setP720(String p720) { this.p720 = p720; }
        
        public String getP480() { return p480; }
        public void setP480(String p480) { this.p480 = p480; }
    }
    
    public static class LiveStreams {
        @SerializedName("test_hls")
        @Expose
        private LiveStream testHls;
        
        // Getters and Setters
        public LiveStream getTestHls() { return testHls; }
        public void setTestHls(LiveStream testHls) { this.testHls = testHls; }
    }
    
    public static class LiveStream {
        @SerializedName("title")
        @Expose
        private String title;
        
        @SerializedName("description")
        @Expose
        private String description;
        
        @SerializedName("url")
        @Expose
        private String url;
        
        @SerializedName("quality")
        @Expose
        private String quality;
        
        @SerializedName("type")
        @Expose
        private String type;
        
        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        
        public String getQuality() { return quality; }
        public void setQuality(String quality) { this.quality = quality; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}