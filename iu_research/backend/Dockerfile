FROM maven:3.8.2-jdk-8

WORKDIR /app
COPY . .
RUN mvn clean install
CMD mvn spring-boot:run

##### Stage 1: Build the application
#FROM openjdk:8-jdk-alpine as build
#
## Set the current working directory inside the image
#WORKDIR /app
#
## Copy maven executable to the image
#COPY mvnw .
#COPY .mvn .mvn
#
## Copy the pom.xml file
#COPY pom.xml .
#
## Build all the dependencies in preparation to go offline.
## This is a separate step so the dependencies will be cached unless
## the pom.xml file has changed.
#RUN ./mvnw dependency:go-offline -B
#
## Copy the project source
#COPY src src
#
## Package the application
#RUN ./mvnw package -DskipTests
#RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
#
##### Stage 2: A minimal docker image with command to run the app
#FROM openjdk:8-jre-alpine
#
#ARG DEPENDENCY=/app/target/dependency
#
## Copy project dependencies from the build stage
#COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
#COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
#COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
#
##ARG JAR_FILE=target/*.jar
##COPY ${JAR_FILE} app.jar
##ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar","/app.jar"]
#
##ENTRYPOINT ["java","-cp","app:app/lib/*","com.haidv.lab.ecommerce.EcommerceApplication"]