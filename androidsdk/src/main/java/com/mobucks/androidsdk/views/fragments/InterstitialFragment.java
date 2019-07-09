package com.mobucks.androidsdk.views.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.mobucks.androidsdk.R;
import com.mobucks.androidsdk.tools.Tools;
import com.mobucks.androidsdk.views.InterstitialAdView;

/**
 *  Fragment used for interstitial layout
 */
public class InterstitialFragment extends Fragment {
    private InterstitialAdView rootView;

    public InterstitialFragment(InterstitialAdView rootView) {
        this.rootView = rootView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.interstitial_layout,container,false);
        RelativeLayout interHeader = relativeLayout.findViewById(R.id.interHeader);
        int color = Tools.getWindowColor(getActivity());
        color = color!=-1 ? color : Color.LTGRAY;
        interHeader.setBackgroundColor(color);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(rootView.getLayoutParams());
        layoutParams.addRule(RelativeLayout.BELOW,R.id.interHeader);
        rootView.setLayoutParams(layoutParams);

        Tools.removeViewFromParent(rootView);
        relativeLayout.addView(rootView);
        ImageButton imageButton = relativeLayout.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootView.dismiss();
            }
        });
        imageButton.bringToFront();
        return relativeLayout;
    }


}
