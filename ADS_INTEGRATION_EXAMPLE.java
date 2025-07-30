// ===== ADS INTEGRATION WITH JSON API =====
// This shows how to integrate AdMob and Facebook ads with your JSON API

package my.cinemax.app.free.ui.activities;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.entity.JsonApiResponse;
import my.cinemax.app.free.Provider.PrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Example of how to integrate ads with the JSON API
 * This shows how to load ad configurations from your GitHub JSON
 */
public class ADS_INTEGRATION_EXAMPLE {

    // ===== STEP 1: LOAD ADS CONFIGURATION =====
    public void loadAdsConfiguration(Activity activity) {
        apiClient.loadAdsConfigAndUpdatePrefs(activity, new apiClient.AdsConfigCallback() {
            @Override
            public void onSuccess(String message) {
                Log.d("ADS_API", "Ads configuration loaded successfully");
                
                // Now your app will use the ad IDs from the JSON
                // The PrefManager has been updated with the new ad IDs
                
                // You can now show ads using your existing ad implementation
                showAdsExample(activity);
            }
            
            @Override
            public void onError(String error) {
                Log.e("ADS_API", "Failed to load ads config: " + error);
            }
        });
    }
    
    // ===== STEP 2: SHOW ADS USING JSON CONFIGURATION =====
    public void showAdsExample(Activity activity) {
        PrefManager prefManager = new PrefManager(activity.getApplicationContext());
        
        // Check if ads are enabled from JSON
        String bannerType = prefManager.getString("ADMIN_BANNER_TYPE");
        String interstitialType = prefManager.getString("ADMIN_INTERSTITIAL_TYPE");
        String nativeType = prefManager.getString("ADMIN_NATIVE_TYPE");
        
        Log.d("ADS_API", "Banner Type: " + bannerType);
        Log.d("ADS_API", "Interstitial Type: " + interstitialType);
        Log.d("ADS_API", "Native Type: " + nativeType);
        
        // Show banner ad if enabled
        if (!bannerType.equals("FALSE")) {
            showBannerAd(activity, bannerType);
        }
        
        // Show interstitial ad if enabled
        if (!interstitialType.equals("FALSE")) {
            showInterstitialAd(activity, interstitialType);
        }
        
        // Show native ad if enabled
        if (!nativeType.equals("FALSE")) {
            showNativeAd(activity, nativeType);
        }
    }
    
    // ===== STEP 3: BANNER ADS =====
    private void showBannerAd(Activity activity, String adType) {
        PrefManager prefManager = new PrefManager(activity.getApplicationContext());
        
        if (adType.equals("ADMOB")) {
            String bannerId = prefManager.getString("ADMIN_BANNER_ADMOB_ID");
            Log.d("ADS_API", "Showing AdMob Banner with ID: " + bannerId);
            
            // Your existing AdMob banner implementation
            // This will use the ID from the JSON configuration
            
        } else if (adType.equals("FACEBOOK")) {
            String bannerId = prefManager.getString("ADMIN_BANNER_FACEBOOK_ID");
            Log.d("ADS_API", "Showing Facebook Banner with ID: " + bannerId);
            
            // Your existing Facebook banner implementation
            // This will use the ID from the JSON configuration
        }
    }
    
    // ===== STEP 4: INTERSTITIAL ADS =====
    private void showInterstitialAd(Activity activity, String adType) {
        PrefManager prefManager = new PrefManager(activity.getApplicationContext());
        
        if (adType.equals("ADMOB")) {
            String interstitialId = prefManager.getString("ADMIN_INTERSTITIAL_ADMOB_ID");
            int clicks = prefManager.getInt("ADMIN_INTERSTITIAL_CLICKS");
            Log.d("ADS_API", "Showing AdMob Interstitial with ID: " + interstitialId + " every " + clicks + " clicks");
            
            // Your existing AdMob interstitial implementation
            // This will use the ID and settings from the JSON configuration
            
        } else if (adType.equals("FACEBOOK")) {
            String interstitialId = prefManager.getString("ADMIN_INTERSTITIAL_FACEBOOK_ID");
            Log.d("ADS_API", "Showing Facebook Interstitial with ID: " + interstitialId);
            
            // Your existing Facebook interstitial implementation
            // This will use the ID from the JSON configuration
        }
    }
    
