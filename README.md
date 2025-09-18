# JSON2XML
A parser to convert JSON into XML.

#### Requirements
* Java
* JDK 11+
* Maven (optional, it is provided as a wrapper in the project)


### Usage
If you already have Maven installed, then the tests can be performed using the following command in your terminal:

```
mvn test
```

However, if you instead want to use the Maven wrapper that is included in the project then use the following commands in your terminal:

```
mwnw.cmd test   (windows)
./mvnw test     (linux/MacOS)
```

In order to execute the project itself, there are a couple of alternatives.

1. Use your IDE of choice and run Main.java with the IDE
2. Execute with Maven

##### Maven
If going with the second option, then execute the following command in your terminal (replace "mvn" with the wrapper if Maven is not installed):

```
mvn -Dexec.mainClass=com.json2xml.Main exec:java
```