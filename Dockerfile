ARG JAR_FILE_PATH=build/libs/*.jar
ARG SPRING_PROFILES_ACTIVE=default

ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILE

FROM openjdk:17-alpine
WORKDIR /app

COPY ${JAR_FILE_PATH} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

#image 생성
#docker build --platform linux/x86_64 -t concert-img .

#container 실행
#docker run --rm --name concert -p 8080:8080 -e PROFILE=dev concert-img