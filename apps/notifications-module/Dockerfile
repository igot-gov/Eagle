FROM openjdk:8
COPY Notification-0.0.1-SNAPSHOT.jar /opt/
EXPOSE 5805
CMD ["java", "-XX:+PrintFlagsFinal", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-jar", "/opt/Notification-0.0.1-SNAPSHOT.jar"]

