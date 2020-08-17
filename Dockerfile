FROM openjdk:8-alpine

ENV USER=homework

COPY build/shadow/homework.jar /app/homework.jar

RUN addgroup -g 1000 -S $USER \
    && adduser -u 1000 \
               -S $USER \
               -G $USER \
               -H -h /app \
               -s /bin/bash \
    && chown -R $USER:$USER /app

WORKDIR /app

HEALTHCHECK --interval=5s --retries=10 CMD nc -z 127.0.0.1 8383

USER $USER
CMD ["java", "-jar", "homework.jar"]

EXPOSE 8383
