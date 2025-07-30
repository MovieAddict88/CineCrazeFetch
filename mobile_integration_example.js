// 🎬 Mobile App Integration Example
// Free JSON API for Movie & TV Streaming Apps

class MovieAPI {
    constructor() {
        this.baseUrl = 'https://your-github-pages-url.github.io/free_movie_api.json';
        this.data = null;
    }

    // Load the JSON data
    async loadData() {
        try {
            const response = await fetch(this.baseUrl);
            this.data = await response.json();
            console.log('✅ Data loaded successfully!');
            return this.data;
        } catch (error) {
            console.error('❌ Error loading data:', error);
            return null;
        }
    }

    // Get home page data
    getHomeData() {
        if (!this.data) return null;
        return this.data.home;
    }

    // Get all movies
    getAllMovies() {
        if (!this.data) return [];
        return this.data.movies;
    }

    // Get movie by ID
    getMovieById(id) {
        if (!this.data) return null;
        return this.data.movies.find(movie => movie.id === id);
    }

    // Get all channels
    getAllChannels() {
        if (!this.data) return [];
        return this.data.channels;
    }

    // Get channel by ID
    getChannelById(id) {
        if (!this.data) return null;
        return this.data.channels.find(channel => channel.id === id);
    }

    // Get all actors
    getAllActors() {
        if (!this.data) return [];
        return this.data.actors;
    }

    // Get actor by ID
    getActorById(id) {
        if (!this.data) return null;
        return this.data.actors.find(actor => actor.id === id);
    }

    // Get all genres
    getAllGenres() {
        if (!this.data) return [];
        return this.data.genres;
    }

    // Get movies by genre
    getMoviesByGenre(genreId) {
        if (!this.data) return [];
        return this.data.movies.filter(movie => 
            movie.genres.some(genre => genre.id === genreId)
        );
    }

    // Search movies
    searchMovies(query) {
        if (!this.data) return [];
        const searchTerm = query.toLowerCase();
        return this.data.movies.filter(movie => 
            movie.title.toLowerCase().includes(searchTerm) ||
            movie.description.toLowerCase().includes(searchTerm)
        );
    }

    // Get video sources for a movie
    getMovieSources(movieId) {
        const movie = this.getMovieById(movieId);
        return movie ? movie.sources : [];
    }

    // Get live stream sources for a channel
    getChannelSources(channelId) {
        const channel = this.getChannelById(channelId);
        return channel ? channel.sources : [];
    }
}

// 📱 React Native Example
class ReactNativeExample {
    constructor() {
        this.api = new MovieAPI();
    }

    async initialize() {
        await this.api.loadData();
    }

    // Example: Display home screen
    renderHomeScreen() {
        const homeData = this.api.getHomeData();
        if (!homeData) return null;

        return {
            slides: homeData.slides,
            featuredMovies: homeData.featured_movies,
            channels: homeData.channels,
            actors: homeData.actors,
            genres: homeData.genres
        };
    }

    // Example: Display movie details
    renderMovieDetails(movieId) {
        const movie = this.api.getMovieById(movieId);
        if (!movie) return null;

        return {
            title: movie.title,
            description: movie.description,
            year: movie.year,
            rating: movie.rating,
            duration: movie.duration,
            image: movie.image,
            cover: movie.cover,
            sources: movie.sources,
            actors: movie.actors,
            genres: movie.genres,
            comments: movie.comments
        };
    }

    // Example: Play video
    playVideo(movieId, quality = '1080p') {
        const movie = this.api.getMovieById(movieId);
        if (!movie) return null;

        const source = movie.sources.find(s => s.quality === quality);
        return source ? source.url : null;
    }

    // Example: Play live stream
    playLiveStream(channelId) {
        const channel = this.api.getChannelById(channelId);
        if (!channel) return null;

        const source = channel.sources.find(s => s.type === 'live');
        return source ? source.url : null;
    }
}

// 📱 Flutter/Dart Example
class FlutterExample {
    constructor() {
        this.api = new MovieAPI();
    }

    async initialize() async {
        await this.api.loadData();
    }

    // Example: Get movie list for Flutter
    getMovieList() {
        const movies = this.api.getAllMovies();
        return movies.map(movie => ({
            id: movie.id,
            title: movie.title,
            image: movie.image,
            year: movie.year,
            rating: movie.rating,
            duration: movie.duration
        }));
    }

    // Example: Get movie details for Flutter
    getMovieDetails(movieId) {
        const movie = this.api.getMovieById(movieId);
        if (!movie) return null;

        return {
            id: movie.id,
            title: movie.title,
            description: movie.description,
            year: movie.year,
            rating: movie.rating,
            duration: movie.duration,
            image: movie.image,
            cover: movie.cover,
            videoSources: movie.sources.map(source => ({
                quality: source.quality,
                url: source.url,
                size: source.size
            })),
            actors: movie.actors,
            genres: movie.genres
        };
    }
}

