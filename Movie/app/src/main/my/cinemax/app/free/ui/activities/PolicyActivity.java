package my.cinemax.app.free.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import my.cinemax.app.free.R;
import my.cinemax.app.free.config.Global;
import my.cinemax.app.free.config.PolicyUrls;
import my.cinemax.app.free.entity.Actress;


public class PolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the type from intent extras (privacy or policy)
        String policyType = getIntent().getStringExtra("policy_type");
        String url;
        String title;
        
        if ("policy".equals(policyType)) {
            url = PolicyUrls.TERMS_OF_SERVICE_URL;
            title = getResources().getString(R.string.terms_of_service);
        } else {
            // Default to privacy policy
            url = PolicyUrls.PRIVACY_POLICY_URL;
            title = getResources().getString(R.string.policy_privacy);
        }
        
        toolbar.setTitle(title);

        WebView webView=findViewById(R.id.web_view);
        webView.loadUrl(url);

    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        return;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
