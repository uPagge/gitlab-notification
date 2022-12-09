FROM eclipse-temurin:17 as app-build
ENV RELEASE=17

WORKDIR /opt/build
COPY ./gitlab-app/target/gitlab-notification.jar ./application.jar

RUN java -Djarmode=layertools -jar application.jar extract
RUN $JAVA_HOME/bin/jlink \
         --add-modules `jdeps --ignore-missing-deps -q -recursive --multi-release ${RELEASE} --print-module-deps -cp 'dependencies/BOOT-INF/lib/*' application.jar`,jdk.crypto.cryptoki \
         --strip-java-debug-attributes \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output jdk

FROM debian:buster-slim

ARG BUILD_PATH=/opt/build
ENV JAVA_HOME=/opt/jdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
ENV TELEGRAM_PERSON_ID=TELEGRAM_PERSON_ID DATASOURCE_URL=DATASOURCE_URL \
    DATASOURCE_PASSWORD=DATASOURCE_PASSWORD DATASOURCE_USERNAME=DATASOURCE_USERNAME \
    GITLAB_PERSONAL_TOKEN=GITLAB_PERSONAL_TOKEN TELEGRAM_BOT_TOKEN=TELEGRAM_BOT_TOKEN \
    TELEGRAM_BOT_USERNAME=TELEGRAM_BOT_USERNAME GITLAB_URL=GITLAB_URL GITLAB_REPLACE_URL=GITLAB_REPLACE_URL

RUN groupadd --gid 1000 spring-app \
  && useradd --uid 1000 --gid spring-app --shell /bin/bash --create-home spring-app

USER spring-app:spring-app
WORKDIR /opt/workspace

COPY --from=app-build $BUILD_PATH/jdk $JAVA_HOME
COPY --from=app-build $BUILD_PATH/spring-boot-loader/ ./
COPY --from=app-build $BUILD_PATH/dependencies/ ./
COPY --from=app-build $BUILD_PATH/application/ ./

ENTRYPOINT java -Dfile.encoding=UTF8 -Dconsole.encoding=UTF8 -DTELEGRAM_BOT_USERNAME=$TELEGRAM_BOT_USERNAME -DTELEGRAM_BOT_TOKEN=$TELEGRAM_BOT_TOKEN -DTELEGRAM_PERSON_ID=$TELEGRAM_PERSON_ID -DDATASOURCE_URL=$DATASOURCE_URL -DDATASOURCE_PASSWORD=$DATASOURCE_PASSWORD -DDATASOURCE_USERNAME=$DATASOURCE_USERNAME -DGITLAB_PERSONAL_TOKEN=$GITLAB_PERSONAL_TOKEN -DGITLAB_URL=$GITLAB_URL -DGITLAB_REPLACE_URL=$GITLAB_REPLACE_URL org.springframework.boot.loader.JarLauncher