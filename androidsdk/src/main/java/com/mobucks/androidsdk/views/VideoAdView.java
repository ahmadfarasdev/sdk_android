package com.mobucks.androidsdk.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.appnexus.opensdk.ResultCode;
import com.appnexus.opensdk.instreamvideo.Quartile;
import com.appnexus.opensdk.instreamvideo.VideoAd;
import com.appnexus.opensdk.instreamvideo.VideoAdLoadListener;
import com.appnexus.opensdk.instreamvideo.VideoAdPlaybackListener;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.mobucks.androidsdk.R;
import com.mobucks.androidsdk.activities.VideoFullScreenActivity;
import com.mobucks.androidsdk.exceptions.NoAdException;
import com.mobucks.androidsdk.logger.Logger;
import com.mobucks.androidsdk.models.Ad;
import com.mobucks.androidsdk.models.VideoData;
import com.mobucks.androidsdk.network.callbacks.NetworkCall;
import com.mobucks.androidsdk.network.tasks.CheckInternet;
import com.mobucks.androidsdk.network.tasks.DownloadVideo;
import com.mobucks.androidsdk.tools.Tools;
import com.mobucks.androidsdk.tools.vastparser.VastParser;
import com.mobucks.androidsdk.tools.vastparser.models.Vast;
import com.mobucks.androidsdk.tools.vastparser.models.VideoTrackEvent;

