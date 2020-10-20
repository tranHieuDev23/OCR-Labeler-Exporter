FROM maven:3.6.3-jdk-11 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn clean dependency:go-offline
COPY src/ /build/src/
RUN mvn package

FROM jetty:9.4.32-jre11-slim
USER root
RUN mkdir -p /vol
COPY --from=build /build/target/ocrlabeler.war /var/lib/jetty/webapps/ROOT.war
EXPOSE 8080
CMD ["sh", "-c", "java -jar $JETTY_HOME/start.jar"]