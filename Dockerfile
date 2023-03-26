FROM openjdk:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/clj-state-machine-0.1.0-standalone.jar /clj-state-machine/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/clj-state-machine/app.jar"]
