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

  public String parseXML(String json) throws XMLStreamException {
    StringWriter output = new StringWriter();
    XMLOutputFactory XMLOut = XMLOutputFactory.newFactory();
    XMLStreamWriter XMLWriter = XMLOut.createXMLStreamWriter(output);

    Boolean isValidJson = isValidJson(json);

    if (!isValidJson) {
      return null;
    }

    JsonObject jsonbj = JsonParser.parseString(json).getAsJsonObject();
    JsonArray features = jsonbj.getAsJsonArray("features");

    XMLWriter.writeStartDocument("utf-8", "1.0");

    if (features == null) {
      System.out.println("Size less than one");
      XMLWriter.writeEmptyElement("Geometries");

    } else {

      XMLWriter.writeStartElement("Geometries");

      for (JsonElement jsonElement : features) {
        XMLWriter.writeStartElement("Geometry");
        JsonObject feature = jsonElement.getAsJsonObject();
        JsonObject attributes = feature.getAsJsonObject("attributes");
        JsonObject geometry = feature.getAsJsonObject("geometry");

        String objID = getAttributeAsString(attributes, "OBJECTID");
        String owner = getAttributeAsString(attributes, "Owner");
        String vaghallare = getAttributeAsString(attributes, "Vaghallare");

        String id = getAttributeAsString(attributes, "ID");
        id = (id == null) ? "ID saknas" : id;

        String X = getAttributeAsString(geometry, "x");
        String Y = getAttributeAsString(geometry, "y");
        String XY = "POINT(" + X + " " + Y + ")";
        String selectable = isSelectable(owner) ? "true" : "false";

        XMLWriter.writeAttribute("ObjectID", objID);
        XMLWriter.writeAttribute("Name", id);
        XMLWriter.writeAttribute("Description", "Ägare: " + owner);
        XMLWriter.writeAttribute("WKTGeometry", XY);
        XMLWriter.writeAttribute("Selectable", selectable);
        XMLWriter.writeAttribute("IconURL",
            "https://minasidor.testkommun.se/fileconnector/file/cesamh/456_felanmalan_belysning/trbel_normal_light_green_16px.png");
        XMLWriter.writeEndElement();
      }
      XMLWriter.writeEndElement();
    }

    return output.toString();
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
