package com.mobucks.androidsdk.tools;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.mobucks.androidsdk.logger.Logger;
import com.mobucks.androidsdk.models.Ad;
import com.mobucks.androidsdk.network.NetworkService;
import com.mobucks.androidsdk.network.callbacks.NetworkCall;
import com.mobucks.androidsdk.tools.vastparser.models.VideoTrackEvent;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Tools {
    /**
     * Transforms xml document to string.
     *
     * @param doc
     * @return
     */
    public static String xmlDocumentToString(Document doc) {
        String xml = null;
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            StringWriter sw = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(sw));

            xml = sw.toString();

        } catch (Exception e) {
            Logger.e("xmlDocumentToString error", e);
        }

        return xml;
    }

    /**
     * Transforms xml document node to string.
     *
     * @param node
     * @return
     */
    public static String xmlDocumentToString(Node node) {
        String xml = null;
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer
                    .setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "4");

            StringWriter sw = new StringWriter();
            transformer.transform(new DOMSource(node), new StreamResult(sw));

            xml = sw.toString();

        } catch (Exception e) {
            Logger.e("xmlDocumentToString error", e);
        }

        return xml;
    }

    /**
     * Transforms xml string to document.
     *
     * @param doc
     * @return
     */
    public static Document stringToDocument(String doc) {

        DocumentBuilder db;
        Document document = null;
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(doc));

            document = db.parse(is);

        } catch (Exception e) {
            Logger.e("stringToDocument error", e);
        }
        return document;

    }

    /**
     * Read all stream data to a string.
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String stringFromStream(InputStream inputStream)
            throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;

        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }

        byte[] bytes = baos.toByteArray();

        return new String(bytes, "UTF-8");
    }

    /**
     * Writes a stream to a file.
     *
     * @param inputStream
     * @param output
     * @throws IOException
     */
    public static void streamToFile(InputStream inputStream, File output)
            throws IOException {
        FileOutputStream fout = new FileOutputStream(output);
        byte[] buffer = new byte[1024];
        int length = 0;

        while ((length = inputStream.read(buffer)) != -1) {
            fout.write(buffer, 0, length);
        }
        fout.flush();
        fout.close();
    }

    /**
     * Hashes string to md5.
     *
     * @param string
     * @return
     */
    public static String toMd5(String string) {
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());
            byte[] digest = md.digest();

            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
        } catch (NoSuchAlgorithmException e) {
            Logger.e("toMd5 error", e);
        }
        return sb.toString();
    }

    /**
     * Returns the xml node value.
     *
     * @param node
     * @return
     */
    public static String getElementValue(Node node) {

        NodeList childNodes = node.getChildNodes();
        Node child;
        String value = null;
        CharacterData cd;

        for (int childIndex = 0; childIndex < childNodes.getLength(); childIndex++) {
            child = childNodes.item(childIndex);
            // value = child.getNodeValue().trim();
            cd = (CharacterData) child;
            value = cd.getData().trim();

            if (value.length() == 0) {
                // this node was whitespace
                continue;
            }
            return value;

        }
        return value;
    }

    /**
     * Gets android device screen size.
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getScreenSize(Context context) {
        if (!(context instanceof Activity)) {
            return null;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    /**
     * Returns node attribute value
     *
     * @param node
     * @param attrName
     * @return
     */
    public static String getAttributeValue(Node node, String attrName) {
        String val = "";
        if (node != null) {
            Node namedItem = node.getAttributes().getNamedItem(attrName);
            if (namedItem != null) {
                val = namedItem.getNodeValue();
            }
        }
        return val;
    }

    /**
     * True if video is downloaded in cache.
     *
     * @param context
     * @param videoUrl
     * @return
     */
    public static boolean isVideoAvailableInCache(Context context, String videoUrl) {
        return getLocalVideoFile(context, videoUrl).exists();
    }

    /**
     * Get local video url in cache.
     *
     * @param context
     * @param videoUrl
     * @return
     */
    public static File getLocalVideoFile(Context context, String videoUrl) {
        File videoDir = new File(context.getCacheDir(), "videos");
        return new File(videoDir, Tools.toMd5(videoUrl) + ".video");
    }

    /**
     * @param events
     * @param duration
     * @return
     */
    public static Map<Integer, List<String>> getTimeEventsTrackers(Map<VideoTrackEvent, List<String>> events, long duration) {
        Map<Integer, List<String>> timeBasedTrackers = new HashMap<>();
        for (Map.Entry<VideoTrackEvent, List<String>> entry : events.entrySet()) {
            VideoTrackEvent trackingEvent = entry.getKey();
            switch (trackingEvent) {
                case start:
                    int key = 0;
                    timeBasedTrackers.put(key, entry.getValue());
                    break;
                case firstQuartile:
                    key = (int) (duration * 0.25);
                    timeBasedTrackers.put(key, entry.getValue());
                    break;
                case midpoint:
                    key = (int) (duration * 0.5);
                    timeBasedTrackers.put(key, entry.getValue());
                    break;
                case thirdQuartile:
                    key = (int) (duration * 0.75);
                    timeBasedTrackers.put(key, entry.getValue());
                    break;
                case complete:
                    key = (int) (duration);
                    timeBasedTrackers.put(key, entry.getValue());
                    break;
            }
        }
        return timeBasedTrackers;
    }

    /**
     * Converts vast duration to millis
     *
     * @param duration
     * @return
     */
    public static long vastDurationToMillis(String duration) {
        String[] timeUnits = duration.split("[:.]");
        if (timeUnits.length < 3) return 0;
        int hours = Integer.parseInt(timeUnits[0]);
        int minutes = Integer.parseInt(timeUnits[1]);
        int sec = Integer.parseInt(timeUnits[2]);
        int millis = 0;
        if (timeUnits.length > 3) {
            millis = Integer.parseInt(timeUnits[3]);
        }
        return hours * 3600000 + minutes * 60000 + sec * 1000 + millis;
    }

    /**
     * Tracks speficic event
     *
     * @param event
     * @param ad
     */
    public static void trackEvent(VideoTrackEvent event, Ad ad) {
        if (ad == null || ad.getVideoData() == null || ad.getVideoData().getEventsMap() == null) {
            return;
        }

        List<String> trackers = ad.getVideoData().getEventsMap().remove(event);
        if (trackers != null) {
            updateTrackers(trackers);
        }
    }

    /**
     * Get request on tracker url
     *
     * @param trackers
     */
    public static void updateTrackers(List<String> trackers) {
        NetworkService networkService = new NetworkService();
        for (final String url : trackers) {
            networkService.get(url, new NetworkCall() {
                @Override
                public void onComplete(Object result) {
                    Logger.d("Tracker updated " + url);
                }

                @Override
                public void onError(Exception error) {
                    Logger.e("Tracker failed " + url, error);
                }
            });
        }
    }

    /**
     * Converts exception stacktrace to string.
     *
     * @param e
     * @return
     */
    public static String exceptionToStackTrace(Exception e) {
        if (e != null) {
            StringWriter sw = new StringWriter();
            PrintWriter printWriter = new PrintWriter(sw);
            e.printStackTrace(printWriter);
            return sw.toString();
        }
        return null;
    }

    /**
     * Removes view from parentView if any.
     *
     * @param view
     */
    public static void removeViewFromParent(View view) {
        if ((view != null) && (view.getParent() instanceof ViewGroup)) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    /**
     * Cleans video caches, remove files that are stored for more than 5 hours.
     *
     * @param context
     */
    public static void clearVideoCache(Context context) {
        File videoDir = new File(context.getCacheDir(), "videos");
        File[] videos = videoDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                long diff = new Date().getTime() - file.lastModified();
                return (diff > 5 * 60 * 60 * 1000) && file.getName().endsWith("video");
            }
        });

        if (videos == null || videos.length == 0) {
            return;
        }

        for (File fileToBeDeleted : videos) {
            fileToBeDeleted.delete();
        }
    }

    /**
     * Returns android primary window color
     *
     * @param context
     * @return
     */
    public static int getWindowColor(Context context) {
        TypedValue a = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorPrimary, a, true);
        if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            // windowBackground is a color
            return a.data;
        }
        return -1;
    }

    /**
     * Adds protocol if there is not any
     *
     * @param url
     * @return
     */
    public static String transformFailUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }

        if (url.startsWith("//")) {
            url = "https:" + url;
        }
        return url;
    }

}
