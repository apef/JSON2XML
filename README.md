# JSON2XML
A JSON to XML parser written in Java that is tailored towards a specific format. (Not a general JSON to XML converter).

#### Requirements
* Java
* JDK 11+
* Maven (optional, it is provided as a wrapper in the project)

### Download instructions
Either download the repository as a zip and unzip it at the directory you choose, or you could use your terminal with git.

If using Git, open up your terminal and move to the desired location of your choosing. (eg: cd /example/parser)

Then use the following commands.
```
git clone https://github.com/apef/JSON2XML.git
cd JSON2XML
cd json2xml
```

### Usage
*Ensure that you are within the "json2xml" folder before performing any of the following steps.*


#### Running the tests
If you already have Maven installed, then the tests can be performed using the following command in your terminal:

```
mvn test
```

However, if you instead want to use the Maven wrapper that is included in the project then use the following commands in your terminal:

```
mwnw.cmd test   (windows)
./mvnw test     (linux/MacOS)
```
#### Executing main (running the project)
In order to execute the project itself, there are a couple of alternatives.

1. Use your IDE of choice and run Main.java with the IDE
2. Execute with Maven

##### Maven
If going with the second option, then execute the following command in your terminal (replace "mvn" with the wrapper if Maven is not installed):

```
mvn -Dexec.mainClass=com.json2xml.Main exec:java
```

## Error handling
When it comes to my solution and where things could go wrong in the code, the biggest issues lie within the reading of JSON objects. For example, consider the following.

```
String getAttributeAsString(JsonObject object, String value) {
    String returnValue = null;
    try {
      returnValue = object.get(value).getAsString();
    } catch (UnsupportedOperationException | NullPointerException err) {
      System.err.println(err + " " + value);
    }

    return returnValue;
  }
```
The presented code snippet shows how I retrieve the values from the JSON objects. The issues that can happen here is "what if the value does not exist?", from trying to get a value from a key that the JSON object does not contain. In this case, the method will return null and it has to be handled from where it was called. If not handled properly, then a NullPointerException will be thrown. Here I am able to take some liberties with how to handle it, for example, depending on which value did not exist in the JSON object then perhaps ignoring that object would be one valid course of action.

What I mean by that is, if a Lamp does not contain an ObjectID (from the database, if I assume correclty), then something might've gone wrong and that lamp should be disregarded. Other values can be "optional", for example a Lamp ID (not their database ID) can be missing since it might just not be recorded yet. I currently handle this case by exchanging missing IDs with a generic "ID missing" string.

To expand on the issue with converting JSON to Objects is that I am currently assuming that the input will follow the same structure each time. This could lead to issues if the structure were to change.

The following showcases what I mean.
```
JsonObject jsonbj = JsonParser.parseString(json).getAsJsonObject();
JsonArray features = jsonbj.getAsJsonArray(JSON_FEATURES);
```

If the input were to not have any Features (the tag itself is not present in the JSON object) then that could lead to issues. Right now I have a check to see if the retrieval of the Features attribute became null, and if so, simply write an empty element in the XML. It should not crash at least.

With larger data sets (larger input JSON), the issues that can stem from this code is slower processing. As the input is read sequentially (taking out the content from each lamp, writing it into the XML, one by one), the processing time will take longer with each increase in the amount of entries. For around a couple of thousand entries, this should still not be a big issue but if the data were to become GB's in size, then that may lead to a longer wait before the XML is produced.

Since I write the XML within the loop where the attributes of each Lamp is extracted, if some exception where to occur then the XML document might not get to be written to completion. This issue has not presented itself with the amount of data I have at hands at this current time though. As long as the data stays within the format provided by the test data, then there should not be any issues down the line.
