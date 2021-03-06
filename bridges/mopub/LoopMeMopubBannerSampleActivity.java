package com.example.MopubBridgeDemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.loopme.LoopMeBannerView;
import com.mopub.mobileads.LoopMeMopubBanner;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import java.util.HashMap;
import java.util.Map;

public class LoopMeMopubBannerSampleActivity extends Activity implements MoPubView.BannerAdListener {

    private MoPubView mBanner;
    private static final String AD_UNIT_ID = "b4dc5fb8636645c08017780f6c0f0b71";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.banner_bridge_layout);

        mBanner = (MoPubView) findViewById(R.id.mopubView);
    }

    @Override
    protected void onPause() {
        LoopMeMopubBanner.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        LoopMeMopubBanner.resume();
        super.onResume();
    }

    public void onHideClicked(View view){
        LoopMeMopubBanner.destroy();
    }

    public void onLoadClicked(View view){
        mBanner.setAdUnitId(AD_UNIT_ID);
        mBanner.setBannerAdListener(this);
        mBanner.loadAd();

        LoopMeBannerView loopmeView = (LoopMeBannerView) findViewById(R.id.loopme_view);
        Map extras = new HashMap();
        extras.put("bannerView", loopmeView);
        mBanner.setLocalExtras(extras);
    }

    @Override
    protected void onDestroy() {
        mBanner.destroy();
        LoopMeMopubBanner.destroy();
        super.onDestroy();
    }

    private void toast(String mess) {
        Toast.makeText(getApplicationContext(), mess, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBannerLoaded(MoPubView moPubView) {
        toast("onBannerLoaded");
    }

    @Override
    public void onBannerFailed(MoPubView moPubView, MoPubErrorCode moPubErrorCode) {
        toast("onBannerFailed " + moPubErrorCode.toString());
    }

    @Override
    public void onBannerClicked(MoPubView moPubView) {
        toast("onBannerClicked");
    }

    @Override
    public void onBannerExpanded(MoPubView moPubView) {
        toast("onBannerExpanded");
    }

    @Override
    public void onBannerCollapsed(MoPubView moPubView) {
        toast("onBannerCollapsed");
    }
}
