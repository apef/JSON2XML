package com.json2xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import com.google.gson.Gson;

public class Main {
  public static void main(String[] args) throws IOException {
    String jsonStr = null;
    try (InputStream in = Main.class.getResourceAsStream("/RAWDATA.JSON"))  {
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

  public static File parser(String json) {
    return null;
  }
}