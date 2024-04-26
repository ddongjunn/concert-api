ARG SPRING_PROFILE=default
ARG JAR_FILE_PATH=build/libs/*.jar

FROM openjdk:17-alpine
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILE
COPY ${JAR_FILE_PATH} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}"]

#image 생성
#docker build --platform linux/x86_64 -t concert-img .

#container 실행
#docker run --rm --name concert -p 8080:8080 -e PROFILE=dev concert-img