from flask import Flask, request, jsonify
from flask_cors import CORS
import jwt
import datetime
import uuid
from functools import wraps

app = Flask(__name__)
app.config['SECRET_KEY'] = 'your-secret-key-here'
CORS(app)

# Sample Database (In real implementation, use a proper database)
users_db = {}
movies_db = {}
channels_db = {}
actors_db = {}
genres_db = {}
categories_db = {}
countries_db = {}

# Initialize sample data
def init_sample_data():
    # Sample Genres
    genres_db[1] = {"id": 1, "title": "Action"}
    genres_db[2] = {"id": 2, "title": "Comedy"}
    genres_db[3] = {"id": 3, "title": "Drama"}
    genres_db[4] = {"id": 4, "title": "Horror"}
    genres_db[5] = {"id": 5, "title": "Sci-Fi"}
    genres_db[6] = {"id": 6, "title": "Animation"}

    # Sample Categories
    categories_db[1] = {"id": 1, "title": "News"}
    categories_db[2] = {"id": 2, "title": "Sports"}
    categories_db[3] = {"id": 3, "title": "Entertainment"}
    categories_db[4] = {"id": 4, "title": "Documentary"}

    # Sample Countries
    countries_db[1] = {"id": 1, "title": "USA"}
    countries_db[2] = {"id": 2, "title": "UK"}
    countries_db[3] = {"id": 3, "title": "France"}
    countries_db[4] = {"id": 4, "title": "Germany"}

    # Sample Actors
    actors_db[1] = {
        "id": 1,
        "name": "Tom Hanks",
        "type": "actor",
        "role": "Lead Actor",
        "image": "https://example.com/tom-hanks.jpg",
        "born": "1956-07-09",
        "height": "6'0\"",
        "bio": "American actor and filmmaker"
    }
    actors_db[2] = {
        "id": 2,
        "name": "Emma Watson",
        "type": "actress",
        "role": "Lead Actress",
        "image": "https://example.com/emma-watson.jpg",
        "born": "1990-04-15",
        "height": "5'5\"",
        "bio": "English actress and activist"
    }

    # Sample Movies with Big Buck Bunny
    movies_db[1] = {
        "id": 1,
        "title": "Big Buck Bunny",
        "type": "movie",
        "label": "Animation",
        "sublabel": "Short Film",
        "imdb": "8.5",
        "downloadas": "big-buck-bunny.mp4",
        "comment": True,
        "playas": "video",
        "description": "Big Buck Bunny tells the story of a giant rabbit with a heart bigger than himself. When one sunny day three rodents rudely harass him by throwing nuts, hitting his flowers, and destroying his home, he snaps and goes after them.",
        "classification": "G",
        "year": "2008",
        "duration": "10:00",
        "rating": 8.5,
        "image": "https://peach.blender.org/wp-content/uploads/title_anouncement.jpg?x11217",
        "cover": "https://peach.blender.org/wp-content/uploads/title_anouncement.jpg?x11217",
        "genres": [genres_db[6]],
        "sources": [
            {
                "id": 1,
                "type": "video",
                "title": "Big Buck Bunny 1080p",
                "quality": "1080p",
                "size": "264MB",
                "kind": "mp4",
                "premium": "false",
                "external": False,
                "url": "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            },
            {
                "id": 2,
                "type": "video",
                "title": "Big Buck Bunny 720p",
                "quality": "720p",
                "size": "128MB",
                "kind": "mp4",
                "premium": "false",
                "external": False,
                "url": "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            },
            {
                "id": 3,
                "type": "video",
                "title": "Big Buck Bunny 480p",
                "quality": "480p",
                "size": "64MB",
                "kind": "mp4",
                "premium": "false",
                "external": False,
                "url": "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            }
        ],
        "trailer": {
            "id": 4,
            "type": "video",
            "title": "Big Buck Bunny Trailer",
            "url": "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        },
        "actors": [actors_db[1]],
        "subtitles": [
            {
                "id": 1,
                "title": "English",
                "language": "en",
                "url": "https://example.com/subtitles/big-buck-bunny-en.vtt"
            },
            {
                "id": 2,
                "title": "Spanish",
                "language": "es",
                "url": "https://example.com/subtitles/big-buck-bunny-es.vtt"
            }
        ],
        "comments": [],
        "views": 15000,
        "downloads": 5000,
        "shares": 1200
    }

    # Sample Series
    movies_db[2] = {
        "id": 2,
        "title": "Sample TV Series",
        "type": "series",
        "label": "Drama",
        "sublabel": "Season 1",
        "imdb": "8.2",
        "downloadas": "sample-series-s01",
        "comment": True,
        "playas": "video",
        "description": "A gripping drama series with multiple seasons.",
        "classification": "TV-14",
        "year": "2023",
        "duration": "45:00",
        "rating": 8.2,
        "image": "https://example.com/sample-series.jpg",
        "cover": "https://example.com/sample-series-cover.jpg",
        "genres": [genres_db[3]],
        "sources": [],
        "trailer": None,
        "actors": [actors_db[2]],
        "subtitles": [],
        "comments": [],
        "views": 8000,
        "downloads": 2000,
        "shares": 500,
        "seasons": [
            {
                "id": 1,
                "title": "Season 1",
                "episodes": [
                    {
                        "id": 1,
                        "title": "Episode 1",
                        "episode_number": 1,
                        "image": "https://example.com/ep1.jpg",
                        "duration": "45:00",
                        "sources": [
                            {
                                "id": 5,
                                "type": "video",
                                "title": "Episode 1 1080p",
                                "quality": "1080p",
                                "url": "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
                            }
                        ]
                    }
                ]
            }
        ]
    }

    # Sample Channels
    channels_db[1] = {
        "id": 1,
        "title": "Sample News Channel",
        "label": "News",
        "sublabel": "24/7 News",
        "description": "24/7 news coverage from around the world",
        "website": "https://example-news.com",
        "classification": "News",
        "views": 25000,
        "shares": 800,
        "rating": 7.8,
        "comment": True,
        "image": "https://example.com/news-channel.jpg",
        "playas": "live",
        "sources": [
            {
                "id": 6,
                "type": "live",
                "title": "Live Stream",
                "quality": "1080p",
                "size": "Live",
                "kind": "hls",
                "premium": "false",
                "external": False,
                "url": "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            }
        ],
        "categories": [categories_db[1]],
        "countries": [countries_db[1]],
        "comments": []
    }

    channels_db[2] = {
        "id": 2,
        "title": "Sample Sports Channel",
        "label": "Sports",
        "sublabel": "Live Sports",
        "description": "Live sports coverage and highlights",
        "website": "https://example-sports.com",
        "classification": "Sports",
        "views": 18000,
        "shares": 600,
        "rating": 8.1,
        "comment": True,
        "image": "https://example.com/sports-channel.jpg",
        "playas": "live",
        "sources": [
            {
                "id": 7,
                "type": "live",
                "title": "Live Sports Stream",
                "quality": "720p",
                "size": "Live",
                "kind": "hls",
                "premium": "false",
                "external": False,
                "url": "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            }
        ],
        "categories": [categories_db[2]],
        "countries": [countries_db[1]],
        "comments": []
    }

# Authentication decorator
def token_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = request.headers.get('Authorization')
        if not token:
            return jsonify({'error': 'Token is missing'}), 401
        try:
            token = token.split(' ')[1]  # Remove 'Bearer ' prefix
            data = jwt.decode(token, app.config['SECRET_KEY'], algorithms=["HS256"])
            current_user = users_db.get(data['user_id'])
            if not current_user:
                return jsonify({'error': 'Invalid token'}), 401
        except:
            return jsonify({'error': 'Invalid token'}), 401
        return f(current_user, *args, **kwargs)
    return decorated

# Routes
@app.route('/api/v1/auth/login', methods=['POST'])
def login():
    data = request.get_json()
    username = data.get('username')
    password = data.get('password')
    
    # Simple authentication (in real app, check against database)
    if username == 'demo' and password == 'password':
        user_id = str(uuid.uuid4())
        users_db[user_id] = {
            'id': user_id,
            'name': 'Demo User',
            'username': username,
            'email': 'demo@example.com',
            'type': 'user',
            'image': 'https://example.com/avatar.jpg'
        }
        
        token = jwt.encode({
            'user_id': user_id,
            'exp': datetime.datetime.utcnow() + datetime.timedelta(hours=24)
        }, app.config['SECRET_KEY'])
        
        return jsonify({
            'success': True,
            'message': 'Login successful',
            'user': users_db[user_id],
            'token': token
        })
    
    return jsonify({'error': 'Invalid credentials'}), 401

@app.route('/api/v1/auth/register', methods=['POST'])
def register():
    data = request.get_json()
    user_id = str(uuid.uuid4())
    
    users_db[user_id] = {
        'id': user_id,
        'name': data.get('name'),
        'username': data.get('username'),
        'email': data.get('email'),
        'type': data.get('type', 'user'),
        'image': data.get('image', '')
    }
    
    return jsonify({
        'success': True,
        'message': 'Registration successful',
        'user': users_db[user_id]
    })

@app.route('/api/v1/home', methods=['GET'])
def get_home_data():
    slides = [
        {
            "id": 1,
            "title": "Big Buck Bunny",
            "type": "movie",
            "image": "https://peach.blender.org/wp-content/uploads/title_anouncement.jpg?x11217",
            "url": "/movies/1",
            "poster": movies_db[1]
        },
        {
            "id": 2,
            "title": "Sample News Channel",
            "type": "channel",
            "image": "https://example.com/news-channel.jpg",
            "url": "/channels/1",
            "channel": channels_db[1]
        }
    ]
    
    return jsonify({
        'slides': slides,
        'featured_movies': [movies_db[1]],
        'channels': [channels_db[1], channels_db[2]],
        'actors': list(actors_db.values()),
        'genres': list(genres_db.values())
    })

@app.route('/api/v1/movies/<int:movie_id>', methods=['GET'])
def get_movie(movie_id):
    movie = movies_db.get(movie_id)
    if not movie:
        return jsonify({'error': 'Movie not found'}), 404
    return jsonify(movie)

@app.route('/api/v1/movies/search', methods=['GET'])
def search_movies():
    query = request.args.get('q', '').lower()
    results = []
    
    for movie in movies_db.values():
        if query in movie['title'].lower() or query in movie['description'].lower():
            results.append({
                'id': movie['id'],
                'title': movie['title'],
                'image': movie['image'],
                'year': movie['year'],
                'rating': movie['rating']
            })
    
    return jsonify({'movies': results})

@app.route('/api/v1/movies/genre/<int:genre_id>', methods=['GET'])
def get_movies_by_genre(genre_id):
    page = int(request.args.get('page', 1))
    order = request.args.get('order', 'latest')
    
    results = []
    for movie in movies_db.values():
        if any(genre['id'] == genre_id for genre in movie['genres']):
            results.append({
                'id': movie['id'],
                'title': movie['title'],
                'image': movie['image'],
                'year': movie['year'],
                'rating': movie['rating']
            })
    
    # Simple pagination
    per_page = 20
    start = (page - 1) * per_page
    end = start + per_page
    
    return jsonify({
        'movies': results[start:end],
        'pagination': {
            'current_page': page,
            'total_pages': (len(results) + per_page - 1) // per_page,
            'total_items': len(results)
        }
    })

@app.route('/api/v1/movies/<int:movie_id>/view', methods=['POST'])
def add_movie_view(movie_id):
    movie = movies_db.get(movie_id)
    if not movie:
        return jsonify({'error': 'Movie not found'}), 404
    
    movie['views'] += 1
    return jsonify({'success': True, 'views': movie['views']})

@app.route('/api/v1/channels/<int:channel_id>', methods=['GET'])
def get_channel(channel_id):
    channel = channels_db.get(channel_id)
    if not channel:
        return jsonify({'error': 'Channel not found'}), 404
    return jsonify(channel)

@app.route('/api/v1/channels', methods=['GET'])
def get_channels():
    category_id = request.args.get('category')
    country_id = request.args.get('country')
    page = int(request.args.get('page', 1))
    
    results = []
    for channel in channels_db.values():
        if category_id and not any(cat['id'] == int(category_id) for cat in channel['categories']):
            continue
        if country_id and not any(country['id'] == int(country_id) for country in channel['countries']):
            continue
        
        results.append({
            'id': channel['id'],
            'title': channel['title'],
            'image': channel['image'],
            'views': channel['views'],
            'rating': channel['rating']
        })
    
    return jsonify({'channels': results})

@app.route('/api/v1/actors', methods=['GET'])
def get_actors():
    page = int(request.args.get('page', 1))
    search = request.args.get('search', '').lower()
    
    results = []
    for actor in actors_db.values():
        if search in actor['name'].lower():
            results.append(actor)
    
    return jsonify({'actors': results})

@app.route('/api/v1/actors/<int:actor_id>', methods=['GET'])
def get_actor(actor_id):
    actor = actors_db.get(actor_id)
    if not actor:
        return jsonify({'error': 'Actor not found'}), 404
    
    # Add movies for this actor
    actor_movies = []
    for movie in movies_db.values():
        if any(act['id'] == actor_id for act in movie['actors']):
            actor_movies.append({
                'id': movie['id'],
                'title': movie['title'],
                'image': movie['image'],
                'year': movie['year']
            })
    
    actor['movies'] = actor_movies
    return jsonify(actor)

@app.route('/api/v1/movies/<int:movie_id>/actors', methods=['GET'])
def get_roles_by_movie(movie_id):
    movie = movies_db.get(movie_id)
    if not movie:
        return jsonify({'error': 'Movie not found'}), 404
    
    return jsonify({'actors': movie['actors']})

@app.route('/api/v1/genres', methods=['GET'])
def get_genres():
    return jsonify({'genres': list(genres_db.values())})

@app.route('/api/v1/categories', methods=['GET'])
def get_categories():
    return jsonify({'categories': list(categories_db.values())})

@app.route('/api/v1/countries', methods=['GET'])
def get_countries():
    return jsonify({'countries': list(countries_db.values())})

@app.route('/api/v1/movies/<int:movie_id>/comments', methods=['GET'])
def get_movie_comments(movie_id):
    movie = movies_db.get(movie_id)
    if not movie:
        return jsonify({'error': 'Movie not found'}), 404
    
    return jsonify({'comments': movie['comments']})

@app.route('/api/v1/movies/<int:movie_id>/comments', methods=['POST'])
@token_required
def add_movie_comment(current_user, movie_id):
    data = request.get_json()
    movie = movies_db.get(movie_id)
    if not movie:
        return jsonify({'error': 'Movie not found'}), 404
    
    comment = {
        'id': len(movie['comments']) + 1,
        'user': current_user['name'],
        'comment': data['comment'],
        'created_at': datetime.datetime.now().isoformat()
    }
    
    movie['comments'].append(comment)
    return jsonify({'success': True, 'comment': comment})

@app.route('/api/v1/channels/<int:channel_id>/comments', methods=['GET'])
def get_channel_comments(channel_id):
    channel = channels_db.get(channel_id)
    if not channel:
        return jsonify({'error': 'Channel not found'}), 404
    
    return jsonify({'comments': channel['comments']})

@app.route('/api/v1/channels/<int:channel_id>/comments', methods=['POST'])
@token_required
def add_channel_comment(current_user, channel_id):
    data = request.get_json()
    channel = channels_db.get(channel_id)
    if not channel:
        return jsonify({'error': 'Channel not found'}), 404
    
    comment = {
        'id': len(channel['comments']) + 1,
        'user': current_user['name'],
        'comment': data['comment'],
        'created_at': datetime.datetime.now().isoformat()
    }
    
    channel['comments'].append(comment)
    return jsonify({'success': True, 'comment': comment})

@app.route('/api/v1/user/profile', methods=['GET'])
@token_required
def get_profile(current_user):
    return jsonify(current_user)

@app.route('/api/v1/user/my-list', methods=['GET'])
@token_required
def get_my_list(current_user):
    # Sample my list data
    return jsonify({
        'movies': [
            {
                'id': 1,
                'title': 'Big Buck Bunny',
                'image': 'https://peach.blender.org/wp-content/uploads/title_anouncement.jpg?x11217',
                'type': 'movie'
            }
        ],
        'series': [],
        'channels': []
    })

@app.route('/api/v1/subscription/plans', methods=['GET'])
def get_plans():
    return jsonify({
        'plans': [
            {
                'id': 1,
                'title': 'Basic Plan',
                'price': 9.99,
                'currency': 'USD',
                'duration': 'monthly',
                'features': ['HD Quality', 'No Ads', 'Basic Support']
            },
            {
                'id': 2,
                'title': 'Premium Plan',
                'price': 19.99,
                'currency': 'USD',
                'duration': 'monthly',
                'features': ['4K Quality', 'No Ads', 'Premium Support', 'Download']
            }
        ]
    })

@app.route('/api/v1/support', methods=['POST'])
def contact_support():
    data = request.get_json()
    return jsonify({
        'success': True,
        'message': 'Support request received. We will get back to you soon.'
    })

if __name__ == '__main__':
    init_sample_data()
    print("🎬 Movie API Server Started!")
    print("📺 Sample Data Loaded:")
    print("   - Big Buck Bunny (Animation)")
    print("   - Sample TV Series")
    print("   - News & Sports Channels")
    print("   - Sample Actors & Genres")
    print("\n🔗 API Endpoints:")
    print("   - POST /api/v1/auth/login (username: demo, password: password)")
    print("   - GET  /api/v1/home")
    print("   - GET  /api/v1/movies/1 (Big Buck Bunny)")
    print("   - GET  /api/v1/channels/1 (News Channel)")
    print("\n🎥 Video Sources:")
    print("   - Big Buck Bunny: http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
    print("   - Live Stream: https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8")
    print("\n🚀 Server running on http://localhost:5000")
    app.run(debug=True, host='0.0.0.0', port=5000)