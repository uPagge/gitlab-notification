FROM openjdk:8-jdk-alpine
MAINTAINER uPagge <me@upagge.ru>
RUN apk add --no-cache bash && \
    addgroup -S gitlabbot && \
    adduser -S gitlabbot -G gitlabbot && \
    mkdir -p /bot && \
    chown -R gitlabbot:gitlabbot /bot
WORKDIR /bot
USER gitlabbot:gitlabbot
COPY target/gitlab-notification.jar app.jar
VOLUME logs
ENV TELEGRAM_PERSON_ID=TELEGRAM_PERSON_ID DATASOURCE_URL=DATASOURCE_URL \
    DATASOURCE_PASSWORD=DATASOURCE_PASSWORD DATASOURCE_USERNAME=DATASOURCE_USERNAME \
    GITLAB_PERSONAL_TOKEN=GITLAB_PERSONAL_TOKEN TELEGRAM_BOT_TOKEN=TELEGRAM_BOT_TOKEN \
    TELEGRAM_BOT_USERNAME=TELEGRAM_BOT_USERNAME GITLAB_URL=GITLAB_URL
ENTRYPOINT java -DTELEGRAM_BOT_USERNAME=${TELEGRAM_BOT_USERNAME} \
                    -DTELEGRAM_BOT_TOKEN=$TELEGRAM_BOT_TOKEN \
                    -DTELEGRAM_PERSON_ID=$TELEGRAM_PERSON_ID \
                    -DDATASOURCE_URL=$DATASOURCE_URL \
                    -DDATASOURCE_PASSWORD=$DATASOURCE_PASSWORD \
                    -DDATASOURCE_USERNAME=$DATASOURCE_USERNAME \
                    -DGITLAB_PERSONAL_TOKEN=$GITLAB_PERSONAL_TOKEN \
                    -DGITLAB_URL=$GITLAB_URL \
                    -jar app.jar