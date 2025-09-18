package com.json2xml;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Parser {

  private final String SELECTABLE_ICONURL = "https://minasidor.testkommun.se/fileconnector/file/cesamh/456_felanmalan_belysning/trbel_normal_light_green_16px.png";
  private final String NON_SELECTABLE_ICONURL = "https://minasidor.testkommun.se/fileconnector/file/cesamh/456_felanmalan_belysning/trbel_normal_green_16px.png";
  private final String ICONURLKEY = "IconURL";
  private final String ATTRIBUTES = "attributes";
  private final String JSON_GEOMETRY = "geometry";
  private final String XML_GEOMETRY_ELEMENT = "Geometry";
  private final String XML_GEOMETRIES_ELEMENT = "Geometries";
  private final String JSON_OBJECTID = "OBJECTID";
  private final String XML_OBJECTID = "ObjectID";
  private final String OWNER = "Owner";
  private final String NAME = "Name";
  private final String DESCRIPTION = "Description";
  private final String WKTGEOMETRY = "WKTGeometry";
  private final String SELECTABLE = "Selectable";
  private final String JSON_FEATURES = "features";
  private final String FASTIGETSAGARE_STR = "ga:";
  private final Set<String> invalidNames = Set.of("kalmar kommun - fastighetsservice", "trafikverket");
  private final String MISSING_ID = "ID saknas";
  private final String MISSING_NAME = "Namn saknas";

  /***
   * Parses a JSON object in string representation into objects in order to extract values from it
   * which are written into XML.
   * 
   * @param json a json object in string representation
   * @return XML in string representation or null if input json was malformed
   * @throws XMLStreamException if it encounters malformatted XML
   * @throws IOException if IO cannot be closed
   */
  public String parseXML(String json) throws XMLStreamException, IOException {
    StringWriter output = new StringWriter();
    XMLOutputFactory XMLOut = XMLOutputFactory.newFactory();
    XMLStreamWriter XMLWriter = XMLOut.createXMLStreamWriter(output);

    Boolean isValidJson = isValidJson(json);

    if (!isValidJson) {
      return null;
    }

    JsonObject jsonbj = JsonParser.parseString(json).getAsJsonObject();
    JsonArray features = jsonbj.getAsJsonArray(JSON_FEATURES);

    XMLWriter.writeStartDocument("UTF-8", "1.0");
    XMLWriter.writeCharacters("\n");

    if (features == null) {

      XMLWriter.writeStartElement(XML_GEOMETRIES_ELEMENT);
      XMLWriter.writeEndElement();

    } else {

      XMLWriter.writeStartElement(XML_GEOMETRIES_ELEMENT);
      XMLWriter.writeCharacters("\n");
      for (JsonElement jsonElement : features) {

        XMLWriter.writeCharacters("\t");
        XMLWriter.writeStartElement(XML_GEOMETRY_ELEMENT);
        XMLWriter.writeCharacters("\n");

        JsonObject feature = jsonElement.getAsJsonObject();
        JsonObject attributes = feature.getAsJsonObject(ATTRIBUTES);
        JsonObject geometry = feature.getAsJsonObject(JSON_GEOMETRY);

        String objID = getAttributeAsString(attributes, JSON_OBJECTID);
        String owner = getAttributeAsString(attributes, OWNER);

        String id = getAttributeAsString(attributes, "ID");
        id = handleNullId(id);

        String X = getAttributeAsString(geometry, "x");
        String Y = getAttributeAsString(geometry, "y");
        String XY = "POINT(" + X + " " + Y + ")";
        boolean isSelectable = isSelectable(owner);

        String selectableStr = isSelectable ? "true" : "false";
        String iconURL = isSelectable ? SELECTABLE_ICONURL : NON_SELECTABLE_ICONURL;
        owner = generalizeOwnerName(owner);

        writeElement(XMLWriter, XML_OBJECTID, objID, 2);
        writeElement(XMLWriter, NAME, id, 2);
        writeElement(XMLWriter, DESCRIPTION, "Ägare: " + owner, 2);
        writeElement(XMLWriter, WKTGEOMETRY, XY, 2);
        writeElement(XMLWriter, SELECTABLE, selectableStr, 2);
        writeElement(XMLWriter, ICONURLKEY, iconURL, 2);

        XMLWriter.writeCharacters("\t");
        XMLWriter.writeEndElement();
        XMLWriter.writeCharacters("\n");
      }
      XMLWriter.writeEndElement();
    }

    XMLWriter.close();
    output.close();
    return output.toString();
  }

  /**
   * Tests if provided JSON is valid by attempting to parse it. 
   * @param json a JSON object in string representation
   * @return true if valid JSON. False if the JSON contained invalid syntax
   */
  public boolean isValidJson(String json) {
    try {
      JsonParser.parseString(json);
      return true;
    } catch (JsonSyntaxException err) {
      return false;
    }
  }

  /**
   * A wrapper for writing child elements in XML with correct indentation using an XMLStreamWriter.
   * @param XMLWriter a XMLStreamWriter object
   * @param tag a string that shall represent the element's tag
   * @param value the value inside the created element
   * @param depth the amount of indentation
   * @throws XMLStreamException if it encounters an issue with the XMLStream, perhaps it was closed prematurely
   */
  public void writeElement(XMLStreamWriter XMLWriter, String tag, String value, int depth) throws XMLStreamException {
    XMLWriter.writeCharacters("\t".repeat(depth));
    XMLWriter.writeStartElement(tag);

    if (value != null) {
      XMLWriter.writeCharacters(value);
    }

    XMLWriter.writeEndElement();
    XMLWriter.writeCharacters("\n");
  }

  String handleNullId(String id) {
    id = (id == null) ? MISSING_ID : id;

    return id;
  }

  boolean isSelectable(String name) {
    if (name.equals(null)) {
      name = MISSING_NAME;
    }

    name = name.toLowerCase();
    if (invalidNames.contains(name) || name.contains(FASTIGETSAGARE_STR)) {
      return false;
    }
    return true;
  }

  String generalizeOwnerName(String name) {
    if (name.equals(null)) {
      name = MISSING_NAME;
    }

    if (name.toLowerCase().contains("ga:")) {
      // In XML it says: 'fastighetesägare', using that to pass the tests for now.
      name = "GA (fastighetesägare i området)";
    }

    return name;
  }

  String getAttributeAsString(JsonObject object, String value) {
    String returnValue = null;
    try {
      returnValue = object.get(value).getAsString();
    } catch (UnsupportedOperationException | NullPointerException err) {
      System.err.println("Attribute '" + value + "' was null. " + err);
    }

    return returnValue;
  }
}