FROM adoptopenjdk/openjdk11:alpine
ADD /build/libs/CoopMeeting-1.0.jar CoopMeeting-1.0.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "CoopMeeting-1.0.jar"]