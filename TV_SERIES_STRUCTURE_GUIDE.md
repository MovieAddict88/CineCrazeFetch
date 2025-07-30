# 📺 TV Series Structure Guide

## 🎯 **Updated TV Series Structure**

Your `free_movie_api.json` now includes a complete TV series with **2 seasons** and **1 episode each** as a guide for your data structure.

## 📋 **Series Overview:**

```json
{
  "id": 2,
  "title": "Sample TV Series",
  "type": "series",
  "label": "Drama",
  "sublabel": "2 Seasons",
  "imdb": "8.2",
  "downloadas": "sample-series",
  "description": "A gripping drama series with 2 seasons. Each season contains 1 episode with multiple quality options and subtitles.",
  "classification": "TV-14",
  "year": "2023",
  "duration": "45:00",
  "rating": 8.2,
  "seasons": [
    {
      "id": 1,
      "title": "Season 1",
      "episodes": [...]
    },
    {
      "id": 2,
      "title": "Season 2", 
      "episodes": [...]
    }
  ]
}
```

## 🎬 **Season 1 Structure:**

```json
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
        },
        {
          "id": 6,
          "type": "video",
          "title": "Episode 1 720p",
          "quality": "720p",
          "url": "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
        },
        {
          "id": 7,
          "type": "video",
          "title": "Episode 1 480p",
          "quality": "480p",
          "url": "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
        }
      ],
      "subtitles": [
        {
          "id": 1,
          "title": "English",
          "language": "en",
          "url": "https://example.com/subtitles/ep1-en.vtt"
        },
        {
          "id": 2,
          "title": "Spanish",
          "language": "es",
          "url": "https://example.com/subtitles/ep1-es.vtt"
        }
      ],
      "views": 5000,
      "downloads": 1200
    }
  ]
}
```

## 🎬 **Season 2 Structure:**

```json
{
  "id": 2,
  "title": "Season 2",
  "episodes": [
    {
      "id": 2,
      "title": "Episode 1",
      "episode_number": 1,
      "image": "https://example.com/ep2.jpg",
      "duration": "45:00",
      "sources": [
        {
          "id": 8,
          "type": "video",
          "title": "Episode 1 1080p",
          "quality": "1080p",
          "url": "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        },
        {
          "id": 9,
          "type": "video",
          "title": "Episode 1 720p",
          "quality": "720p",
          "url": "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        },
        {
          "id": 10,
          "type": "video",
          "title": "Episode 1 480p",
          "quality": "480p",
          "url": "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        }
      ],
      "subtitles": [
        {
          "id": 3,
          "title": "English",
          "language": "en",
          "url": "https://example.com/subtitles/ep2-en.vtt"
        },
        {
          "id": 4,
          "title": "Spanish",
          "language": "es",
          "url": "https://example.com/subtitles/ep2-es.vtt"
        }
      ],
      "views": 3000,
      "downloads": 800
    }
  ]
}
```

## 📊 **Key Features:**

### **✅ Multiple Quality Options:**
- **1080p** - High quality
- **720p** - Medium quality  
- **480p** - Low quality

### **✅ Multiple Subtitles:**
- **English** - Primary language
- **Spanish** - Secondary language

### **✅ Analytics:**
- **Views** - Track episode popularity
- **Downloads** - Track offline usage

### **✅ Real Video URLs:**
- **Season 1**: Uses Elephants Dream video
- **Season 2**: Uses Big Buck Bunny video

## 🔧 **How to Use This Structure:**

### **1. For Your Own Series:**
```json
{
  "id": 3,
  "title": "Your TV Series",
  "type": "series",
  "seasons": [
    {
      "id": 1,
      "title": "Season 1",
      "episodes": [
        {
          "id": 1,
          "title": "Episode 1",
          "sources": [
            {
              "id": 11,
              "quality": "1080p",
              "url": "YOUR_VIDEO_URL_HERE"
            }
          ]
        }
      ]
    }
  ]
}
```

### **2. Add More Episodes:**
```json
"episodes": [
  {
    "id": 1,
    "title": "Episode 1",
    "episode_number": 1
  },
  {
    "id": 2,
    "title": "Episode 2", 
    "episode_number": 2
  },
  {
    "id": 3,
    "title": "Episode 3",
    "episode_number": 3
  }
]
```

### **3. Add More Seasons:**
```json
"seasons": [
  {
    "id": 1,
    "title": "Season 1"
  },
  {
    "id": 2,
    "title": "Season 2"
  },
  {
    "id": 3,
    "title": "Season 3"
  }
]
```

## 🎯 **Benefits of This Structure:**

1. **Scalable** - Easy to add more seasons and episodes
2. **Flexible** - Multiple quality options and subtitles
3. **Analytics** - Track views and downloads
4. **Real Videos** - Working video URLs for testing
5. **Complete** - All necessary metadata included
6. **Consistent** - Follows your existing data structure

## 🚀 **Next Steps:**

1. **Upload** the updated `free_movie_api.json` to your GitHub
2. **Test** the series structure in your app
3. **Use** this as a template for your own series
4. **Add** more seasons and episodes as needed

Your TV series now has a complete structure with 2 seasons and 1 episode each! 📺🎬