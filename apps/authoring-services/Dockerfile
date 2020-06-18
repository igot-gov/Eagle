FROM openjdk:8
COPY wingspan-authoring-services-0.0.1-SNAPSHOT.jar /opt/
EXPOSE 4011
CMD ["java", "-XX:+PrintFlagsFinal", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-jar", "/opt/wingspan-authoring-services-0.0.1-SNAPSHOT.jar"]

