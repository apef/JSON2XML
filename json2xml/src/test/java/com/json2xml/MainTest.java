package com.json2xml;

import org.junit.jupiter.api.DisplayName;
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

  // Tests to ensure that all needed files are able to be read.
  @Test
  @DisplayName("Verifies if the resource reader method is able to read a file from the classpath")
  void filesAreAbleToBeReadFromClasspath() {
    String json = readResource("rawdata.json");
    assertFalse(json == null);
  }

  @Test
  @DisplayName("Tries to parse JSON in order to verify if it is valid or not")
  void verifiesIfValidJson() {
    String validJson = readResource("null_id_removed.json");
    String invalidJson = "geometry: {x:this, y: shouldFail";
    Parser parse = new Parser();
    Boolean isValid = parse.isValidJson(validJson);
    Boolean isNotValid = parse.isValidJson(invalidJson);

    assertTrue(isValid);
    assertFalse(isNotValid);

  }

  @Test
  @DisplayName("Verifies if the output from the parser is not null")
  void parserOutputNotNull() throws XMLStreamException, IOException {
    String json = readResource("null_id_removed.json");

    Parser parser = new Parser();
    String xml = parser.parseXML(json);

    assertNotNull(xml);
  }

  @Test
  @DisplayName("Tests to verify that the parser is able to detect IDs that are null and handles them correctly")
  void changesNullIdToReadableResult() throws XMLStreamException {
    String expectedResult = "ID saknas";
    String nullId = null;

    Parser parser = new Parser();
    String result = parser.handleNullId(nullId);

    assertEquals(expectedResult, result);
  }

  
  @Test
  @DisplayName("Tests the parser in order to see if it handles 'GA' owner names properly")
  void generalizesOwnerNames() {
    String owner = "MELBY GA:2";
    String expectedResult = "GA (fastighetesägare i området)";

    Parser parser = new Parser();
    String actual = parser.generalizeOwnerName(owner);

    assertEquals(expectedResult, actual);
  }


  @Test
  @DisplayName("Verifies if the converted input JSON into XML is identical with the expected XML output")
  void convertsJsonToExpectedXml() throws XMLStreamException, IOException {
    // Using input JSON that has null ID's removed
    // This ensures that the input and output match exactly (as the expected output has removed null ID entries)
    String json = readResource("null_id_removed.json");
    String expectedXml = readResource("expectedXml.xml");

    Parser parser = new Parser();
    String convertedXML = parser.parseXML(json);

    assertEquals(expectedXml, convertedXML);
  }

  @Test
  @DisplayName("Tries to parse XML in order to verify if it has valid formatting")
  void verifiesIfValidXML() throws IOException, XMLStreamException {
    String json = readResource("null_id_removed.json");
    String invalidXML = readResource("invalidXml.xml");

    Parser parser = new Parser();
    String convertedXML = parser.parseXML(json);


    assertTrue(verifyXML(convertedXML));
    assertFalse(verifyXML(invalidXML));
  }

  @Test
  @DisplayName("Verifies if the parser correctly handles a case where no features (lamps) were provided")
  void EmptyFeaturesShouldLeadToEmptyGeometriesElement() throws XMLStreamException, IOException {
    String json = readResource("dataNoFeatures.json");
    String expectedResult = readResource("EmptyElementXML.XML");
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

  private static String readResource(String path) {
    ClassLoader cLoader = Thread.currentThread().getContextClassLoader();
    String jsonStr = null;
    try (InputStream in = cLoader.getResourceAsStream(path)) {
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
   * @param xml the provided XML in a string representation
   * @return true if valid XML format or false if invalid
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
