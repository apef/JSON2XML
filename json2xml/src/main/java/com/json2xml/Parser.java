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

  private final String iconURL = "https://minasidor.testkommun.se/fileconnector/file/cesamh/456_felanmalan_belysning/trbel_normal_light_green_16px.png";
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

    XMLWriter.writeStartDocument("utf-8", "1.0");

    if (features == null) {
      System.out.println("Size less than one");
      XMLWriter.writeEmptyElement(XML_GEOMETRIES_ELEMENT);

    } else {

      XMLWriter.writeStartElement(XML_GEOMETRIES_ELEMENT);

      for (JsonElement jsonElement : features) {
        XMLWriter.writeStartElement(XML_GEOMETRY_ELEMENT);
        JsonObject feature = jsonElement.getAsJsonObject();
        JsonObject attributes = feature.getAsJsonObject(ATTRIBUTES);
        JsonObject geometry = feature.getAsJsonObject(JSON_GEOMETRY);

        String objID = getAttributeAsString(attributes, JSON_OBJECTID);
        String owner = getAttributeAsString(attributes, OWNER);
        //String vaghallare = getAttributeAsString(attributes, "Vaghallare"); // Unused in output?

        String id = getAttributeAsString(attributes, "ID");
        id = (id == null) ? "ID saknas" : id;

        String X = getAttributeAsString(geometry, "x");
        String Y = getAttributeAsString(geometry, "y");
        String XY = "POINT(" + X + " " + Y + ")";
        String isSelectable = isSelectable(owner) ? "true" : "false";

        writeElement(XMLWriter, XML_OBJECTID, objID);
        writeElement(XMLWriter, NAME, id);
        writeElement(XMLWriter, DESCRIPTION, "Ägare: " + owner);
        writeElement(XMLWriter, WKTGEOMETRY, XY);
        writeElement(XMLWriter, SELECTABLE, isSelectable);
        writeElement(XMLWriter, ICONURLKEY, iconURL);
        
        XMLWriter.writeEndElement();
      }
      XMLWriter.writeEndElement();
    }

    return output.toString();
  }

  private void writeElement(XMLStreamWriter XMLWriter, String tag, String value) throws XMLStreamException {
    XMLWriter.writeStartElement(tag);
    if (value != null) {
      XMLWriter.writeCharacters(value);
    }
    XMLWriter.writeEndElement();
  }

  private boolean isSelectable(String name) {
    Set<String> invalidNames = Set.of("Kalmar kommun - Fastighetsservice", "Trafikverket");
    if (!invalidNames.contains(name) && !(name.contains("GA:".toLowerCase()))) {
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
