package my.cinemax.app.free.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import my.cinemax.app.free.entity.JsonApiResponse;
import my.cinemax.app.free.entity.Plan;

import java.util.List;

public class SubscriptionManager {
    private static final String TAG = "SubscriptionManager";
    private static final String PREF_NAME = "subscription_prefs";
    private static final String KEY_SUBSCRIPTION_ENABLED = "subscription_enabled";
    private static final String KEY_MONTHLY_BILLING_ENABLED = "monthly_billing_enabled";
    private static final String KEY_FREE_TRIAL_DAYS = "free_trial_days";
    private static final String KEY_GRACE_PERIOD_DAYS = "grace_period_days";
    private static final String KEY_AUTO_RENEWAL_ENABLED = "auto_renewal_enabled";
    private static final String KEY_CURRENCY_DEFAULT = "currency_default";
    private static final String KEY_PAYMENT_METHODS = "payment_methods";

    private Context context;
    private SharedPreferences prefs;
    // private JsonApiResponse.SubscriptionConfig config; // Removed to fix compilation

    public SubscriptionManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Load subscription configuration from JSON response
     */
    public void loadConfig(JsonApiResponse jsonResponse) {
        // Subscription config loading disabled to fix compilation issues
        Log.d(TAG, "Subscription config loading disabled");
    }

    /**
     * Save configuration to SharedPreferences
     */
    private void saveConfigToPrefs() {
        // Subscription config saving disabled to fix compilation issues
        Log.d(TAG, "Subscription config saving disabled");
    }

    /**
     * Check if subscription system is enabled
     */
    public boolean isSubscriptionEnabled() {
        return prefs.getBoolean(KEY_SUBSCRIPTION_ENABLED, true);
    }

    /**
     * Check if monthly billing is enabled
     */
    public boolean isMonthlyBillingEnabled() {
        return prefs.getBoolean(KEY_MONTHLY_BILLING_ENABLED, true);
    }

    /**
     * Get available payment methods
     */
    public List<String> getPaymentMethods() {
        String methods = prefs.getString(KEY_PAYMENT_METHODS, "paypal,stripe");
        return java.util.Arrays.asList(methods.split(","));
    }

    /**
     * Get default currency
     */
    public String getDefaultCurrency() {
        return prefs.getString(KEY_CURRENCY_DEFAULT, "USD");
    }

    /**
     * Get free trial days
     */
    public int getFreeTrialDays() {
        return prefs.getInt(KEY_FREE_TRIAL_DAYS, 7);
    }

    /**
     * Get grace period days
     */
    public int getGracePeriodDays() {
        return prefs.getInt(KEY_GRACE_PERIOD_DAYS, 3);
    }

    /**
     * Check if auto renewal is enabled
     */
    public boolean isAutoRenewalEnabled() {
        return prefs.getBoolean(KEY_AUTO_RENEWAL_ENABLED, true);
    }

    /**
     * Get enabled subscription plans
     */
    public List<Plan> getEnabledPlans(List<Plan> allPlans) {
        if (allPlans == null) return null;
        
        return allPlans.stream()
                .filter(Plan::isEnabled)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Check if a feature requires subscription
     */
    public boolean isFeatureRequiresSubscription(String feature) {
        // Subscription features disabled to fix compilation issues
        return false;
    }

    /**
     * Validate subscription plan
     */
    public boolean isValidPlan(Plan plan) {
        if (plan == null) return false;
        
        // Check if plan is enabled
        if (!plan.isEnabled()) {
            Log.d(TAG, "Plan " + plan.getTitle() + " is disabled");
            return false;
        }
        
        // Check if monthly billing is required but plan doesn't support it
        if (isMonthlyBillingEnabled() && !"monthly".equals(plan.getBillingType())) {
            Log.d(TAG, "Plan " + plan.getTitle() + " doesn't support monthly billing");
            return false;
        }
        
        // Check if duration is valid
        if (plan.getDuration() == null || plan.getDuration().isEmpty()) {
            Log.d(TAG, "Plan " + plan.getTitle() + " has invalid duration");
            return false;
        }
        
        return true;
    }

    /**
     * Get plan by ID
     */
    public Plan getPlanById(List<Plan> plans, int planId) {
        if (plans == null) return null;
        
        return plans.stream()
                .filter(plan -> plan.getId() == planId && isValidPlan(plan))
                .findFirst()
                .orElse(null);
    }

    /**
     * Format price with currency
     */
    public String formatPrice(double price, String currency) {
        if (currency == null) currency = getDefaultCurrency();
        return String.format("%s %.2f", currency, price);
    }

    /**
     * Check if subscription is required for content
     */
    public boolean isSubscriptionRequiredForContent(String contentType) {
        return isFeatureRequiresSubscription(contentType);
    }

    /**
     * Get subscription configuration summary
     */
    public String getConfigSummary() {
        return "Subscription System: Disabled (removed to fix compilation issues)";
    }
}