    // ===== STEP 5: NATIVE ADS =====
    private void showNativeAd(Activity activity, String adType) {
        PrefManager prefManager = new PrefManager(activity.getApplicationContext());
        
        if (adType.equals("ADMOB")) {
            String nativeId = prefManager.getString("ADMIN_NATIVE_ADMOB_ID");
            String lines = prefManager.getString("ADMIN_NATIVE_LINES");
            Log.d("ADS_API", "Showing AdMob Native with ID: " + nativeId + " every " + lines + " lines");
            
            // Your existing AdMob native implementation
            // This will use the ID and settings from the JSON configuration
            
        } else if (adType.equals("FACEBOOK")) {
            String nativeId = prefManager.getString("ADMIN_NATIVE_FACEBOOK_ID");
            Log.d("ADS_API", "Showing Facebook Native with ID: " + nativeId);
            
            // Your existing Facebook native implementation
            // This will use the ID from the JSON configuration
        }
    }
    
    // ===== STEP 6: REWARDED ADS =====
    public void showRewardedAd(Activity activity, String adType) {
        PrefManager prefManager = new PrefManager(activity.getApplicationContext());
        
        if (adType.equals("ADMOB")) {
            String rewardedId = prefManager.getString("ADMIN_REWARDED_ADMOB_ID");
            Log.d("ADS_API", "Showing AdMob Rewarded with ID: " + rewardedId);
            
            // Your existing AdMob rewarded implementation
            // This will use the ID from the JSON configuration
            
        } else if (adType.equals("FACEBOOK")) {
            String rewardedId = prefManager.getString("ADMIN_REWARDED_FACEBOOK_ID");
            Log.d("ADS_API", "Showing Facebook Rewarded with ID: " + rewardedId);
            
            // Your existing Facebook rewarded implementation
            // This will use the ID from the JSON configuration
        }
    }
    
    // ===== STEP 7: GET ADS CONFIG FROM JSON =====
    public void getAdsConfigFromJson(Callback<JsonApiResponse> callback) {
        apiClient.getAdsConfigFromJson(callback);
    }
    
    // ===== STEP 8: EXAMPLE USAGE IN ACTIVITY =====
    public void exampleUsageInActivity(Activity activity) {
        // 1. Load ads configuration when app starts
        loadAdsConfiguration(activity);
        
        // 2. Show ads based on JSON configuration
        showAdsExample(activity);
        
        // 3. Show rewarded ad when user wants to skip ads
        showRewardedAd(activity, "ADMOB"); // or "FACEBOOK"
    }
}

// ===== JSON ADS CONFIGURATION STRUCTURE =====

/**
 * Your free_movie_api.json now includes this ads configuration:
 * 
 * {
 *   "ads_config": {
 *     "admob": {
 *       "banner_id": "ca-app-pub-3940256099942544/6300978111",
 *       "interstitial_id": "ca-app-pub-3940256099942544/1033173712",
 *       "rewarded_id": "ca-app-pub-3940256099942544/5224354917",
 *       "native_id": "ca-app-pub-3940256099942544/2247696110",
 *       "app_id": "ca-app-pub-3940256099942544~3347511713"
 *     },
 *     "facebook": {
 *       "banner_id": "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID",
 *       "interstitial_id": "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID",
 *       "rewarded_id": "VID_HD_16_9_15S_APP_INSTALL#YOUR_PLACEMENT_ID",
 *       "native_id": "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID"
 *     },
 *     "settings": {
 *       "banner_enabled": true,
 *       "interstitial_enabled": true,
 *       "rewarded_enabled": true,
 *       "native_enabled": true,
 *       "interstitial_clicks": 3,
 *       "native_lines": 6,
 *       "banner_type": "ADMOB",
 *       "interstitial_type": "ADMOB",
 *       "native_type": "ADMOB"
 *     }
 *   }
 * }
 */

// ===== INTEGRATION WITH YOUR EXISTING CODE =====

/**
 * To integrate with your existing HomeActivity:
 * 
 * 1. Add this to your HomeActivity:
 *    private ADS_INTEGRATION_EXAMPLE adsExample = new ADS_INTEGRATION_EXAMPLE();
 * 
 * 2. Call in onCreate:
 *    adsExample.loadAdsConfiguration(this);
 * 
 * 3. Your existing ad implementations will automatically use the new IDs
 * 
 * 4. Update your JSON file to change ad IDs remotely
 * 
 * 5. Build and test your app!
 */

// ===== BENEFITS =====

/**
 * Benefits of JSON API Ads Integration:
 * 
 * 1. **Remote Configuration** - Change ad IDs without app update
 * 2. **A/B Testing** - Test different ad networks easily
 * 3. **Dynamic Control** - Enable/disable ads remotely
 * 4. **Multiple Networks** - Support both AdMob and Facebook
 * 5. **Easy Management** - Update ads from GitHub
 * 6. **No Server Required** - Hosted on GitHub Pages
 * 7. **Free** - No hosting costs
 * 8. **Backward Compatible** - Works with existing ad code
 */