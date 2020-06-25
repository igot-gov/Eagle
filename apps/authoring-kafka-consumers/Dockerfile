FROM openjdk:8
COPY authoring-kafka-consumers-0.0.1-SNAPSHOT.jar /opt/
CMD ["java", "-XX:+PrintFlagsFinal", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-jar", "/opt/authoring-kafka-consumers-0.0.1-SNAPSHOT.jar"]

