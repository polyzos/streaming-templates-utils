FROM openjdk:17-jdk-slim

ADD build/libs/kafka-patterns-0.1.0.jar app.jar
ADD credentials .
RUN #apk update && apk add --no-cache gcompat

RUN mkdir -p /tmp
RUN chmod 777 /tmp
COPY credentials/ /tmp

ENTRYPOINT ["java","-cp","/app.jar", "io.ipolyzos.offsets."]