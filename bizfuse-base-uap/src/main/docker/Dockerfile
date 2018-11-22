FROM java:8

ADD uap-1.0.0.0-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'

EXPOSE 9999/tcp

VOLUME /tmp
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
