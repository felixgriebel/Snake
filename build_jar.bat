javac -d out src/java/*.java
jar cfm GameApplication.jar META-INF/MANIFEST.MF -C out .

java -jar GameApplication.jar