package com.mobucks.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobucks.androidsdk.enumerations.Gender;
import com.mobucks.androidsdk.interfaces.AdListener;
import com.mobucks.androidsdk.views.BannerAdView;
import com.mobucks.androidsdk.views.InterstitialAdView;
import com.mobucks.androidsdk.views.VideoAdView;


public class BannerActivity extends AppCompatActivity {

    public static final String TAG = "BannerActivity";

    EditText uidEditText;
    EditText passwordEditText;
    EditText placementIdEditText;
    EditText widthEditText;
    EditText heightEditText;

    String uid;
    String password;
    String placementId;
    int width;
    int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Button loadBanner = findViewById(R.id.lbanner);
        Button loadVideo = findViewById(R.id.lvideo);
        Button loadInterstitial = findViewById(R.id.sinterstitial);

        uidEditText = findViewById(R.id.uid);
        passwordEditText = findViewById(R.id.password);
        placementIdEditText = findViewById(R.id.placement_id);
        widthEditText = findViewById(R.id.width);
        heightEditText = findViewById(R.id.height);

        uidEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                AppPreference.getInstance(BannerActivity.this).setStringSharedPreference("uid", s.toString());
            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                AppPreference.getInstance(BannerActivity.this).setStringSharedPreference("password", s.toString());
            }
        });
        placementIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                AppPreference.getInstance(BannerActivity.this).setStringSharedPreference("placementId", s.toString());
            }
        });
        widthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                AppPreference.getInstance(BannerActivity.this).setStringSharedPreference("width", s.toString());
            }
        });

        heightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                AppPreference.getInstance(BannerActivity.this).setStringSharedPreference("height", s.toString());
            }
        });
        readSharedPreference();

        uidEditText.setText(uid);
        passwordEditText.setText(password);
        placementIdEditText.setText(placementId);
        if (width > 0) {
            widthEditText.setText(String.valueOf(width));
        }
        if (height > 0) {
            heightEditText.setText(String.valueOf(height));
        }

        final BannerAdView bannerAdView = findViewById(R.id.bannerView);
        final VideoAdView videoAdView = findViewById(R.id.videoView);
        final InterstitialAdView interstitialAdView = new InterstitialAdView(this);

        bannerAdView.setAdListener(new AdListener<BannerAdView>() {

            @Override
            public void onAdloaded(BannerAdView adView) {
                Toast.makeText(BannerActivity.this, "bannerAdView - onAdloaded", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "bannerAdView - onAdloaded");
            }

            @Override
            public void onAdFailed(Exception e) {
                Toast.makeText(BannerActivity.this, "bannerAdView - onAdFailed", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "bannerAdView - onAdFailed");
                e.printStackTrace();
            }

            @Override
            public void onAdClicked(BannerAdView adView) {
                Log.d(TAG, "bannerAdView - onAdClicked");
            }
        });

        videoAdView.setTargetingAge(19);
        videoAdView.setTargetingGender(Gender.MALE);
        videoAdView.setTargetingLanguage("el");
        videoAdView.setAdListener(new AdListener<VideoAdView>() {
            @Override
            public void onAdloaded(VideoAdView videoAdView) {
                Log.d(TAG, "videoAdView - onAdloaded");
            }

            @Override
            public void onAdFailed(Exception e) {
                Log.d(TAG, "videoAdView - onAdFailed");
                e.printStackTrace();
            }

            @Override
            public void onAdClicked(VideoAdView videoAdView) {
                Log.d(TAG, "videoAdView - onAdClicked");
            }
        });

        interstitialAdView.setAdListener(new AdListener<InterstitialAdView>() {
            @Override
            public void onAdloaded(InterstitialAdView ad) {
                Log.d(TAG, "interstitialAdView - onAdloaded");
                interstitialAdView.show();
            }

            @Override
            public void onAdFailed(Exception e) {
                Log.d(TAG, "interstitialAdView - onAdFailed");
                e.printStackTrace();
            }

            @Override
            public void onAdClicked(InterstitialAdView ad) {
                Log.d(TAG, "interstitialAdView - onAdClicked");
            }
        });

        loadBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputsFilled()) {
                    if (isHeightAndWidthFilled()) {
                        bannerAdView.setAdSize(width, height);
                        bannerAdView.setUid(uid);
                        bannerAdView.setPassword(password);
                        bannerAdView.setPlacementId(placementId);

                        bannerAdView.setVisibility(View.VISIBLE);
                        videoAdView.setVisibility(View.GONE);
                        interstitialAdView.setVisibility(View.GONE);
                        bannerAdView.loadAd();
                    }
                }
            }
        });

        loadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputsFilled()) {

                    videoAdView.setUid(uid);
                    videoAdView.setPassword(password);
                    videoAdView.setPlacementId(placementId);

                    videoAdView.setVisibility(View.VISIBLE);
                    bannerAdView.setVisibility(View.GONE);
                    interstitialAdView.setVisibility(View.GONE);
                    videoAdView.loadAd();
                }
            }
        });

        loadInterstitial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputsFilled()) {

                    interstitialAdView.setUid(uid);
                    interstitialAdView.setPassword(password);
                    interstitialAdView.setPlacementId(placementId);

                    interstitialAdView.setVisibility(View.VISIBLE);
                    bannerAdView.setVisibility(View.GONE);
                    videoAdView.setVisibility(View.GONE);
                    interstitialAdView.loadAd();
                }
            }
        });

        setSupportActionBar(toolbar);
    }

    private void readInputs() {
        uid = uidEditText.getText().toString();
        password = passwordEditText.getText().toString();
        placementId = placementIdEditText.getText().toString();
    }

    private void readBannerHeightAndWidth() {
        height = Integer.parseInt(heightEditText.getText().toString());
        width = Integer.parseInt(widthEditText.getText().toString());
    }

    private boolean isInputsFilled() {
        if (!TextUtils.isEmpty(uidEditText.getText().toString())
                && !TextUtils.isEmpty(passwordEditText.getText().toString())
                && !TextUtils.isEmpty(placementIdEditText.getText().toString())) {

            readInputs();
            return true;
        } else {
            Toast.makeText(this, "Input field(s) cant be empty!", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean isHeightAndWidthFilled() {
        if (!TextUtils.isEmpty(widthEditText.getText().toString()) && !TextUtils.isEmpty(heightEditText.getText().toString())) {
            readBannerHeightAndWidth();
            if (width > 0 && height > 0) {
                return true;
            }
            Toast.makeText(this, "Height and Width cant be 0!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            Toast.makeText(this, "Height and Width cant be empty!", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void readSharedPreference() {
        uid = AppPreference.getInstance(BannerActivity.this).getStringSharedPreference("uid");
        password = AppPreference.getInstance(BannerActivity.this).getStringSharedPreference("password");
        placementId = AppPreference.getInstance(BannerActivity.this).getStringSharedPreference("placementId");
        String widthString = AppPreference.getInstance(BannerActivity.this).getStringSharedPreference("width");
        if (!TextUtils.isEmpty(widthString)) {
            width = Integer.parseInt(widthString);
        }
        String heightString = AppPreference.getInstance(BannerActivity.this).getStringSharedPreference("height");
        if (!TextUtils.isEmpty(heightString)) {
            height = Integer.parseInt(heightString);
        }
    }
}
