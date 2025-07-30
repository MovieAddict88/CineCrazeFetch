import requests
import json

# API Base URL
BASE_URL = "http://localhost:5000/api/v1"

def test_api():
    print("🎬 Testing Movie API")
    print("=" * 50)
    
    # Test 1: Login
    print("\n1. Testing Login...")
    login_data = {
        "username": "demo",
        "password": "password"
    }
    
    response = requests.post(f"{BASE_URL}/auth/login", json=login_data)
    if response.status_code == 200:
        token = response.json()['token']
        print("✅ Login successful!")
        print(f"Token: {token[:50]}...")
    else:
        print("❌ Login failed!")
        return
    
    headers = {"Authorization": f"Bearer {token}"}
    
    # Test 2: Get Home Data
    print("\n2. Testing Home Data...")
    response = requests.get(f"{BASE_URL}/home")
    if response.status_code == 200:
        data = response.json()
        print("✅ Home data retrieved!")
        print(f"Slides: {len(data['slides'])}")
        print(f"Featured Movies: {len(data['featured_movies'])}")
        print(f"Channels: {len(data['channels'])}")
        print(f"Actors: {len(data['actors'])}")
        print(f"Genres: {len(data['genres'])}")
    else:
        print("❌ Failed to get home data!")
    
    # Test 3: Get Big Buck Bunny Movie
    print("\n3. Testing Movie Details (Big Buck Bunny)...")
    response = requests.get(f"{BASE_URL}/movies/1")
    if response.status_code == 200:
        movie = response.json()
        print("✅ Movie details retrieved!")
        print(f"Title: {movie['title']}")
        print(f"Year: {movie['year']}")
        print(f"Rating: {movie['rating']}")
        print(f"Duration: {movie['duration']}")
        print(f"Sources: {len(movie['sources'])}")
        print("Video Sources:")
        for source in movie['sources']:
            print(f"  - {source['title']} ({source['quality']}): {source['url']}")
    else:
        print("❌ Failed to get movie details!")
    
    # Test 4: Search Movies
    print("\n4. Testing Movie Search...")
    response = requests.get(f"{BASE_URL}/movies/search?q=bunny")
    if response.status_code == 200:
        data = response.json()
        print("✅ Search results retrieved!")
        print(f"Found {len(data['movies'])} movies")
        for movie in data['movies']:
            print(f"  - {movie['title']} ({movie['year']})")
    else:
        print("❌ Search failed!")
    
    # Test 5: Get Channels
    print("\n5. Testing Channels...")
    response = requests.get(f"{BASE_URL}/channels")
    if response.status_code == 200:
        data = response.json()
        print("✅ Channels retrieved!")
        print(f"Found {len(data['channels'])} channels")
        for channel in data['channels']:
            print(f"  - {channel['title']} (Views: {channel['views']})")
    else:
        print("❌ Failed to get channels!")
    
    # Test 6: Get Channel Details
    print("\n6. Testing Channel Details...")
    response = requests.get(f"{BASE_URL}/channels/1")
    if response.status_code == 200:
        channel = response.json()
        print("✅ Channel details retrieved!")
        print(f"Title: {channel['title']}")
        print(f"Description: {channel['description']}")
        print(f"Views: {channel['views']}")
        print(f"Sources: {len(channel['sources'])}")
        print("Live Stream Sources:")
        for source in channel['sources']:
            print(f"  - {source['title']} ({source['quality']}): {source['url']}")
    else:
        print("❌ Failed to get channel details!")
    
    # Test 7: Get Genres
    print("\n7. Testing Genres...")
    response = requests.get(f"{BASE_URL}/genres")
    if response.status_code == 200:
        data = response.json()
        print("✅ Genres retrieved!")
        print(f"Found {len(data['genres'])} genres")
        for genre in data['genres']:
            print(f"  - {genre['title']}")
    else:
        print("❌ Failed to get genres!")
    
    # Test 8: Get Movies by Genre
    print("\n8. Testing Movies by Genre (Animation)...")
    response = requests.get(f"{BASE_URL}/movies/genre/6")
    if response.status_code == 200:
        data = response.json()
        print("✅ Movies by genre retrieved!")
        print(f"Found {len(data['movies'])} movies in Animation genre")
        for movie in data['movies']:
            print(f"  - {movie['title']} ({movie['year']})")
    else:
        print("❌ Failed to get movies by genre!")
    
    # Test 9: Add Movie View
    print("\n9. Testing Add Movie View...")
    response = requests.post(f"{BASE_URL}/movies/1/view")
    if response.status_code == 200:
        data = response.json()
        print("✅ Movie view added!")
        print(f"Total views: {data['views']}")
    else:
        print("❌ Failed to add movie view!")
    
    # Test 10: Add Comment (requires authentication)
    print("\n10. Testing Add Comment...")
    comment_data = {"comment": "Great movie! Love Big Buck Bunny!"}
    response = requests.post(f"{BASE_URL}/movies/1/comments", json=comment_data, headers=headers)
    if response.status_code == 200:
        data = response.json()
        print("✅ Comment added!")
        print(f"Comment: {data['comment']['comment']}")
    else:
        print("❌ Failed to add comment!")
    
    # Test 11: Get User Profile
    print("\n11. Testing User Profile...")
    response = requests.get(f"{BASE_URL}/user/profile", headers=headers)
    if response.status_code == 200:
        user = response.json()
        print("✅ User profile retrieved!")
        print(f"Name: {user['name']}")
        print(f"Username: {user['username']}")
        print(f"Email: {user['email']}")
    else:
        print("❌ Failed to get user profile!")
    
    # Test 12: Get Subscription Plans
    print("\n12. Testing Subscription Plans...")
    response = requests.get(f"{BASE_URL}/subscription/plans")
    if response.status_code == 200:
        data = response.json()
        print("✅ Subscription plans retrieved!")
        print(f"Found {len(data['plans'])} plans")
        for plan in data['plans']:
            print(f"  - {plan['title']}: ${plan['price']} {plan['currency']}")
            print(f"    Features: {', '.join(plan['features'])}")
    else:
        print("❌ Failed to get subscription plans!")
    
    print("\n" + "=" * 50)
    print("🎉 API Testing Complete!")
    print("\n📺 Sample Video URLs:")
    print("   Big Buck Bunny: http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
    print("   Live Stream: https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8")
    print("   Elephants Dream: http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4")

if __name__ == "__main__":
    test_api()