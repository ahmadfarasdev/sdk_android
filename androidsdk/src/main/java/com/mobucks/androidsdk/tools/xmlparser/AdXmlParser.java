package com.mobucks.androidsdk.tools.xmlparser;

import android.util.Xml;

import com.mobucks.androidsdk.logger.Logger;
import com.mobucks.androidsdk.models.AdError;
import com.mobucks.androidsdk.models.AdResponse;
import com.mobucks.androidsdk.models.Content;
import com.mobucks.androidsdk.models.Info;
import com.mobucks.androidsdk.models.Main;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class AdXmlParser {
    // We don't use namespaces
    private static final String ns = null;

    public AdResponse parse(InputStream in) throws XmlPullParserException, IOException {
        return parse(in, Xml.newPullParser());
    }

    /**
     * Parse xml file
     *
     * @param in
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public AdResponse parse(InputStream in, XmlPullParser parser) throws XmlPullParserException, IOException {

        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            return readContent(parser);
        } finally {
            in.close();
        }
    }

    /**
     * Reads content tag
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private AdResponse readContent(XmlPullParser parser) throws XmlPullParserException, IOException {
        AdResponse adResponse = new AdResponse();
        adResponse.setContent(new Content());

        parser.require(XmlPullParser.START_TAG, ns, "content");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.toLowerCase().equals("main")) {
                Main main = new Main();
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    name = parser.getName();
                    if (name.equals("IMGURL")) {
                        main.setIMGURL(readAttribute("IMGURL", parser));
                    } else if (name.equals("IMGTEXT")) {
                        main.setIMGTEXT(readAttribute("IMGTEXT", parser));
                    } else if (name.equals("TITLE")) {
                        main.setTITLE(readAttribute("TITLE", parser));
                    } else if (name.equals("IMGLINK")) {
                        main.setIMGLINK(readAttribute("IMGLINK", parser));
                    } else if (name.equals("INFO")) {
                        main.setINFO(readInfo(parser));
                    } else {
                        skip(parser);
                    }
                }

                adResponse.getContent().setMain(main);
            }else if (name.toLowerCase().equals("error")) {
                adResponse.getContent().setError(readError(parser));
            }else {
                skip(parser);
            }
        }
        return adResponse;
    }
    private AdError readError(XmlPullParser parser) throws IOException, XmlPullParserException {
        AdError adError = new AdError();
        parser.require(XmlPullParser.START_TAG, ns, "error");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("code")) {
                adError.setCode(readAttribute("code", parser));
            } else if (name.equals("description")) {
               adError.setDescription(readAttribute("description", parser));
            }
        }
        return  adError;
    }

    /**
     * Reads info tag
     *
     * @param parser
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private Info readInfo(XmlPullParser parser) throws IOException, XmlPullParserException {
        Info info = new Info();
        parser.require(XmlPullParser.START_TAG, ns, "INFO");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("tagId")) {
                info.setTagId(readAttribute("tagId", parser));
            } else if (name.equals("memberId")) {
                info.setMemberId(readAttribute("memberId", parser));
            }
        }
        return info;
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
