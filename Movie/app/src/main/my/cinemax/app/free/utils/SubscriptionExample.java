package my.cinemax.app.free.utils;

import android.content.Context;
import android.util.Log;

import my.cinemax.app.free.entity.Plan;

import java.util.List;

/**
 * Example class showing how to use the subscription system
 * This demonstrates how to check subscription requirements and handle billing
 */
public class SubscriptionExample {
    private static final String TAG = "SubscriptionExample";
    private SubscriptionManager subscriptionManager;
    private Context context;

    public SubscriptionExample(Context context) {
        this.context = context;
        this.subscriptionManager = new SubscriptionManager(context);
    }

    /**
     * Example: Check if user can access premium content
     */
    public boolean canAccessPremiumContent(String contentType) {
        // Check if subscription is required for this content type
        if (subscriptionManager.isFeatureRequiresSubscription(contentType)) {
            // Here you would check if user has active subscription
            // For now, we'll just return false to show the subscription dialog
            Log.d(TAG, "Subscription required for: " + contentType);
            return false;
        }
        return true;
    }

    /**
     * Example: Show subscription dialog for premium content
     */
    public void showSubscriptionDialog(String contentType) {
        if (subscriptionManager.isSubscriptionEnabled()) {
            Log.d(TAG, "Showing subscription dialog for: " + contentType);
            
            // Here you would show your subscription dialog
            // You can get the enabled plans and show them to the user
            // For example:
            // showSubscriptionPlansDialog();
        } else {
            Log.d(TAG, "Subscription system is disabled");
        }
    }

    /**
     * Example: Validate a subscription plan before purchase
     */
    public boolean validatePlanForPurchase(Plan plan) {
        if (!subscriptionManager.isValidPlan(plan)) {
            Log.d(TAG, "Plan validation failed for: " + plan.getTitle());
            return false;
        }

        // Check if monthly billing is required
        if (subscriptionManager.isMonthlyBillingEnabled() && 
            !"monthly".equals(plan.getBillingType())) {
            Log.d(TAG, "Plan doesn't support monthly billing: " + plan.getTitle());
            return false;
        }

        Log.d(TAG, "Plan validated successfully: " + plan.getTitle());
        return true;
    }

    /**
     * Example: Format plan price for display
     */
    public String getFormattedPlanPrice(Plan plan) {
        return subscriptionManager.formatPrice(plan.getPrice(), plan.getCurrency());
    }

    /**
     * Example: Check if content requires subscription
     */
    public void checkContentAccess(String contentId, String contentType) {
        Log.d(TAG, "Checking access for content: " + contentId + " of type: " + contentType);
        
        if (canAccessPremiumContent(contentType)) {
            Log.d(TAG, "User can access content: " + contentId);
            // Proceed with content access
            // playMovie(contentId);
        } else {
            Log.d(TAG, "Subscription required for content: " + contentId);
            showSubscriptionDialog(contentType);
        }
    }

    /**
     * Example: Handle subscription purchase
     */
    public void handleSubscriptionPurchase(Plan selectedPlan) {
        Log.d(TAG, "Handling subscription purchase for plan: " + selectedPlan.getTitle());
        
        // Validate the plan
        if (!validatePlanForPurchase(selectedPlan)) {
            Log.e(TAG, "Plan validation failed, cannot proceed with purchase");
            return;
        }

        // Check if monthly billing is enabled
        if (subscriptionManager.isMonthlyBillingEnabled()) {
            Log.d(TAG, "Monthly billing is enabled");
            
            // Check free trial
            int freeTrialDays = subscriptionManager.getFreeTrialDays();
            if (freeTrialDays > 0) {
                Log.d(TAG, "Free trial available for " + freeTrialDays + " days");
            }
            
            // Check grace period
            int gracePeriodDays = subscriptionManager.getGracePeriodDays();
            Log.d(TAG, "Grace period: " + gracePeriodDays + " days");
        }

        // Get available payment methods
        List<String> paymentMethods = subscriptionManager.getPaymentMethods();
        Log.d(TAG, "Available payment methods: " + String.join(", ", paymentMethods));

        // Here you would proceed with the actual purchase
        // For example:
        // initiatePayment(selectedPlan, paymentMethods);
    }

    /**
     * Example: Check subscription status for different content types
     */
    public void checkSubscriptionRequirements() {
        String[] contentTypes = {
            "premium_movies",
            "4k_quality", 
            "download",
            "no_ads",
            "exclusive_content"
        };

        for (String contentType : contentTypes) {
            boolean requiresSubscription = subscriptionManager.isFeatureRequiresSubscription(contentType);
            Log.d(TAG, "Content type '" + contentType + "' requires subscription: " + requiresSubscription);
        }
    }

    /**
     * Example: Get subscription configuration summary
     */
    public void logSubscriptionConfig() {
        String configSummary = subscriptionManager.getConfigSummary();
        Log.d(TAG, "Subscription Configuration:\n" + configSummary);
    }

    /**
     * Example: Check if monthly billing is enabled
     */
    public boolean isMonthlyBillingEnabled() {
        boolean enabled = subscriptionManager.isMonthlyBillingEnabled();
        Log.d(TAG, "Monthly billing enabled: " + enabled);
        return enabled;
    }

    /**
     * Example: Get default currency
     */
    public String getDefaultCurrency() {
        String currency = subscriptionManager.getDefaultCurrency();
        Log.d(TAG, "Default currency: " + currency);
        return currency;
    }
}