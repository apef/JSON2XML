package com.json2xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Main {
  public static void main(String[] args) throws IOException {
    String jsonStr = null;
    try (InputStream in = Main.class.getResourceAsStream("/RAWDATA.JSON")) {
      if (in == null) {
        throw new FileNotFoundException("JSON File could not be found on the classpath");
      } else {
        jsonStr = new String(in.readAllBytes());
      }
    }

    if (jsonStr.equals(null)) {
      System.err.println("JSON file could not be found, check the filepath.");
    } else {
      String XML = parser(jsonStr);
      System.out.println("\n\n\n\n" + XML);
    }
  }

  public static boolean isValidJson(String json) {
    try {
      JsonParser.parseString(json);
      return true;
    } catch (JsonSyntaxException err) {
      return false;
    }
  }

  public static String parser(String json) {
    Boolean isValidJson = isValidJson(json);

    if (!isValidJson) {
      return null;
    }
    StringBuilder sb = new StringBuilder();

    JsonObject jsonbj = JsonParser.parseString(json).getAsJsonObject();
    JsonArray features = jsonbj.getAsJsonArray("features");

    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

    if (features == null) {
      System.out.println("Size less than one");
      sb.append("<Geometries/>");
    } else {
      sb.append("<Geometries>");
      for (JsonElement jsonElement : features) {
        sb.append("<Geometry>");
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
        sb.append(writeXMLAttribute("ObjectID", objID));
        sb.append(writeXMLAttribute("Name", id));
        sb.append(writeXMLAttribute("Description", "Ägare: " + owner));
        // sb.append(writeXMLAttribute(vaghallare, json))
        sb.append(writeXMLAttribute("WKTGeometry", XY));
        String selectable = isSelectable(owner) ? "true" : "false";
        sb.append(writeXMLAttribute("Selectable", selectable));
        sb.append(writeXMLAttribute("IconURL", "https://minasidor.testkommun.se/fileconnector/file/cesamh/456_felanmalan_belysning/trbel_normal_light_green_16px.png"));

        // <Geometry>
        //   <ObjectID>2227910</ObjectID>
        //   <Name>7910</Name>
        //   <Description>Ägare: Kalmar Energi</Description>
        //   <WKTGeometry>POINT(140767.4570000004 6287207.636700001)</WKTGeometry>
        //   <Selectable>true</Selectable>
        //   <IconURL>https://minasidor.testkommun.se/fileconnector/file/cesamh/456_felanmalan_belysning/trbel_normal_light_green_16px.png</IconURL>
        // </Geometry>

        sb.append("</Geometry>");
      }
      sb.append("</Geometries>");
    }

    return sb.toString();
  }

  private static boolean isSelectable(String name) {
    // String[] invalidNames = {"Kalmar kommun - Fastighetsservice", "Trafikverket"};
    Set<String> invalidNames = Set.of("Kalmar kommun - Fastighetsservice", "Trafikverket");
    if (!invalidNames.contains(name) && !(name.contains("GA:".toLowerCase()))) {
      return true;
    }
    return false;
  }

  private static String getAttributeAsString(JsonObject object, String value) {
    String returnValue = null;
    try {
      returnValue = object.get(value).getAsString();
    } catch (UnsupportedOperationException | NullPointerException err) {
      System.err.println(err + " " + value);
    }

    return returnValue;
  }

  public static String writeXMLAttribute(String tagName, String value) {
    // <ObjectID>2227910</ObjectID>
    return "<" + tagName + ">" + value + "</" + tagName + ">";
  }
  // <Geometries/> if none

  /**
   * POJO classes to use with Gson (templates which lets me use Javascript dot syntax)
   */
  // public class Lamp {
  //   String objectid;
  //   String id;
  //   String owner;
  //   String vaghallare;
  // } 

  // public class geometry {
  //   float X;
  //   float Y;
  // }
}