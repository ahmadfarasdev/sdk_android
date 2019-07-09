package com.mobucks.androidsdk.tools.xmlparser;

import com.mobucks.androidsdk.tools.Tools;
import com.mobucks.androidsdk.tools.vastparser.models.VideoTrackEvent;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class ToolsTest {
    private  String xmlTest ="<test><data><fname>Jane</fname><lname>Doe</lname><age>25</age><email>jane@doe.com</email></data></test>";

    @Test
    public  void stringToDocumentTest(){
        Document document = Tools.stringToDocument(xmlTest);
        NodeList testNode = document.getElementsByTagName("test");
        assertEquals(testNode.getLength(),1);

        Node test = testNode.item(0);
        assertEquals(test.getFirstChild().getNodeName(),"data");

        Node data = test.getFirstChild();
        NodeList dataItems = data.getChildNodes();
        assertEquals(dataItems.item(0).getNodeName(),"fname");
        assertEquals(dataItems.item(0).getTextContent(),"Jane");

        assertEquals(dataItems.item(1).getNodeName(),"lname");
        assertEquals(dataItems.item(1).getTextContent(),"Doe");


        assertEquals(dataItems.item(2).getNodeName(),"age");
        assertEquals(dataItems.item(2).getTextContent(),"25");


        assertEquals(dataItems.item(3).getNodeName(),"email");
        assertEquals(dataItems.item(3).getTextContent(),"jane@doe.com");

    }

    @Test
    public  void getElementValueTest(){
        Document document = Tools.stringToDocument(xmlTest);

        NodeList testNode = document.getElementsByTagName("fname");
        Node fname = testNode.item(0);
        String elementValue = Tools.getElementValue(fname);
        assertEquals(elementValue,"Jane");
    }

    @Test
    public  void xmlDocumentToStringTest(){
        Document document = Tools.stringToDocument(xmlTest);
        String xmlString = Tools.xmlDocumentToString(document);
        assertEquals(xmlString.replaceAll("\r\n",""),"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
                "<test>" +
                "    <data>" +
                "        <fname>Jane</fname>" +
                "        <lname>Doe</lname>" +
                "        <age>25</age>" +
                "        <email>jane@doe.com</email>" +
                "    </data>" +
                "</test>");

    }

    @Test
    public  void xmlDocumentToStringNodeTest(){
        Document document = Tools.stringToDocument(xmlTest);

        NodeList testNode = document.getElementsByTagName("fname");
        Node fname = testNode.item(0);
        String fnameString = Tools.xmlDocumentToString(fname);
        assertEquals(fnameString.replaceAll("\r\n",""),"<fname>Jane</fname>");
    }

    @Test
    public  void stringFromStreamTest() throws IOException {
        String testString = "Hello World";
        InputStream inputStream = new ByteArrayInputStream(testString.getBytes());
        String generatedString = Tools.stringFromStream(inputStream);
        assertEquals(generatedString,testString);
    }

    @Test
    public  void toMd5Test(){
        String md5 = Tools.toMd5("hello world!");
        assertEquals(md5,"fc3ff98e8c6a0d3087d515c0473f8677");
    }

    @Test
    public  void streamToFileTest() throws IOException {
        String someString = "i am going to be saved in a file";
        InputStream inputStream = new ByteArrayInputStream(someString.getBytes());
        File file =new File("test.file");
        Tools.streamToFile(inputStream,file );
        FileInputStream fis = new FileInputStream(file);
        String fromFile = Tools.stringFromStream(fis);
        assertEquals(fromFile,someString);

    }

    @Test
    public  void getTimeEventsTrackersTest() {
        HashMap<VideoTrackEvent, List<String>> events =new HashMap<>();
        events.put(VideoTrackEvent.start, Collections.singletonList("start"));
        events.put(VideoTrackEvent.firstQuartile, Collections.singletonList("firstQuartile"));
        events.put(VideoTrackEvent.midpoint, Collections.singletonList("midpoint"));
        events.put(VideoTrackEvent.thirdQuartile, Collections.singletonList("thirdQuartile"));
        events.put(VideoTrackEvent.complete, Collections.singletonList("complete"));
        Map<Integer,List<String>> eventsOnTime = Tools.getTimeEventsTrackers(events,5000);
        assertEquals(eventsOnTime.get(0).get(0),"start");
        assertEquals(eventsOnTime.get(1250).get(0),"firstQuartile");
        assertEquals(eventsOnTime.get(2500).get(0),"midpoint");
        assertEquals(eventsOnTime.get(3750).get(0),"thirdQuartile");
        assertEquals(eventsOnTime.get(5000).get(0),"complete");

    }

    @Test
    public  void vastDurationToMillisTest() {
        assertEquals(Tools.vastDurationToMillis("00:00:05.31"),5031);
        assertEquals(Tools.vastDurationToMillis("00:01:05.31"),65031);
        assertEquals(Tools.vastDurationToMillis("01:01:05.31"),3665031);
    }

    @Test
    public  void transformFailUrlTest() {
        assertEquals(Tools.transformFailUrl("//example.com"),"https://example.com");
        assertEquals(Tools.transformFailUrl("example.com"),"example.com");
        assertEquals(Tools.transformFailUrl("https://example.com"),"https://example.com");
    }
}
