FROM openjdk:17-alpine
WORKDIR /app

ARG JAR_FILE_PATH=build/libs/*.jar
ARG SPRING_PROFILE=dev

COPY ${JAR_FILE_PATH} app.jar
RUN ls -al /app

ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILE
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "app.jar"]

#image 생성
#docker build --platform linux/x86_64 -t concert-img .

#container 실행
#docker run --rm --name concert -p 8080:8080 -e PROFILE=dev concert-img