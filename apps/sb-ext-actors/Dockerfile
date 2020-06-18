FROM openjdk:8
COPY bodhi-1.0-SNAPSHOT.jar /opt/
EXPOSE 5902
CMD ["java", "-XX:+PrintFlagsFinal", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-jar", "/opt/bodhi-1.0-SNAPSHOT.jar"]

