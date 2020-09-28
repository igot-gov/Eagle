FROM openjdk:8
COPY hub-services-0.0.1-SNAPSHOT.jar /opt/
EXPOSE 3013
CMD ["java", "-XX:+PrintFlagsFinal", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-jar", "/opt/hub-services-0.0.1-SNAPSHOT.jar"]

