package com.json2xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import javax.xml.stream.*;

public class Main {
  public static void main(String[] args) throws IOException {
    String jsonPath = "/RAWDATA.JSON";
    // String jsonPath = "/Null_ID_removed.JSON";
    String expectedXMLpath = "/ExpectedXML.XML";
    String jsonStr = readDocument(jsonPath);
    String expXmlString = readDocument(expectedXMLpath);
    
    // System.out.println("---------EXPECTED----------\n\n" + expXmlString + "--------------\n\n\n\n\n");
    if (jsonStr.equals(null)) {
      System.err.println("JSON file could not be found, check the filepath.");
    } else {
      try {
        Parser xmlParser = new Parser();
        String XML = xmlParser.parseXML(jsonStr);

        // System.out.println((XML == expXmlString));
        // System.out.println("\n\n" + XML);

        // PrintWriter pw = new PrintWriter(new FileOutputStream(new File("./output.xml")));
        // pw.append(XML);
        // pw.flush();
        // pw.close();

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