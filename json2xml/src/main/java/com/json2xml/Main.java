package com.json2xml;

import java.io.File;
import java.io.FileNotFoundException;
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
      File XML = parser(jsonStr);
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

  public static File parser(String json) {
    Boolean isValidJson = isValidJson(json);

    if (!isValidJson) {
      return null;
    }

    JsonObject jsonbj = JsonParser.parseString(json).getAsJsonObject();
    JsonArray features = jsonbj.getAsJsonArray("features");

    for (JsonElement jsonElement : features) {
      // System.out.println(jsonElement.getClass());

      JsonObject feature = jsonElement.getAsJsonObject();
      JsonObject attributes = feature.getAsJsonObject("attributes");
      // Lamp check = gson.fromJson(attributes.)
      JsonObject geometry = feature.getAsJsonObject("geometry");
      // System.out.println("ATT: " + attributes + "\n GEO: " + geometry);

      String objID = attributes.get("OBJECTID").getAsString();
      String id = attributes.get("ID").getAsString();
      String owner = attributes.get("Owner").getAsString();
      String vaghallare = attributes.get("Vaghallare").getAsString();
      
      System.out.println(objID + " " + id + " " + owner + " " + vaghallare);
    }

    return null;
  }

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