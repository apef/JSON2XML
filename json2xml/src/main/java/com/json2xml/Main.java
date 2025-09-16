package com.json2xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
      for (JsonElement jsonElement : features) {
        JsonObject feature = jsonElement.getAsJsonObject();
        JsonObject attributes = feature.getAsJsonObject("attributes");
        JsonObject geometry = feature.getAsJsonObject("geometry");
  
        String objID = getAttributeAsString(attributes, "OBJECTID");
        String owner = getAttributeAsString(attributes, "Owner");
        String vaghallare = getAttributeAsString(attributes, "Vaghallare");
        
        String id = getAttributeAsString(attributes, "ID");
        id = (id == null) ? "ID Saknas" : id;
  
        String X = getAttributeAsString(geometry, "x");
        String Y = getAttributeAsString(geometry, "y");
        
      }
    }

    return sb.toString();
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