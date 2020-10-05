FROM openjdk:8
COPY bodhi-1.0-SNAPSHOT.jar /opt/
#HEALTHCHECK --interval=30s --timeout=30s CMD curl --fail http://localhost:7001/actuator/health || exit 1
CMD ["java", "-XX:+PrintFlagsFinal", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-jar", "/opt/bodhi-1.0-SNAPSHOT.jar"]

