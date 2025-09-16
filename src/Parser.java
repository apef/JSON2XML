import java.io.File;
import java.io.IOException;

public class Parser {
  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println("Usage: Input JSON path, Expected XML path");
    }


    String JSONpath = args[0];
    String XMLpath = args[1];
    File rawJSON = new File(JSONpath);
    // File expectedXML = new File(XMLpath); // Temporary, if needed.

    if (!rawJSON.isFile()) {
      System.out.println("The provided JSON filepath did not resolve to a file, check the filepath.");
      throw new IOException("JSON file not found");
    } else {
      //parse(rawJSON) Expected output (file/object?)
    }
  }
}
