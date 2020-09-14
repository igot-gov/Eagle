FROM openjdk:8
COPY recommendation-services-0.0.1-SNAPSHOT.jar /opt/
EXPOSE 3012
CMD ["java", "-XX:+PrintFlagsFinal", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-jar", "/opt/recommendation-services-0.0.1-SNAPSHOT.jar"]

