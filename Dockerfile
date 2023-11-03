FROM amazoncorretto:17-alpine3.18

WORKDIR /opt/api
COPY . .

RUN ./gradlew build

ENTRYPOINT ["java", "-Xms512M", "-Xmx2048", "-jar", "/opt/api/build/libs/project-api-all.jar"]
