package com.json2xml;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

public class MainTest {
  @Test
  void convertsJsonToExpectedXml() {

    String json = readResource("/RAWDATA.JSON");
    String expectedXml = readResource("/ExpectedXML.XML");
    
    Parser parser = new Parser();
    String convertedXML = null;
    try {
      convertedXML = parser.parseXML(json);

    } catch (XMLStreamException err) {
      System.err.println(err);
    }

    
    assertEquals(expectedXml, convertedXML);
  }

  @Test
  void readJsonToString() {
    String json = readResource("/RAWDATA.JSON");
    assertFalse(json == null);
  }

  @Test
  void readXmlToString() {
    String xml = readResource("/ExpectedXML.XML");
    assertFalse(xml == null);
  }

  


  private static String readResource(String path) {
    String jsonStr = null;
    try (InputStream in = Main.class.getResourceAsStream(path)) {
      if (in == null) {
        throw new FileNotFoundException("JSON File could not be found on the classpath");
      } else {
        jsonStr = new String(in.readAllBytes());
      }
    } catch (IOException err) {
      err.printStackTrace();
    }

    return jsonStr;
  }
}
