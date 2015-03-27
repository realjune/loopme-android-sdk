package com.loopme;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.loopme.utilites.Drawables;

/**
 * Layout for AdBrowserActivity
 */
final class AdBrowserLayout extends RelativeLayout {
	
	private static final int HEADER_HEIGHT_DP = 30;

	private final RelativeLayout mFooterView;
    private final ProgressBar mProgressBar;
    private final Button mBackBtn;
    private final Button mRefreshBtn;
    private final Button mCloseBtn;
    private final Button mNativeBtn;
    private final BrowserWebView mAdBrowserWebview;
    
	public AdBrowserLayout(Context context) {
		super(context);
    	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, 
				ViewGroup.LayoutParams.MATCH_PARENT);
    	setLayoutParams(params);
    	
    	mAdBrowserWebview = new BrowserWebView(context);
    	mAdBrowserWebview.setLayoutParams(params);
    	addView(mAdBrowserWebview);
    	
    	mFooterView = new RelativeLayout(context);
    	configFooterView();

    	LinearLayout buttonsContainer = new LinearLayout(context);
    	configButtonsContainer(buttonsContainer);
    	
    	int buttonWidth = Utils.getDisplayMetrics(context).widthPixels / 5;
    	RelativeLayout.LayoutParams buttons_params = new RelativeLayout.LayoutParams(
				buttonWidth, ViewGroup.LayoutParams.MATCH_PARENT);

    	int size = Utils.convertDpToPixel(HEADER_HEIGHT_DP) / 2;
    	RelativeLayout.LayoutParams pb_params = new RelativeLayout.LayoutParams(size, size);
		pb_params.addRule(RelativeLayout.CENTER_IN_PARENT);

		mProgressBar = new ProgressBar(context);
    	configProgressButton(context, buttonsContainer, buttons_params, pb_params);
		
		mBackBtn = new Button(context);
		configBackButton(context, buttonsContainer, buttons_params, pb_params);
		
		mRefreshBtn = new Button(context);
		configRefreshButton(context, buttonsContainer, buttons_params, pb_params);
		
		mNativeBtn = new Button(context);
		configNativeButton(context, buttonsContainer, buttons_params, pb_params);
		
		mCloseBtn = new Button(context);
		configCloseButton(context, buttonsContainer, buttons_params, pb_params);

		mFooterView.addView(initBottomWhiteLineView(context));
	}
	
	private void configProgressButton(Context context, 
			LinearLayout buttonsContainer,
			RelativeLayout.LayoutParams buttons_params,
			RelativeLayout.LayoutParams pb_params) {
		RelativeLayout progressLayout = new RelativeLayout(context);
    	progressLayout.setLayoutParams(buttons_params);
		mProgressBar.setLayoutParams(pb_params);
		progressLayout.addView(mProgressBar);
		buttonsContainer.addView(progressLayout);
	}
	
	private void configButtonsContainer(LinearLayout buttonsContainer) {
		LinearLayout.LayoutParams buttonsContainerParams = new LinearLayout.LayoutParams(
    			ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    	buttonsContainer.setLayoutParams(buttonsContainerParams);
    	buttonsContainer.setBackgroundColor(Color.BLACK);
    	mFooterView.addView(buttonsContainer);
	}
	
	private void configFooterView() {
		RelativeLayout.LayoutParams footer_params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				Utils.convertDpToPixel(HEADER_HEIGHT_DP));
    	footer_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    	mFooterView.setLayoutParams(footer_params);
    	addView(mFooterView);
	}
	
	private View initBottomWhiteLineView(Context context) {
		View whiteLine = new View(context);
		RelativeLayout.LayoutParams whiteLineParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, 1);
		whiteLineParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		whiteLine.setLayoutParams(whiteLineParams);
		whiteLine.setBackgroundColor(Color.WHITE);
		return whiteLine;
	}
	
	private void configBackButton(Context context, LinearLayout buttonsContainer,
			RelativeLayout.LayoutParams buttons_params,
			RelativeLayout.LayoutParams pb_params) {
		RelativeLayout backLayout = new RelativeLayout(context);
    	backLayout.setLayoutParams(buttons_params);
    	if (Build.VERSION.SDK_INT < 16) {
    		mBackBtn.setBackgroundDrawable(Drawables.BTN_BACK_INACTIVE.decodeImage());
    	} else {
    		mBackBtn.setBackground(Drawables.BTN_BACK_INACTIVE.decodeImage());
    	}
    	mBackBtn.setLayoutParams(pb_params);
		backLayout.addView(mBackBtn);
		buttonsContainer.addView(backLayout);
	}
	
	private void configRefreshButton(Context context, LinearLayout buttonsContainer,
			RelativeLayout.LayoutParams buttons_params,
			RelativeLayout.LayoutParams pb_params) {
		RelativeLayout refreshLayout = new RelativeLayout(context);
		refreshLayout.setLayoutParams(buttons_params);
    	if (Build.VERSION.SDK_INT < 16) {
    		mRefreshBtn.setBackgroundDrawable(Drawables.BTN_REFRESH.decodeImage());
    	} else {
    		mRefreshBtn.setBackground(Drawables.BTN_REFRESH.decodeImage());
    	}
		mRefreshBtn.setLayoutParams(pb_params);
		refreshLayout.addView(mRefreshBtn);
		buttonsContainer.addView(refreshLayout);
	}
	
	private void configNativeButton(Context context, LinearLayout buttonsContainer,
			RelativeLayout.LayoutParams buttons_params,
			RelativeLayout.LayoutParams pb_params) {
		RelativeLayout nativeLayout = new RelativeLayout(context);
    	nativeLayout.setLayoutParams(buttons_params);
    	if (Build.VERSION.SDK_INT < 16) {
    		mNativeBtn.setBackgroundDrawable(Drawables.BTN_NATIVE_BROWSER.decodeImage());
    	} else {
    		mNativeBtn.setBackground(Drawables.BTN_NATIVE_BROWSER.decodeImage());
    	}
		mNativeBtn.setLayoutParams(pb_params);
		nativeLayout.addView(mNativeBtn);
		buttonsContainer.addView(nativeLayout);
	}
	
	private void configCloseButton(Context context, LinearLayout buttonsContainer,
			RelativeLayout.LayoutParams buttons_params,
			RelativeLayout.LayoutParams pb_params) {
		RelativeLayout closeLayout = new RelativeLayout(context);
		closeLayout.setLayoutParams(buttons_params);
    	if (Build.VERSION.SDK_INT < 16) {
    		mCloseBtn.setBackgroundDrawable(Drawables.BTN_CLOSE.decodeImage());
    	} else {
    		mCloseBtn.setBackground(Drawables.BTN_CLOSE.decodeImage());
    	}
		mCloseBtn.setLayoutParams(pb_params);
		closeLayout.addView(mCloseBtn);
		buttonsContainer.addView(closeLayout);
	}
	
	ProgressBar getProgressBar() {
		return mProgressBar;
	}
	
	Button getBackButton() {
		return mBackBtn;
	}
	
	Button getRefreshButton() {
		return mRefreshBtn;
	}
	
	Button getCloseButton() {
		return mCloseBtn;
	}
	
	Button getNativeButton() {
		return mNativeBtn;
	}
	
	BrowserWebView getWebView() {
		return mAdBrowserWebview;
	}
}