// 📱 Android/Kotlin Example
class AndroidExample {
    constructor() {
        this.api = new MovieAPI();
    }

    async initialize() {
        await this.api.loadData();
    }

    // Example: Get data for Android RecyclerView
    getMovieListForAndroid() {
        const movies = this.api.getAllMovies();
        return movies.map(movie => ({
            id: movie.id,
            title: movie.title,
            imageUrl: movie.image,
            year: movie.year,
            rating: movie.rating,
            duration: movie.duration
        }));
    }

    // Example: Get movie details for Android
    getMovieDetailsForAndroid(movieId) {
        const movie = this.api.getMovieById(movieId);
        if (!movie) return null;

        return {
            id: movie.id,
            title: movie.title,
            description: movie.description,
            year: movie.year,
            rating: movie.rating,
            duration: movie.duration,
            imageUrl: movie.image,
            coverUrl: movie.cover,
            videoSources: movie.sources,
            actors: movie.actors,
            genres: movie.genres,
            comments: movie.comments
        };
    }
}

// 🎥 Video Player Integration Examples

// ExoPlayer (Android)
class ExoPlayerExample {
    playMovie(movieId, quality = '1080p') {
        const movie = this.api.getMovieById(movieId);
        if (!movie) return null;

        const source = movie.sources.find(s => s.quality === quality);
        return source ? source.url : null;
    }

    playLiveStream(channelId) {
        const channel = this.api.getChannelById(channelId);
        if (!channel) return null;

        const source = channel.sources.find(s => s.type === 'live');
        return source ? source.url : null;
    }
}

// AVPlayer (iOS)
class AVPlayerExample {
    playMovie(movieId, quality = '1080p') {
        const movie = this.api.getMovieById(movieId);
        if (!movie) return null;

        const source = movie.sources.find(s => s.quality === quality);
        return source ? source.url : null;
    }

    playLiveStream(channelId) {
        const channel = this.api.getChannelById(channelId);
        if (!channel) return null;

        const source = channel.sources.find(s => s.type === 'live');
        return source ? source.url : null;
    }
}

// 🚀 Usage Examples

// Initialize the API
const api = new MovieAPI();

// Load data and start using
async function initializeApp() {
    console.log('🎬 Loading movie data...');
    await api.loadData();
    
    // Get home data
    const homeData = api.getHomeData();
    console.log('🏠 Home data loaded:', homeData);
    
    // Get Big Buck Bunny movie
    const bigBuckBunny = api.getMovieById(1);
    console.log('🐰 Big Buck Bunny:', bigBuckBunny);
    
    // Get video sources
    const videoSources = api.getMovieSources(1);
    console.log('🎥 Video sources:', videoSources);
    
    // Get live stream
    const liveStream = api.getChannelSources(1);
    console.log('📺 Live stream:', liveStream);
    
    // Search movies
    const searchResults = api.searchMovies('bunny');
    console.log('🔍 Search results:', searchResults);
}

// Example: React Native usage
const reactNativeApp = new ReactNativeExample();
reactNativeApp.initialize().then(() => {
    const homeScreen = reactNativeApp.renderHomeScreen();
    console.log('📱 React Native Home Screen:', homeScreen);
    
    const movieDetails = reactNativeApp.renderMovieDetails(1);
    console.log('📱 React Native Movie Details:', movieDetails);
    
    const videoUrl = reactNativeApp.playVideo(1, '1080p');
    console.log('🎥 Video URL:', videoUrl);
});

// Example: Flutter usage
const flutterApp = new FlutterExample();
flutterApp.initialize().then(() => {
    const movieList = flutterApp.getMovieList();
    console.log('📱 Flutter Movie List:', movieList);
    
    const movieDetails = flutterApp.getMovieDetails(1);
    console.log('📱 Flutter Movie Details:', movieDetails);
});

// Example: Android usage
const androidApp = new AndroidExample();
androidApp.initialize().then(() => {
    const movieList = androidApp.getMovieListForAndroid();
    console.log('📱 Android Movie List:', movieList);
    
    const movieDetails = androidApp.getMovieDetailsForAndroid(1);
    console.log('📱 Android Movie Details:', movieDetails);
});

// 🎯 Video URLs for Testing
const videoUrls = {
    bigBuckBunny: 'http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
    elephantsDream: 'http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4',
    liveStream: 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'
};

console.log('🎥 Available Video URLs:', videoUrls);

// Initialize the app
initializeApp();