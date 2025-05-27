FROM maven:3-amazoncorretto-17 as develop-stage-oauth

WORKDIR /app

COPY /config/ /resources/

COPY /api/gestsuite-oauth .
RUN mvn clean package -f pom.xml
ENTRYPOINT ["mvn","spring-boot:run","-f","pom.xml"]

FROM maven:3-amazoncorretto-17 as build-stage-oauth

WORKDIR /resources

COPY /api/gestsuite-oauth .
RUN mvn clean package -f pom.xml

FROM maven:3-amazoncorretto-17 as production-stage-oauth

COPY --from=build-stage-oauth /resources/target/oauth-0.0.1-SNAPSHOT.jar oauth.jar
COPY /config/ /resources/
ENTRYPOINT ["java","-jar","/oauth.jar"]