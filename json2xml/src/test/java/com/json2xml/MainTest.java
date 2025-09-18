package com.json2xml;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class MainTest {
  @Test
  void convertsJsonToExpectedXml() throws XMLStreamException, IOException {

    String json = readResource("/Null_ID_removed.JSON");
    String expectedXml = readResource("/ExpectedXML.XML");

    Parser parser = new Parser();
    String convertedXML = parser.parseXML(json);

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

  @Test
  void verifiesIfValidJson() {
    String validJson = readResource("/Null_ID_removed.JSON");
    String invalidJson = "geometry: {x:this, y: shouldFail";
    Parser parse = new Parser();
    Boolean isValid = parse.isValidJson(validJson);
    Boolean isNotValid = parse.isValidJson(invalidJson);

    assertTrue(isValid);
    assertFalse(isNotValid);

  }

  @Test
  void verifiesIfValidXML() throws IOException, XMLStreamException {
    String json = readResource("/Null_ID_removed.JSON");
    String invalidXML = readResource("InvalidXML.XML");

    Parser parser = new Parser();
    String convertedXML = parser.parseXML(json);


    assertTrue(verifyXML(convertedXML));
    assertFalse(verifyXML(invalidXML));
  }

  @Test
  void parserOutputNotNull() throws XMLStreamException, IOException {
    String json = readResource("/Null_ID_removed.JSON");

    Parser parser = new Parser();
    String xml = parser.parseXML(json);

    assertNotNull(xml);
  }

  @Test
  void changesNullIdToReadableResult() throws XMLStreamException {
    String expectedResult = "ID saknas";
    String nullId = null;

    Parser parser = new Parser();
    String result = parser.handleNullId(nullId);

    assertEquals(expectedResult, result);
  }

  @Test
  void EmptyFeaturesShouldLeadToEmptyGeometriesElement() throws XMLStreamException, IOException {
    String json = readResource("/DataNoFeatures.JSON");
    String expectedResult = readResource("/EmptyElementXML.XML");
    Parser parser = new Parser();
    String xml = parser.parseXML(json);

    // both <Geometries\> and <Geometries></Geometries> are equivalent.
    // When I used XMLStreamWriter.WriteEmptyElement(ElementString) it produced:
    // "<Geometries" no ending "\>"
    // I cannot see the issue for why this happens, as I flush and close the streams
    // and write no attributes afterwards.
    // Due to this I chose to use <Geometries></Geometries> as the "empty element"
    // instead. Semantically, they are equivalent for any XML parser.
    assertEquals(expectedResult, xml);
  }

  @Test
  void generalizesOwnerNames() {
    String owner = "MELBY GA:2";
    String expectedResult = "GA (fastighetesägare i området)";

    Parser parser = new Parser();
    String actual = parser.generalizeOwnerName(owner);

    assertEquals(expectedResult, actual);
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

  /**
   * Verifies XML by trying to read it, if the XMLStreamReader cannot read the next line then the syntax/format
   * the provided XML is malformed and an exception is thrown. Source for this information: https://stackoverflow.com/questions/38255981/stax-well-formedness-check-of-xml
   * @param xml
   * @return
   */
  public static boolean verifyXML(String xml) {
    boolean isValidXML = false;
    XMLInputFactory XMLInFactory = XMLInputFactory.newFactory();

    XMLStreamReader XMLReader = null;
    try {
        XMLReader = XMLInFactory.createXMLStreamReader(new StringReader(xml));
        while (XMLReader.hasNext()) {
          XMLReader.next(); 
        } 

    } catch (XMLStreamException | NullPointerException err) {
        return isValidXML;
    } 

    isValidXML = true;
    return isValidXML;
  }
}
