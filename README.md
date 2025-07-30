# 🎬 Movie & TV Streaming API

A complete RESTful API for a movie and TV streaming platform, converted from an Android app structure. This API includes authentication, movie/channel management, user profiles, comments, ratings, and subscription features.

## 🚀 Features

- **Authentication System** - JWT-based authentication
- **Movie Management** - Full CRUD operations for movies and series
- **Channel Streaming** - Live TV channels with HLS streams
- **User Management** - Profiles, watchlists, and preferences
- **Comments & Ratings** - Social features for users
- **Search & Filtering** - Advanced search with genre/category filters
- **Subscription Plans** - Multiple payment options (PayPal, Stripe, Cash)
- **Video Sources** - Multiple quality options and external sources
- **Subtitles Support** - Multi-language subtitle support

## 📺 Sample Content

The API includes sample data with real video sources:

- **Big Buck Bunny** - Famous open-source animation
- **Live Streams** - Test HLS streams for channels
- **Sample Series** - TV series with episodes
- **News & Sports Channels** - Live TV channels

## 🛠️ Installation

### Prerequisites

- Python 3.7+
- pip

### Setup

1. **Clone or download the files**
2. **Install dependencies:**
   ```bash
   pip install -r requirements.txt
   ```

3. **Run the API server:**
   ```bash
   python sample_api_implementation.py
   ```

4. **Test the API:**
   ```bash
   python test_api.py
   ```

## 🔗 API Endpoints

### Authentication
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/register` - User registration

### Home & Content
- `GET /api/v1/home` - Get home page data (slides, featured content)
- `GET /api/v1/movies/{id}` - Get movie details
- `GET /api/v1/channels/{id}` - Get channel details
- `GET /api/v1/movies/search?q={query}` - Search movies

### Categories & Filters
- `GET /api/v1/genres` - Get all genres
- `GET /api/v1/categories` - Get all categories
- `GET /api/v1/countries` - Get all countries
- `GET /api/v1/movies/genre/{id}` - Get movies by genre

### User Features
- `GET /api/v1/user/profile` - Get user profile
- `GET /api/v1/user/my-list` - Get user's watchlist
- `POST /api/v1/movies/{id}/comments` - Add movie comment
- `POST /api/v1/movies/{id}/view` - Add movie view

### Subscriptions
- `GET /api/v1/subscription/plans` - Get subscription plans

## 🎥 Video Sources

### Big Buck Bunny
- **1080p**: `http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4`
- **720p**: Same URL (client handles quality)
- **480p**: Same URL (client handles quality)

### Live Streams
- **Test HLS Stream**: `https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8`

### Additional Sample Videos
- **Elephants Dream**: `http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4`

## 🔐 Authentication

### Login
```bash
curl -X POST http://localhost:5000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "demo", "password": "password"}'
```

### Using Token
```bash
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:5000/api/v1/user/profile
```

## 📊 Sample Data Structure

### Movie Object
```json
{
  "id": 1,
  "title": "Big Buck Bunny",
  "type": "movie",
  "description": "Big Buck Bunny tells the story of a giant rabbit...",
  "year": "2008",
  "duration": "10:00",
  "rating": 8.5,
  "image": "https://peach.blender.org/wp-content/uploads/title_anouncement.jpg",
  "sources": [
    {
      "id": 1,
      "type": "video",
      "title": "Big Buck Bunny 1080p",
      "quality": "1080p",
      "url": "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    }
  ],
  "actors": [...],
  "genres": [...],
  "subtitles": [...]
}
```

### Channel Object
```json
{
  "id": 1,
  "title": "Sample News Channel",
  "description": "24/7 news coverage from around the world",
  "views": 25000,
  "rating": 7.8,
  "sources": [
    {
      "id": 6,
      "type": "live",
      "title": "Live Stream",
      "quality": "1080p",
      "url": "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
    }
  ]
}
```

## 🧪 Testing

Run the test script to verify all endpoints:

```bash
python test_api.py
```

This will test:
- ✅ Authentication
- ✅ Home data retrieval
- ✅ Movie details with video sources
- ✅ Search functionality
- ✅ Channel streaming
- ✅ User profiles
- ✅ Comments and ratings
- ✅ Subscription plans

## 🌐 API Documentation

The complete API structure is defined in `api_structure.json` with:
- All endpoints and their parameters
- Request/response formats
- Data models
- Error handling
- Rate limiting specifications

## 🔧 Customization

### Adding New Movies
Edit the `init_sample_data()` function in `sample_api_implementation.py`:

```python
movies_db[3] = {
    "id": 3,
    "title": "Your New Movie",
    "type": "movie",
    "description": "Your movie description",
    "year": "2024",
    "sources": [
        {
            "id": 8,
            "type": "video",
            "title": "Your Movie 1080p",
            "quality": "1080p",
            "url": "https://your-video-url.mp4"
        }
    ]
}
```

### Adding New Channels
```python
channels_db[3] = {
    "id": 3,
    "title": "Your New Channel",
    "description": "Channel description",
    "sources": [
        {
            "id": 9,
            "type": "live",
            "title": "Live Stream",
            "quality": "720p",
            "url": "https://your-live-stream.m3u8"
        }
    ]
}
```

## 🚀 Deployment

### Local Development
```bash
python sample_api_implementation.py
```

### Production (with Gunicorn)
```bash
pip install gunicorn
gunicorn -w 4 -b 0.0.0.0:5000 sample_api_implementation:app
```

### Docker
```dockerfile
FROM python:3.9-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install -r requirements.txt
COPY . .
EXPOSE 5000
CMD ["python", "sample_api_implementation.py"]
```

## 📱 Client Integration

### JavaScript/React Example
```javascript
// Login
const login = async (username, password) => {
  const response = await fetch('/api/v1/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  return response.json();
};

// Get movie with video sources
const getMovie = async (id) => {
  const response = await fetch(`/api/v1/movies/${id}`);
  return response.json();
};
```

### Android/Kotlin Example
```kotlin
// Using Retrofit
@GET("movies/{id}")
suspend fun getMovie(@Path("id") id: Int): Movie

@POST("auth/login")
suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
```

## 🔒 Security Features

- JWT token authentication
- CORS enabled for cross-origin requests
- Input validation
- Rate limiting (configurable)
- Secure headers

## 📈 Performance

- Lightweight Flask implementation
- In-memory data storage (replace with database for production)
- Efficient JSON responses
- CORS support for web clients

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Add your changes
4. Test thoroughly
5. Submit a pull request

## 📄 License

This project is open source and available under the MIT License.

---

**🎬 Ready to stream!** Start the server and test the API with real video content including Big Buck Bunny and live streams.