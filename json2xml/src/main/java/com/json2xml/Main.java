package com.json2xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.*;

public class Main {
  public static void main(String[] args) throws IOException {
    String jsonPath = "/rawdata.json";
    String jsonStr = readDocument(jsonPath);

    if (jsonStr.equals(null)) {
      System.err.println("JSON file could not be found, check the filepath.");
    } else {
      try {
        Parser xmlParser = new Parser();
        String XML = xmlParser.parseXML(jsonStr);
        System.out.println(XML);
        
      } catch (XMLStreamException err) {
        System.err.println(err);
      }
    }
  }

  public static String readDocument(String path) throws FileNotFoundException, IOException {
    String returnStr = null;
    try (InputStream in = Main.class.getResourceAsStream(path)) {
      if (in == null) {
        throw new FileNotFoundException("JSON File could not be found on the classpath");
      } else {
        returnStr = new String(in.readAllBytes());
      }
    }
    return returnStr;
  } 

}