import org.w3c.dom.Document;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VideoAdView extends AdView<VideoAdView> implements Player.EventListener, VideoListener {

    private RelativeLayout startLayout;
    private RelativeLayout mobucksVideo;
    private ImageView playVideoButton;
    private PlayerView playerView;
    private DataSource.Factory dataSourceFactory;
    private SimpleExoPlayer player;
    private ImageView mute, unmute, fullscreen, fullscreen_exit, play, pause;
    private Ad ad;

    //appNexus
    private VideoAd videoAd;
    private RelativeLayout appNexusVideo;
    private Handler mHandler = new Handler();
    private long playerMonitorInterval = 500;
    private boolean afterPrepare = false;
    private boolean isAppNexusAd =false;
    /*
        Task that monitors video events
     */
    private Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {

            if (isPlaying()) {
                VideoData videoData = ad.getVideoData();
                if (videoData != null) {
                    long currentPosition = player.getCurrentPosition();
                    //  long duration = player.getDuration()>0? player.getDuration(): videoData.getDuration();
                    List<Integer> toBeRemoved = new ArrayList<>();
                    Map<Integer, List<String>> timeEvents = videoData.getTimeBasedEventMap();
                    for (Map.Entry<Integer, List<String>> entry : timeEvents.entrySet()) {
                        if (entry.getKey() <= currentPosition) {
                            toBeRemoved.add(entry.getKey());
                            Tools.updateTrackers(entry.getValue());
                        }
                    }
                    for (Integer key : toBeRemoved) {
                        timeEvents.remove(key);
                    }

                }
            }
            if (hasPlaybackEnded()) {
                player.setPlayWhenReady(false);
                return;
            }
            mHandler.postDelayed(mHandlerTask, playerMonitorInterval);

        }
    };

    /**
     * Creates an VideoAdView with the minimum required params
     * @param placementId
     * @param uid
     * @param password
     * @param context
     */
    public VideoAdView(@NonNull String placementId, @NonNull String uid, @NonNull String password, @NonNull Context context) {
        super(placementId, uid, password, context);
        createUi(context);
        createPlayer(context);
        Tools.clearVideoCache(context);
    }

    /**
     * Constructor expects placementId,uid and password from layout xml
     * @param context
     */
    public VideoAdView(@NonNull Context context) {
        this(context, null, 0, 0);
    }
    /**
     * Constructor expects placementId,uid and password from layout xml
     * @param context
     * @param attrs
     */
    public VideoAdView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    /**
     * Constructor expects placementId,uid and password from layout xml
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public VideoAdView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /**
     * Constructor expects placementId,uid and password from layout xml
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    public VideoAdView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        createUi(context);
        createPlayer(context);
        Tools.clearVideoCache(context);
    }

    /**
     * Seek video playback to start
     */
    public void seekToStart() {
        player.seekTo(0);
    }

    /**
     * True if video is ended
     * @return
     */
    public boolean hasPlaybackEnded() {
        return player.getPlaybackState() == Player.STATE_ENDED;
    }

    /**
     * True if video is playing
     * @return
     */
    public boolean isPlaying() {
        return player.getPlaybackState() == Player.STATE_READY && player.getPlayWhenReady();
    }


    /**
     * Creates the video player ui
     * @param context
     */
    private void createUi(Context context) {
        mobucksVideo = new RelativeLayout(context);
        mobucksVideo.setBackgroundColor(Color.BLACK);
        mobucksVideo.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mobucksVideo.setGravity(Gravity.CENTER);

        playerView = new PlayerView(context);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        playerView.setLayoutParams(new ViewGroup.LayoutParams(1, 1));
        play = playerView.findViewById(R.id.exo_play);
        pause = playerView.findViewById(R.id.exo_pause);
        mute = playerView.findViewById(R.id.ic_mute);
        unmute = playerView.findViewById(R.id.ic_unmute);
        fullscreen = playerView.findViewById(R.id.fullscreen);
        fullscreen_exit = playerView.findViewById(R.id.fullscreen_exit);

        mobucksVideo.addView(playerView);
        this.addView(mobucksVideo);

        startLayout = new RelativeLayout(context);
        startLayout.setLayoutParams(new RelativeLayout.LayoutParams(1, 1));
        startLayout.setBackgroundColor(Color.TRANSPARENT);
        startLayout.setGravity(Gravity.CENTER);
        startLayout.bringToFront();

        playVideoButton = new ImageView(context);
        playVideoButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        playVideoButton.setImageResource(R.drawable.ic_play_video);

        playVideoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startLayout.setVisibility(GONE);
                play();

            }
        });

        startLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        startLayout.addView(playVideoButton);
        this.addView(startLayout);

        //appNexus

        appNexusVideo = new RelativeLayout(context);
        appNexusVideo.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        appNexusVideo.setVisibility(GONE);
        this.addView(appNexusVideo);

        videoAd = new VideoAd(context,"");

        videoAd.setAdLoadListener(new VideoAdLoadListener() {
            @Override
            public void onAdLoaded(VideoAd videoAd) {
                // Play the VideoAd by passing the container.
                appNexusMode();
                videoAd.playAd(appNexusVideo);
            }

            @Override
            public void onAdRequestFailed(VideoAd videoAd, ResultCode resultCode) {
                loadOfSite();
            }
        });

        videoAd.setVideoPlaybackListener(new VideoAdPlaybackListener() {
            @Override
            public void onAdPlaying(VideoAd videoAd) {

            }

            @Override
            public void onQuartile(VideoAd videoAd, Quartile quartile) {

            }

            @Override
            public void onAdCompleted(VideoAd videoAd, PlaybackCompletionState playbackCompletionState) {

            }

            @Override
            public void onAdMuted(VideoAd videoAd, boolean b) {

            }

            @Override
            public void onAdClicked(VideoAd videoAd) {

            }

            @Override
            public void onAdClicked(VideoAd videoAd, String s) {

            }
        });


    }

    /**
     * Shows the correct audio controls
     */
    private void updateVolumeControl() {
        boolean isMuted = player.getVolume() == 0;
        mute.setVisibility(isMuted ? GONE : VISIBLE);
        unmute.setVisibility(isMuted ? VISIBLE : GONE);
    }

    /**
     * Show fullscreen icon or exit fullscreen icon
     */
    private void updateFullscreenControl() {
        boolean isFullscreen = ad != null && ad.getVideoData() != null && ad.getVideoData().isFullscreen();
        fullscreen.setVisibility(isFullscreen ? GONE : VISIBLE);
        fullscreen_exit.setVisibility(isFullscreen ? VISIBLE : GONE);
    }

    /**
     * Configures player settings
     */
    private void configurePlayer() {
        updateVolumeControl();
        updateFullscreenControl();
        OnClickListener volumeToggleListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isGoingToBeMuted = player.getVolume() == 1;

                VideoTrackEvent actionToTrack = isGoingToBeMuted ? VideoTrackEvent.mute : VideoTrackEvent.unmute;
                Tools.trackEvent(actionToTrack, ad);

                player.setVolume(player.getVolume() == 0 ? 1 : 0);
                updateVolumeControl();
            }
        };
        mute.setOnClickListener(volumeToggleListener);
        unmute.setOnClickListener(volumeToggleListener);

        fullscreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Tools.trackEvent(VideoTrackEvent.fullscreen, ad);
                switchToFullScreenMode();
            }
        });

        fullscreen_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (VideoAdView.this.getContext() instanceof VideoFullScreenActivity) {
                    ((VideoFullScreenActivity) VideoAdView.this.getContext()).finish();
                    Tools.trackEvent(VideoTrackEvent.exitFullscreen, ad);
                }
            }
        });
        play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Tools.trackEvent(VideoTrackEvent.resume, ad);
                play(true);
            }
        });

        pause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Tools.trackEvent(VideoTrackEvent.pause, ad);
                pause();
            }
        });
        player.addListener(this);
        player.addVideoListener(this);
    }

    /**
     * Loads a video from a video file yrl
     * @param localVideoUrl
     */
    private void loadVideo(String localVideoUrl) {
        if(localVideoUrl==null || localVideoUrl.isEmpty()){
            Logger.e("InvalidS video url on :"+placementId,null);
            return;
        }
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(localVideoUrl));

        afterPrepare = true;
        player.prepare(videoSource);

        long currentPosition = ad.getVideoData().getCurrentPosition();
        if (currentPosition > 0) {
            player.seekTo(currentPosition);
        }


    }

    /**
     * Load a video from an ad .
     * If video is available in cache plays it immediately otherwise video is first downloaded and then played.
     * @param ad
     */
    public void loadVideoFromAd(Ad ad) {
        if (!ad.isValid() || !ad.isVideoAd() || !ad.hasValidMediaFile()) {
            Logger.w("Invalid ad");
            return;
        }

        this.ad = ad;

        updateVolumeControl();
        updateFullscreenControl();


        String videoUrl = ad.getVideoData().getVideoUrl();

        if (Tools.isVideoAvailableInCache(getContext(), videoUrl)) {
            loadVideo(Tools.getLocalVideoFile(getContext(), videoUrl).getAbsolutePath());
            return;
        }

        //If not available in cache download
        DownloadVideo downloadVideo = new DownloadVideo(new NetworkCall<String>() {
            @Override
            public void onComplete(String result) {
                loadVideo(result);
            }

            @Override
            public void onError(Exception error) {
                error.printStackTrace();
            }
        }, new File(getContext().getCacheDir(), "videos"));

        if (ad.getVideoData() != null && ad.getVideoData().getVideoUrl() != null) {
            downloadVideo.execute(videoUrl);
        }

    }

    /**
     * Creates a player instance
     * @param context
     */
    private void createPlayer(Context context) {
        player = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());

        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "mobuckssdk"));
        playerView.setPlayer(player);

        configurePlayer();

    }

    /**
     * Callback called on video clicked
     * @param ad
     */
    private void adClicked(Ad ad) {
        if (ad != null) {
            openToBrowser(ad);
            if (adListener != null) {
                adListener.onAdClicked(this);
            }
        }
    }

    /**
     * Switch to  appNexus mode
     */
    private void appNexusMode(){
        isAppNexusAd = true;
        appNexusVideo.setVisibility(VISIBLE);
        mobucksVideo.setVisibility(GONE);
        startLayout.setVisibility(GONE);
    }

    /**
     * Switch to  mobucks  mode
     */
    private void mobucksMode(){
        isAppNexusAd = false;
        appNexusVideo.setVisibility(GONE);
        mobucksVideo.setVisibility(VISIBLE);
        startLayout.setVisibility(VISIBLE);
    }

    /**
     * Returns the VideoView
     * @return
     */
    @Override
    VideoAdView getCurrentView() {
        return this;
    }

    /**
     * Destroys the ad
     */
    @Override
    public void destroy() {
        super.destroy();
        player.release();
        player = null;
        this.removeView(playerView);
        playerView = null;
        dataSourceFactory = null;
        mute = null;
        unmute = null;
        fullscreen = null;
        fullscreen_exit = null;
    }

    /**
     * Returns the current video position in millis
     * @return
     */
    public long getCurrentPosition() {
        return player.getCurrentPosition();
    }

    /**
     * Start video playback
     */
    public void play() {
        play(false);
    }

    /**
     * Start video playback , if seekToStart is true will play the video from the beginning.
     * @param seekToStart
     */
    public void play(boolean seekToStart) {
        if (hasPlaybackEnded()) {
            if (!seekToStart) {
                return;
            }
            seekToStart();
        }
        player.setPlayWhenReady(true);
        mHandler.removeCallbacks(mHandlerTask);
        mHandler.postDelayed(mHandlerTask, playerMonitorInterval);
    }

    /**
     * Pauses video playback
     */
    public void pause() {
        mHandler.removeCallbacks(mHandlerTask);
        player.setPlayWhenReady(false);
    }

    /**
     * Switch to fullscreen mode.
     * Starts VideoFullScreenActivity
     */
    private void switchToFullScreenMode() {
        pause();
        VideoData videoData = ad.getVideoData();
        videoData.setCurrentPosition(player.getCurrentPosition());
        videoData.setFullscreen(true);
        VideoFullScreenActivity.ad = new WeakReference<>(ad);
        VideoFullScreenActivity.innerVideoView = new WeakReference<>(this);
        getContext().startActivity(new Intent(getContext(), VideoFullScreenActivity.class));
    }

    /**
     * Callback called when the ad is loaded.
     * @param ad
     */
    @Override
    void adLoaded(Ad ad) {
        this.ad = ad;
        setRunOfSite(false);
        if (ad.isVideoAd()) {
            //show the play button
            mobucksMode();

            String vastString = ad.getVast();
            VastParser vastParser = new VastParser();
            try {
                Vast vast = vastParser.parse(new ByteArrayInputStream(vastString.getBytes()));
                if(!vast.isValidAd()) {
                    throw new Exception("Invalid vast ad");
                }
                ad.loadVideoData(vast);
                loadVideoFromAd(ad);
            } catch (Exception e) {
                Logger.e("Vast parsing error",e);
            }
        }
    }

    /**
     *  Loads extra params for VideoView
     * @param uriBuilder
     */
    @Override
    void loadParams(Uri.Builder uriBuilder) {
    }

    /**
     * Callback called when there is no fill
     * @param ad
     */
    @Override
    void adNoFill(Ad ad) {
        final String tagId = ad.getTagId();

        if (isRunOfSite()) {
            if (adListener != null) {
                adListener.onAdFailed(new NoAdException("No ad available"));
            }
            setRunOfSite(false);
            return;
        }

        if(tagId == null && !isRunOfSite()){
            loadOfSite();
            return;
        }

        CheckInternet checkInternet = new CheckInternet(getContext(), new NetworkCall<Boolean>() {
            @Override
            public void onComplete(Boolean result) {
                if(result) {
                    videoAd.setPlacementID(tagId);
                    videoAd.loadAd();
                }else {
                    loadOfSite();
                }

            }

            @Override
            public void onError(Exception error) {
                loadOfSite();
            }
        });

        checkInternet.execute("8.8.8.8");
    }

    //EventListener
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_IDLE:
                break;
            case Player.STATE_READY:
                if (afterPrepare) {
                    afterPrepare = false;
                    playerView.hideController();
                }

                break;

            case Player.STATE_BUFFERING:
                break;
            case Player.STATE_ENDED:
                onVideoEnded();
                break;
            default:
                System.out.println("theokir State " + playbackState);
        }
    }

    private void onVideoEnded() {
        if(ad.isValidVideo() && !ad.isClicked()){
            ad.setClicked(true);
            openToBrowser(ad);
            Tools.updateTrackers(ad.getVideoData().getVideoClickTrackers());
        }
    }
    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    //VideoListener
    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

        ViewGroup.LayoutParams layoutParams = playerView.getLayoutParams();
        layoutParams.width = this.getWidth();
        layoutParams.height = this.getWidth() * height / width;
        startLayout.getLayoutParams().width = layoutParams.width;
        startLayout.getLayoutParams().height = layoutParams.height;
        playVideoButton.getLayoutParams().width = layoutParams.width/4;
        playVideoButton.getLayoutParams().height = layoutParams.width/4;
        playerView.setLayoutParams(layoutParams);
        playerView.requestLayout();
    }

    @Override
    public void onRenderedFirstFrame() {
    }

    //Android life cycle

    /**
     * Needs to be calls on android activity life cycle onPause
     */
    @Override
    public void onPause() {
        mHandler.removeCallbacks(mHandlerTask);
        videoAd.activityOnPause();
        super.onPause();
    }

    /**
     * Needs to be calls on android activity life cycle onResume
     */
    @Override
    public void onResume() {
        super.onResume();
        videoAd.activityOnResume();
        removeCallbacks(mHandlerTask);
        mHandler.postDelayed(mHandlerTask, playerMonitorInterval);
    }

    /**
     * Needs to be calls on android activity life cycle onDestroy
     */
    @Override
    public void onDestroy() {
        videoAd.activityOnDestroy();
        super.onDestroy();
    }
}
