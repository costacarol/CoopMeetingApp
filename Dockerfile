FROM adoptopenjdk/openjdk11:alpine
ADD /build/libs/CoopMeeting-1.jar CoopMeeting-1.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "CoopMeeting-1.jar"]