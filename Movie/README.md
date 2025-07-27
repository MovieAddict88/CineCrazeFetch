# Movie App - TMDB Integration

## Overview
This Android movie app has been updated to use TMDB (The Movie Database) API instead of the previous custom API, and all premium/subscription features have been removed to make the app completely free.

## Changes Made

### 1. Removed Premium Features
- Removed all subscription-related code and activities
- Deleted `PlansActivity.java` and `StripeActivity.java`
- Removed payment dependencies (PayPal, Stripe, Google IAP)
- Updated subscription checks to always return `true` (all features free)

### 2. TMDB API Integration
- Created new TMDB API client (`TMDBClient.java`)
- Created TMDB API interface (`TMDBRest.java`)
- Created TMDB entity classes for data mapping
- Created TMDB service class (`TMDBService.java`) for API calls
- Created data converter utility (`TMDBDataConverter.java`)

### 3. Updated Components
- Modified `HomeActivity.java` to remove subscription functionality
- Updated `HomeFragment.java` to use TMDB service
- Updated `Global.java` configuration
- Removed unused imports and dependencies

### 4. TMDB API Key
The app uses the provided TMDB API key: `871c8ec045dba340e55b032a0546948c`

## Features
- Browse popular movies and TV shows
- Search functionality
- Movie and TV show details
- Genre-based filtering
- Trailer playback (YouTube integration)
- Free access to all content

## API Endpoints Used
- Popular movies: `/movie/popular`
- Top rated movies: `/movie/top_rated`
- Now playing movies: `/movie/now_playing`
- Upcoming movies: `/movie/upcoming`
- Popular TV shows: `/tv/popular`
- Top rated TV shows: `/tv/top_rated`
- Movie details: `/movie/{movie_id}`
- TV show details: `/tv/{tv_id}`
- Movie videos: `/movie/{movie_id}/videos`
- TV show videos: `/tv/{tv_id}/videos`
- Search movies: `/search/movie`
- Search TV shows: `/search/tv`
- Movie genres: `/genre/movie/list`
- TV genres: `/genre/tv/list`

## Building the App
1. Open the project in Android Studio
2. Sync Gradle files
3. Build and run the app

## Notes
- The app now fetches real-time data from TMDB
- All content is free and accessible
- Image URLs are automatically converted to TMDB format
- The app maintains compatibility with existing UI components