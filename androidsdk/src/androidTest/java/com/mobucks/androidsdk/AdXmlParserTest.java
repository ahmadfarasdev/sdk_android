package com.mobucks.androidsdk;

import android.support.test.runner.AndroidJUnit4;
import android.util.Xml;

import com.mobucks.androidsdk.models.Ad;
import com.mobucks.androidsdk.models.AdResponse;
import com.mobucks.androidsdk.tools.xmlparser.AdXmlParser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AdXmlParserTest {
    private final String adXML = "<content>\n" +
            "<main>\n" +
            "<IMGURL>http://cdn.mymobucks.com/client_348/source/3789_100gigs.jpg</IMGURL>\n" +
            "<IMGTEXT>100 gigs hook up</IMGTEXT>\n" +
            "<TITLE>test campaign 100918</TITLE>\n" +
            "<IMGLINK>http://www.mymobucks.com/mobucksv2/lnkredirector.php</IMGLINK>\n" +
            "</main>\n" +
            "</content>";

    @Test
    public void adXmlParserTest() throws IOException, XmlPullParserException {
        AdXmlParser adXmlParser = new AdXmlParser();
        XmlPullParser parser = Xml.newPullParser();
        AdResponse adResponse = adXmlParser.parse(new ByteArrayInputStream(adXML.getBytes("utf-8")), parser);
        Ad ad = adResponse.getAd();
        assertEquals(ad.getImageUrl(),"http://cdn.mymobucks.com/client_348/source/3789_100gigs.jpg");
        assertEquals(ad.getImageText(),"100 gigs hook up");
        assertEquals(ad.getTitle(),"test campaign 100918");
        assertEquals(ad.getImageLink(),"http://www.mymobucks.com/mobucksv2/lnkredirector.php");
    }
}
