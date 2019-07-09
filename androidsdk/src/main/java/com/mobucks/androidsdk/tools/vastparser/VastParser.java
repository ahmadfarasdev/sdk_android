package com.mobucks.androidsdk.tools.vastparser;

import android.util.Xml;

import com.mobucks.androidsdk.logger.Logger;
import com.mobucks.androidsdk.tools.Tools;
import com.mobucks.androidsdk.tools.vastparser.models.Creative;
import com.mobucks.androidsdk.tools.vastparser.models.Creatives;
import com.mobucks.androidsdk.tools.vastparser.models.InLine;
import com.mobucks.androidsdk.tools.vastparser.models.Linear;
import com.mobucks.androidsdk.tools.vastparser.models.MediaFile;
import com.mobucks.androidsdk.tools.vastparser.models.MediaFiles;
import com.mobucks.androidsdk.tools.vastparser.models.TrackingEvents;
import com.mobucks.androidsdk.tools.vastparser.models.Vast;
import com.mobucks.androidsdk.tools.vastparser.models.VastAd;
import com.mobucks.androidsdk.tools.vastparser.models.VideoClicks;
import com.mobucks.androidsdk.tools.vastparser.models.VideoTrackEvent;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VastParser {
    // We don't use namespaces
    private static final String ns = null;

    public Vast parse(InputStream in) throws XmlPullParserException, IOException {
        return parse(in, Xml.newPullParser());
    }

    /**
     * Parse vast file
     *
     * @param in
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public Vast parse(InputStream in, XmlPullParser parser) throws XmlPullParserException, IOException {

        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            return readVast(parser);
        } finally {
            in.close();
        }
    }

    /**
     * Reads vast tag
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Vast readVast(XmlPullParser parser) throws XmlPullParserException, IOException {
        Vast vast = new Vast();


        parser.require(XmlPullParser.START_TAG, ns, "VAST");
        vast.setVersion(parser.getAttributeValue(ns, "version"));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("Ad")) {
                vast.setAd(readVastAd(parser));
            }

        }
        return vast;
    }
    /**
     * Reads ad tag
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private VastAd readVastAd(XmlPullParser parser) throws XmlPullParserException, IOException {
        VastAd vastAd = new VastAd();
        parser.require(XmlPullParser.START_TAG, ns, "Ad");
        vastAd.setId(parser.getAttributeValue(ns, "id"));

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("InLine")) {
                vastAd.setInLine(readInLine(parser));
            } else {
                skip(parser);
            }

        }
        return vastAd;
    }
    /**
     * Reads inline tag
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private InLine readInLine(XmlPullParser parser) throws XmlPullParserException, IOException {
        InLine inLine = new InLine();
        parser.require(XmlPullParser.START_TAG, ns, "InLine");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("AdSystem")) {
                inLine.setAdSystem(readAttribute("AdSystem", parser));
            } else if (name.equals("AdTitle")) {
                inLine.setAdTitle(readAttribute("AdTitle", parser));
            } else if (name.equals("Impression")) {
                inLine.getImpressions().add(Tools.transformFailUrl(readAttribute("Impression", parser)));
            } else if (name.equals("Creatives")) {
                inLine.setCreatives(readCreatives(parser));
            } else {
                skip(parser);
            }

        }
        return inLine;
    }

    /**
     * Reads creatives tag
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Creatives readCreatives(XmlPullParser parser) throws XmlPullParserException, IOException {
        Creatives creatives = new Creatives();
        parser.require(XmlPullParser.START_TAG, ns, "Creatives");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("Creative")) {
                creatives.getCreativeList().add(readCreative(parser));
            } else {
                skip(parser);
            }

        }
        return creatives;
    }

    /**
     * Reads creative tag
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Creative readCreative(XmlPullParser parser) throws XmlPullParserException, IOException {
        Creative creative = new Creative();

        parser.require(XmlPullParser.START_TAG, ns, "Creative");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("Linear")) {
                creative.setLinear(readLinear(parser));
            } else {
                skip(parser);
            }
        }
        return creative;
    }

    /**
     * Reads linear tag
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Linear readLinear(XmlPullParser parser) throws XmlPullParserException, IOException {
        Linear linear = new Linear();
        parser.require(XmlPullParser.START_TAG, ns, "Linear");
        linear.setSkipoffset(parser.getAttributeValue(ns, "skipoffset"));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("Duration")) {
                linear.setDuration(readAttribute("Duration", parser));
            } else if (name.equals("VideoClicks")) {
                linear.setVideoClicks(readVideoClicks(parser));
            } else if (name.equals("TrackingEvents")) {
                linear.setTrackingEvents(readTrackingEvents(parser));
            } else if (name.equals("MediaFiles")) {
                linear.setMediaFiles(readMediaFiles(parser));
            } else {
                skip(parser);
            }

        }
        return linear;
    }

    /**
     * Reads videoclicks tag
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private VideoClicks readVideoClicks(XmlPullParser parser) throws XmlPullParserException, IOException {
        VideoClicks videoClicks = new VideoClicks();
        parser.require(XmlPullParser.START_TAG, ns, "VideoClicks");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("ClickThrough")) {
                videoClicks.setClickThrough(Tools.transformFailUrl(readAttribute("ClickThrough", parser)));
            } else if (name.equals("ClickTracking")) {
                videoClicks.getClickTracking().add(Tools.transformFailUrl(readAttribute("ClickTracking", parser)));
            } else if (name.equals("CustomClick")) {
                videoClicks.getCustomClick().add(Tools.transformFailUrl(readAttribute("CustomClick", parser)));
            } else {
                skip(parser);
            }

        }
        return videoClicks;
    }

    /**
     * Reads tracking events tag
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private TrackingEvents readTrackingEvents(XmlPullParser parser) throws XmlPullParserException, IOException {
        TrackingEvents trackingEvents = new TrackingEvents();
        parser.require(XmlPullParser.START_TAG, ns, "TrackingEvents");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("Tracking")) {
                VideoTrackEvent event;
                try {
                    event = VideoTrackEvent.valueOf(parser.getAttributeValue(ns, "event"));
                } catch (IllegalArgumentException e) {
                    continue;
                }

                String tracker = readAttribute("Tracking", parser);
                Map<VideoTrackEvent, List<String>> trackerMap = trackingEvents.getEvents();
                List<String> trackers = trackerMap.get(event);
                if (trackers == null) {
                    trackers = new ArrayList<>();
                }
                trackers.add(Tools.transformFailUrl(tracker));
                trackerMap.put(event, trackers);
            } else {
                skip(parser);
            }

        }
        return trackingEvents;
    }

    /**
     * Reads mediafiles tag
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private MediaFiles readMediaFiles(XmlPullParser parser) throws XmlPullParserException, IOException {
        MediaFiles mediaFiles = new MediaFiles();
        parser.require(XmlPullParser.START_TAG, ns, "MediaFiles");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals("MediaFile")) {
                MediaFile mediaFile = new MediaFile();
                mediaFile.setApiFramework(parser.getAttributeValue(ns, "apiFramework"));
                mediaFile.setBitrate(parser.getAttributeValue(ns, "bitrate"));
                mediaFile.setDelivery(parser.getAttributeValue(ns, "delivery"));
                mediaFile.setHeight(parser.getAttributeValue(ns, "height"));
                mediaFile.setId(parser.getAttributeValue(ns, "id"));
                mediaFile.setMaintainAspectRatio(parser.getAttributeValue(ns, "maintainAspectRatio"));
                mediaFile.setScalable(parser.getAttributeValue(ns, "scalable"));
                mediaFile.setType(parser.getAttributeValue(ns, "type"));
                mediaFile.setWidth(parser.getAttributeValue(ns, "width"));
                mediaFile.setValue(readAttribute("MediaFile", parser));
                mediaFiles.getMediaFileList().add(mediaFile);
            } else {
                skip(parser);
            }

        }
        return mediaFiles;
    }


    /**
     * Skip tags
     *
     * @param parser
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    /**
     * Read tag attributes
     *
     * @param attrib
     * @param parser
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readAttribute(String attrib, XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, attrib);
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, attrib);
        return result;
    }

    /**
     * Read tag text
     *
     * @param parser
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        try {
            if (parser.next() == XmlPullParser.TEXT) {
                result = parser.getText();
                parser.nextTag();
            }
        } catch (Exception e) {
            tryNextTag(parser);
            Logger.e("Error While getting text", e);
        }

        return result;
    }

    /**
     * Tries to get next valid tag
     * @param pullParser
     */
    private void tryNextTag(XmlPullParser pullParser){
        try {
            pullParser.nextTag();
        } catch (Exception e) {
            tryNextTag(pullParser);
        }
    }
}
