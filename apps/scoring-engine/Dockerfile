FROM openjdk:8
COPY scoring-engine-0.0.1-SNAPSHOT.jar /opt/
EXPOSE 4011
CMD ["java", "-XX:+PrintFlagsFinal", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-jar", "/opt/scoring-engine-0.0.1-SNAPSHOT.jar"]

