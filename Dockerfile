FROM openjdk:8-jdk-alpine
RUN apk add --no-cache bash
COPY target/*.jar /app.jar
ENTRYPOINT ["/usr/bin/java", "-jar", "/app.jar"]
# Add Maven dependencies (not shaded into the artifact; Docker-cached)
#ADD target/lib           /usr/share/myservice/lib
# Add the service itself
