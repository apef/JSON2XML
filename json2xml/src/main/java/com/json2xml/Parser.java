package com.json2xml;

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

  public String parseXML(String json) throws XMLStreamException {
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
      System.out.println("Size less than one");
      XMLWriter.writeEmptyElement(XML_GEOMETRIES_ELEMENT);
      XMLWriter.writeCharacters("\n");

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
        String vaghallare = getAttributeAsString(attributes, "Vaghallare"); // Unused in output?

        String id = getAttributeAsString(attributes, "ID");
        id = (id == null) ? "ID saknas" : id;

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

    return output.toString();
  }

  private String generalizeOwnerName(String name) {
    if (name.toLowerCase().contains("ga:")) {
      // In XML it says: 'fastighetesägare', using that to pass the tests for now.
      name = "GA (fastighetesägare i området)";
    }

    return name;
  }

  private void writeElement(XMLStreamWriter XMLWriter, String tag, String value, int depth) throws XMLStreamException {
    XMLWriter.writeCharacters("\t".repeat(depth));
    XMLWriter.writeStartElement(tag);
    if (value != null) {
      XMLWriter.writeCharacters(value);
    }
    XMLWriter.writeEndElement();
    XMLWriter.writeCharacters("\n");
  }

  private boolean isSelectable(String name) {
    Set<String> invalidNames = Set.of("Kalmar kommun - Fastighetsservice", "Trafikverket");
    if (!invalidNames.contains(name) && !(name.toLowerCase().contains("GA:".toLowerCase()))) {
      return true;
    }
    return false;
  }

  private String getAttributeAsString(JsonObject object, String value) {
    String returnValue = null;
    try {
      returnValue = object.get(value).getAsString();
    } catch (UnsupportedOperationException | NullPointerException err) {
      System.err.println(err + " " + value);
    }

    return returnValue;
  }

  private boolean isValidJson(String json) {
    try {
      JsonParser.parseString(json);
      return true;
    } catch (JsonSyntaxException err) {
      return false;
    }
  }
}