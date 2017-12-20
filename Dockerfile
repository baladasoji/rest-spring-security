FROM openjdk:8-jdk-alpine
COPY target/*.jar /app.jar
ENTRYPOINT ["/usr/bin/java", "-jar", "/app.jar"]
# Add Maven dependencies (not shaded into the artifact; Docker-cached)
#ADD target/lib           /usr/share/myservice/lib
# Add the service itself
