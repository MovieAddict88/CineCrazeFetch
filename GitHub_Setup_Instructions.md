# 🚀 GitHub Setup Instructions for JSON API

## 📁 Step 1: Create GitHub Repository

1. **Go to GitHub.com** and sign in
2. **Click "New repository"** or the "+" button
3. **Repository name**: `movie-api` (or any name you want)
4. **Make it Public** (so it can be accessed)
5. **Click "Create repository"**

## 📁 Step 2: Upload JSON File

### Method 1: Direct Upload (Easiest)
1. **In your new repository**, click **"Add file"** → **"Upload files"**
2. **Drag and drop** `free_movie_api.json` into the upload area
3. **Add commit message**: "Add movie API JSON data"
4. **Click "Commit changes"**

### Method 2: Using Git Commands
```bash
# Clone the repository
git clone https://github.com/your-username/movie-api.git
cd movie-api

# Copy the JSON file
cp /path/to/free_movie_api.json .

# Add and commit
git add free_movie_api.json
git commit -m "Add movie API JSON data"
git push origin main
```

## 📁 Step 3: Enable GitHub Pages

1. **Go to your repository** on GitHub
2. **Click "Settings"** tab
3. **Scroll down to "Pages"** section
4. **Under "Source"**, select **"Deploy from a branch"**
5. **Select "main"** branch
6. **Click "Save"**

## 📁 Step 4: Get Your JSON URL

Your JSON file will be available at:
```
https://your-username.github.io/movie-api/free_movie_api.json
```

**Example:**
- Username: `john`
- Repository: `movie-api`
- URL: `https://john.github.io/movie-api/free_movie_api.json`

## 📱 Step 5: Update Android App

### Update the URL in your Java file:

```java
// In Android_JSON_Integration.java, change this line:
private static final String JSON_API_URL = "https://your-username.github.io/movie-api/free_movie_api.json";

// Example:
private static final String JSON_API_URL = "https://john.github.io/movie-api/free_movie_api.json";
```

### Also update in MovieAPIService class:

```java
class MovieAPIService {
    private static final String BASE_URL = "https://your-username.github.io/movie-api/free_movie_api.json";
    // ... rest of the code
}
```

## 📱 Step 6: Add Dependencies to build.gradle

Add these to your `app/build.gradle`:

```gradle
dependencies {
    // For JSON parsing
    implementation 'com.google.code.gson:gson:2.10.1'
    
    // For HTTP requests
    implementation 'com.squareup.okhttp3:okhttp:4.11.0'
    
    // For image loading (optional)
    implementation 'com.github.bumptech.glide:glide:4.15.1'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.15.1'
}
```

## 📱 Step 7: Add Internet Permission

Add this to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## 📱 Step 8: Create MovieAdapter

Create a new file `MovieAdapter.java`:

```java
package com.example.movieapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    
    private List<Movie> movies = new ArrayList<>();
    private OnMovieClickListener listener;
    
    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }
    
    public void setOnMovieClickListener(OnMovieClickListener listener) {
        this.listener = listener;
    }
    
    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }
    
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie);
    }
    
    @Override
    public int getItemCount() {
        return movies.size();
    }
    
    class MovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView titleView;
        private TextView yearView;
        private TextView ratingView;
        
        public MovieViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.movieImage);
            titleView = itemView.findViewById(R.id.movieTitle);
            yearView = itemView.findViewById(R.id.movieYear);
            ratingView = itemView.findViewById(R.id.movieRating);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMovieClick(movies.get(position));
                }
            });
        }
        
        public void bind(Movie movie) {
            titleView.setText(movie.getTitle());
            yearView.setText(movie.getYear());
            ratingView.setText(String.valueOf(movie.getRating()));
            
            // Load image with Glide
            Glide.with(itemView.getContext())
                    .load(movie.getImage())
                    .into(imageView);
        }
    }
}
```

## 📱 Step 7: Create Layout Files

### activity_main.xml:
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</LinearLayout>
```

### item_movie.xml:
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="16dp">

    <ImageView
        android:id="@+id/movieImage"
        android:layout_width="80dp"
        android:layout_height="120dp"
        android:scaleType="centerCrop" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:orientation="vertical">

        <TextView
            android:id="@+id/movieTitle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textSize="18sp"
            android:textStyle="bold" />

        <TextView
            android:id="@+id/movieYear"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="4dp"
            android:textSize="14sp" />

        <TextView
            android:id="@+id/movieRating"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="4dp"
            android:textSize="14sp" />

    </LinearLayout>

</LinearLayout>
```

## 🎯 Complete Example Usage

```java
// In your MainActivity
public class MainActivity extends AppCompatActivity {
    
    private MovieAPIService apiService;
    private MovieAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        apiService = new MovieAPIService();
        adapter = new MovieAdapter();
        
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        // Set click listener
        adapter.setOnMovieClickListener(movie -> {
            // Play the movie
            playMovie(movie);
        });
        
        // Load movies
        loadMovies();
    }
    
    private void loadMovies() {
        apiService.getMovies(new MovieCallback() {
            @Override
            public void onSuccess(List<Movie> movies) {
                runOnUiThread(() -> {
                    adapter.setMovies(movies);
                    Log.d("MovieAPI", "Loaded " + movies.size() + " movies");
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void playMovie(Movie movie) {
        apiService.getMovieSources(movie.getId(), new VideoSourceCallback() {
            @Override
            public void onSuccess(List<VideoSource> sources) {
                if (!sources.isEmpty()) {
                    VideoSource source = sources.get(0);
                    String videoUrl = source.getUrl();
                    
                    // Start video player activity
                    Intent intent = new Intent(MainActivity.this, VideoPlayerActivity.class);
                    intent.putExtra("video_url", videoUrl);
                    intent.putExtra("movie_title", movie.getTitle());
                    startActivity(intent);
                }
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, "Error loading video: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```

## 🎥 Video URLs Available

Your JSON includes these real video URLs:

- **Big Buck Bunny**: `http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4`
- **Elephants Dream**: `http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4`
- **Live Stream**: `https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8`

## ✅ Testing

1. **Build and run** your Android app
2. **Check Logcat** for any errors
3. **Verify** the JSON loads correctly
4. **Test** video playback with the provided URLs

## 🔄 Updating Data

To update the movie data:
1. **Edit** `free_movie_api.json` locally
2. **Upload** the new version to GitHub
3. **Your app** will automatically get the new data

## 🎯 Summary

1. ✅ Upload JSON to GitHub
2. ✅ Enable GitHub Pages
3. ✅ Update URL in Android app
4. ✅ Add dependencies
5. ✅ Add internet permission
6. ✅ Create adapter and layouts
7. ✅ Test the app

Your Android app will now load movie data from your free GitHub-hosted JSON API! 🎬📱