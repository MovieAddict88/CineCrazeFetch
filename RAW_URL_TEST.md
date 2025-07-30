# Raw URL Test Guide

## Test the Raw GitHub URL

### Step 1: Test in Browser
Open this URL in your browser:
```
https://raw.githubusercontent.com/MovieAddict88/movie-api/main/free_movie_api.json
```

**Expected Result**: You should see pure JSON content like:
```json
{
  "api_info": {
    "version": "1.0.0",
    "description": "Free Movie API",
    "last_updated": "2024-01-01"
  },
  "home": {
    "slides": [...],
    "channels": [...],
    "actors": [...],
    "genres": [...]
  },
  "movies": [...],
  "channels": [...],
  "actors": [...],
  "genres": [...]
}
```

**If you see HTML or an error**: The file doesn't exist or there's an issue with the repository.

### Step 2: Test in Android App
1. Build and run your app
2. Check the logs for:
   ```
   JSON_API: Starting to load home data from JSON API...
   JSON_API: Response received. Success: true, Code: 200
   JSON_API: Successfully loaded home data from JSON API
   ```

### Step 3: Alternative URLs to Try

If the raw URL doesn't work, try these alternatives:

**A. Direct GitHub Pages with .json extension:**
```
https://MovieAddict88.github.io/movie-api/free_movie_api.json
```

**B. GitHub API:**
```
https://api.github.com/repos/MovieAddict88/movie-api/contents/free_movie_api.json
```

**C. Test with a public JSON:**
```
https://jsonplaceholder.typicode.com/posts/1
```

### Step 4: Update Code for Different URLs

If you need to use a different URL, update these files:

**For GitHub Pages:**
```java
// Global.java
public static final String API_URL = "https://MovieAddict88.github.io/movie-api/free_movie_api.json";

// apiClient.java
.baseUrl("https://MovieAddict88.github.io/movie-api/")
```

**For GitHub API:**
```java
// Global.java
public static final String API_URL = "https://api.github.com/repos/MovieAddict88/movie-api/contents/free_movie_api.json";

// apiClient.java
.baseUrl("https://api.github.com/")
```

**For Test JSON:**
```java
// Global.java
public static final String API_URL = "https://jsonplaceholder.typicode.com/posts/1";

// apiClient.java
.baseUrl("https://jsonplaceholder.typicode.com/")
```

## Expected Results

**Success**: App loads data and shows content in all tabs
**Failure**: Check logs for specific error messages and try alternative URLs