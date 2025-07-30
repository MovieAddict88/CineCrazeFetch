# JSON API Debug Guide

## Issue: "Failed to load data from JSON API"

### Possible Causes:

1. **Network Connectivity**: The app can't reach the GitHub Pages URL
2. **JSON File Not Found**: The JSON file doesn't exist at the specified URL
3. **CORS Issues**: Cross-origin resource sharing problems
4. **JSON Format Issues**: The JSON file has syntax errors
5. **Base URL Configuration**: Incorrect base URL in the API client

### Debug Steps:

#### 1. **Test JSON File Accessibility**
Open this URL in your browser:
```
https://MovieAddict88.github.io/movie-api/free_movie_api.json
```

**Expected Result**: You should see the JSON data
**If Error**: The JSON file is not accessible

#### 2. **Check Logs**
Look for these log messages in Android Studio:
- `"Starting to load home data from JSON API..."`
- `"Response received. Success: true/false, Code: 200/404/etc"`
- `"Error message: ..."`

#### 3. **Common Error Codes**:
- **404**: File not found
- **403**: Access forbidden
- **500**: Server error
- **Network Error**: No internet connection

#### 4. **Quick Fixes to Try**:

**A. Check Internet Permission**
Make sure your `AndroidManifest.xml` has:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

**B. Test with a Different URL**
Try using a different test JSON:
```java
// In apiClient.java, change the base URL to:
.baseUrl("https://jsonplaceholder.typicode.com/")
```

**C. Add Network Security Config**
If you're testing on Android 9+, add this to `res/xml/network_security_config.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">MovieAddict88.github.io</domain>
    </domain-config>
</network-security-config>
```

And reference it in `AndroidManifest.xml`:
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
```

### Expected Log Output (Success):
```
JSON_API: Starting to load home data from JSON API...
JSON_API: Response received. Success: true, Code: 200
JSON_API: Successfully loaded home data from JSON API
JSON_API: Movies: 5
JSON_API: Channels: 3
JSON_API: Actors: 2
JSON_API: Home data: available
```

### Expected Log Output (Failure):
```
JSON_API: Starting to load home data from JSON API...
JSON_API: Response received. Success: false, Code: 404
JSON_API: Failed to load home data: 404
JSON_API: Error body: Not Found
```

### Next Steps:
1. Check if the JSON file is accessible in browser
2. Look at the logs in Android Studio
3. Try the quick fixes above
4. Report the specific error code and message