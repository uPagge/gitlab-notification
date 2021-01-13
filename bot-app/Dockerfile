FROM openjdk:8-jdk-alpine
MAINTAINER uPagge
RUN addgroup -S bitbucketbot && adduser -S bitbucketbot -G bitbucketbot
USER bitbucketbot:bitbucketbot
COPY target/bitbucketbot.jar app.jar
ENTRYPOINT ["java", "-jar", "", "app.jar"]