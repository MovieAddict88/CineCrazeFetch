package my.cinemax.app.free.api;

import my.cinemax.app.free.entity.ApiResponse;
import my.cinemax.app.free.entity.ApiValue;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class MockApiRest {
    
    public static void handleVersionCheck(Integer code, Integer user, Callback<ApiResponse> callback) {
        // Create a mock successful response for version check
        ApiResponse response = new ApiResponse();
        response.setCode(200);
        response.setMessage("success");
        
        // Add mock configuration values
        List<ApiValue> values = new ArrayList<>();
        ApiValue admobId = new ApiValue();
        admobId.setName("ADMIN_REWARDED_ADMOB_ID");
        admobId.setValue("ca-app-pub-3940256099942544/5224354917"); // Test AdMob ID
        values.add(admobId);
        
        ApiValue bannerAdmobId = new ApiValue();
        bannerAdmobId.setName("ADMIN_BANNER_ADMOB_ID");
        bannerAdmobId.setValue("ca-app-pub-3940256099942544/6300978111"); // Test AdMob ID
        values.add(bannerAdmobId);
        
        ApiValue interstitialAdmobId = new ApiValue();
        interstitialAdmobId.setName("ADMIN_INTERSTITIAL_ADMOB_ID");
        interstitialAdmobId.setValue("ca-app-pub-3940256099942544/1033173712"); // Test AdMob ID
        values.add(interstitialAdmobId);
        
        ApiValue nativeAdmobId = new ApiValue();
        nativeAdmobId.setName("ADMIN_NATIVE_ADMOB_ID");
        nativeAdmobId.setValue("ca-app-pub-3940256099942544/2247696110"); // Test AdMob ID
        values.add(nativeAdmobId);
        
        response.setValues(values);
        
        // Simulate successful response
        Response<ApiResponse> retrofitResponse = Response.success(response);
        
        // Create a dummy call for the callback
        Call<ApiResponse> dummyCall = createDummyCall();
        
        callback.onResponse(dummyCall, retrofitResponse);
    }
    
    @SuppressWarnings("unchecked")
    private static <T> Call<T> createDummyCall() {
        return (Call<T>) new Call<Object>() {
            @Override public Response<Object> execute() { return null; }
            @Override public void enqueue(Callback<Object> callback) { }
            @Override public boolean isExecuted() { return false; }
            @Override public void cancel() { }
            @Override public boolean isCanceled() { return false; }
            @Override public Call<Object> clone() { return this; }
            @Override public okhttp3.Request request() { return null; }
        };
    }
}