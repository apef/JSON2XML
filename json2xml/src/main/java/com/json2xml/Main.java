package com.json2xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import javax.xml.stream.*;

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
      try {
        Parser xmlParser = new Parser();
        String XML = xmlParser.parseXML(jsonStr);
        System.out.println("\n\n" + XML);
      } catch (XMLStreamException err) {
        System.err.println(err);
      }
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
}