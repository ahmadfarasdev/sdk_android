package com.mobucks.androidsdk;

import android.support.test.runner.AndroidJUnit4;
import android.util.Xml;

import com.mobucks.androidsdk.tools.vastparser.VastParser;
import com.mobucks.androidsdk.tools.vastparser.models.InLine;
import com.mobucks.androidsdk.tools.vastparser.models.Linear;
import com.mobucks.androidsdk.tools.vastparser.models.MediaFile;
import com.mobucks.androidsdk.tools.vastparser.models.Vast;
import com.mobucks.androidsdk.tools.vastparser.models.VideoTrackEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class VastParserTest {
    private String VAST ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<VAST version=\"3.0\">\n" +
            "    <Ad id=\"1\">\n" +
            "        <InLine>\n" +
            "            <AdSystem><![CDATA[Mobucks]]></AdSystem>\n" +
            "            <AdTitle><![CDATA[big bunny video]]></AdTitle>\n" +
            "            <Impression><![CDATA[//ib.mymobucks.com/track/impression?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==]]></Impression>\n" +
            "            <Creatives>\n" +
            "                <Creative>\n" +
            "                    <Linear skipoffset=\"00:00:06\">\n" +
            "                        <Duration>00:00:05.31</Duration>\n" +
            "                        <VideoClicks>\n" +
            "                            <ClickThrough><![CDATA[http://out-there-media.com]]></ClickThrough>\n" +
            "                            <ClickTracking><![CDATA[https://ib.mymobucks.com/track/landing?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==]]></ClickTracking>\n" +
            "                            <CustomClick><![CDATA[https://ib.mymobucks.com/track/custom?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==]]></CustomClick>\n" +
            "                        </VideoClicks>\n" +
            "                        <TrackingEvents>\n" +
            "                            <Tracking event=\"start\"><![CDATA[https://ib.mymobucks.com/track/start?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==]]></Tracking>\n" +
            "                            <Tracking event=\"pause\"><![CDATA[https://ib.mymobucks.com/track/pause?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==]]></Tracking>\n" +
            "                            <Tracking event=\"resume\"><![CDATA[https://ib.mymobucks.com/track/resume?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==]]></Tracking>\n" +
            "                            <Tracking event=\"skip\"><![CDATA[https://ib.mymobucks.com/track/skip?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==]]></Tracking>\n" +
            "                            <Tracking event=\"complete\"><![CDATA[https://ib.mymobucks.com/track/complete?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==]]></Tracking>\n" +
            "                            <Tracking event=\"firstQuartile\"><![CDATA[https://ib.mymobucks.com/track/firstQuartile?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==]]></Tracking>\n" +
            "                            <Tracking event=\"midpoint\"><![CDATA[https://ib.mymobucks.com/track/midpoint?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==]]></Tracking>\n" +
            "                            <Tracking event=\"thirdQuartile\"><![CDATA[https://ib.mymobucks.com/track/thirdQuartile?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==]]></Tracking>\n" +
            "                            <Tracking event=\"mute\"><![CDATA[https://ib.mymobucks.com/track/mute?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==]]></Tracking>\n" +
            "                            <Tracking event=\"unmute\"><![CDATA[https://ib.mymobucks.com/track/unmute?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==]]></Tracking>\n" +
            "                            <Tracking event=\"fullscreen\"><![CDATA[https://ib.mymobucks.com/track/fullscreen?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==]]></Tracking>\n" +
            "                            <Tracking event=\"exitFullscreen\"><![CDATA[https://ib.mymobucks.com/track/exitFullscreen?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==]]></Tracking>\n" +
            "                        </TrackingEvents>\n" +
            "                        <MediaFiles>\n" +
            "                            <MediaFile delivery=\"progressive\" type=\"video/mp4\" height=\"720\" width=\"1280\" bitrate=\"1589\"><![CDATA[http://www.mymobucks.com/video/5a82db0cce997.mp4]]></MediaFile>\n" +
            "                        </MediaFiles>\n" +
            "                    </Linear>\n" +
            "                </Creative>\n" +
            "            </Creatives>\n" +
            "        </InLine>\n" +
            "    </Ad>\n" +
            "</VAST>";
    @Test
    public void vastParserTest() throws IOException, XmlPullParserException {
        VastParser vastParser =new VastParser();
        XmlPullParser parser = Xml.newPullParser();
        Vast vast = vastParser.parse(new ByteArrayInputStream(VAST.getBytes("utf-8")),parser);


        assertEquals(vast.getVersion(),"3.0");
        assertEquals(vast.getAd().getId(),"1");
        InLine inLine = vast.getAd().getInLine();
        assertEquals(inLine.getAdSystem(),"Mobucks");
        assertEquals(inLine.getAdTitle(),"big bunny video");
        assertEquals(inLine.getImpressions().get(0),"https://ib.mymobucks.com/track/impression?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==");
        Linear linear = inLine.getCreatives().getCreativeList().get(0).getLinear();
        assertEquals(linear.getSkipoffset(),"00:00:06");
        assertEquals(linear.getDuration(),"00:00:05.31");
        assertEquals(linear.getVideoClicks().getClickThrough(),"http://out-there-media.com");
        assertEquals(linear.getVideoClicks().getClickTracking().get(0),"https://ib.mymobucks.com/track/landing?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==");
        assertEquals(linear.getVideoClicks().getCustomClick().get(0),"https://ib.mymobucks.com/track/custom?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==");
        Map<VideoTrackEvent,List<String>> events = linear.getTrackingEvents().getEvents();
        assertEquals(events.get(VideoTrackEvent.start).get(0),"https://ib.mymobucks.com/track/start?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==");
        assertEquals(events.get(VideoTrackEvent.pause).get(0),"https://ib.mymobucks.com/track/pause?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==");
        assertEquals(events.get(VideoTrackEvent.resume).get(0),"https://ib.mymobucks.com/track/resume?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==");
        assertEquals(events.get(VideoTrackEvent.skip).get(0),"https://ib.mymobucks.com/track/skip?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==");
        assertEquals(events.get(VideoTrackEvent.complete).get(0),"https://ib.mymobucks.com/track/complete?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==");
        assertEquals(events.get(VideoTrackEvent.firstQuartile).get(0),"https://ib.mymobucks.com/track/firstQuartile?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==");
        assertEquals(events.get(VideoTrackEvent.midpoint).get(0),"https://ib.mymobucks.com/track/midpoint?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==");
        assertEquals(events.get(VideoTrackEvent.thirdQuartile).get(0),"https://ib.mymobucks.com/track/thirdQuartile?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==");
        assertEquals(events.get(VideoTrackEvent.mute).get(0),"https://ib.mymobucks.com/track/mute?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==");
        assertEquals(events.get(VideoTrackEvent.unmute).get(0),"https://ib.mymobucks.com/track/unmute?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==");
        assertEquals(events.get(VideoTrackEvent.fullscreen).get(0),"https://ib.mymobucks.com/track/fullscreen?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==");
        assertEquals(events.get(VideoTrackEvent.exitFullscreen).get(0),"https://ib.mymobucks.com/track/exitFullscreen?q=eyJ1aWQiOiI0YWI1MmI3NWU3ZjdhZjgzZjkxYzg2MTA3ZTU2MTc5OSIsImNpZCI6IjQ2MDMiLCJ2aWRlb0ZpbGUiOiI1YTgyZGIwY2NlOTk3Lm1wNCIsInV1aWQiOiI1YmNlMDU4MzE3YjA1IiwicGxpZCI6IjEyMzcifQ==");

        MediaFile mediaFile = linear.getMediaFiles().getMediaFileList().get(0);
        assertEquals(mediaFile.getDelivery(),"progressive");
        assertEquals(mediaFile.getType(),"video/mp4");
        assertEquals(mediaFile.getHeight(),"720");
        assertEquals(mediaFile.getWidth(),"1280");
        assertEquals(mediaFile.getBitrate(),"1589");

    }
}
