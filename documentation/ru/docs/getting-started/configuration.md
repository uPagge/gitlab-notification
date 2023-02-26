# Первый запуск ассистента

Есть несколько способов запустить бота-помощника. Бот был спроектирован таким образом, чтобы работать локально на вашем ПК, но вы можете запустить его на сервере в режиме 24/4.

Первым делом вам предложат ввести имя для бота.

## Конфигурация
Несмотря на то, какой вариант запуска вы вберете, необходимо указать следующие переменные среды:

* `TELEGRAM_BOT_TOKEN` — токен, который вы получили при [создании бота](creating-telegram-bot.md).
* `TELEGRAM_BOT_USERNAME` — название, которое вы дали боту. Оканчивается на bot.
* `GITLAB_PERSONAL_TOKEN` — токен, который вы [получили в GitLab.](create-gitlab-token.md)
* `TELEGRAM_PERSON_ID` — ваш идентификатор в telegram, можно узнать в боте [@myidbot](@myidbot).
* `GITLAB_URL` — url на GitLab. Локальный или облачный.
* `DATASOURCE_URL` — ссылка на базу данных Postgres, в следующем формате: `jdbc:postgresql://databasehost:5432/gitlab_bot`
* `DATASOURCE_USERNAME` — пользователь БД
* `DATASOURCE_PASSWORD` — пароль от БД

## Запуск Docker Compose

Самый простой способ запустить ассистента, - это docker compose. Создайте файлы `docker-compose.yml` и `.env`. Не забудьте в `.env` указать все необходимые для запуска переменные.

=== ":simple-docker: docker-compose.yml"

    ``` yaml
    version: '3.8'

    services:
    
    gitlab-bot-database:
        image: postgres:15.1-alpine
        restart: always
        hostname: gitlab-bot-database
        container_name: gitlab-bot-database
        networks:
            gitlab-bot:
        environment:
            POSTGRES_DB: "gitlab_bot"
            POSTGRES_USER: "postgres"
            POSTGRES_PASSWORD: ${DATASOURCE_PASSWORD}
        volumes:
            - gitlab-bot-database:/var/lib/postgresql/data/
    
    gitlab-bot:
        image: upagge/gitlab-telegram-notify:latest
        hostname: gitlab-bot
        container_name: gitlab-bot
        networks:
            gitlab-bot:
        depends_on:
            - gitlab-bot-database
        environment:
            TELEGRAM_BOT_TOKEN: ${TELEGRAM_BOT_TOKEN}
            TELEGRAM_BOT_USERNAME: ${TELEGRAM_BOT_USERNAME}
            GITLAB_PERSONAL_TOKEN: ${GITLAB_PERSONAL_TOKEN}
            TELEGRAM_PERSON_ID: ${TELEGRAM_PERSON_ID}
            GITLAB_URL: ${GITLAB_URL}
            DATASOURCE_URL: "jdbc:postgresql://gitlab-bot-database:5432/gitlab_bot"
            DATASOURCE_USERNAME: ${DATASOURCE_USERNAME}
            DATASOURCE_PASSWORD: ${DATASOURCE_PASSWORD}
    
    volumes:
        gitlab-bot-database:
    
    networks:
        gitlab-bot:
    ```

=== ":octicons-file-16: .env"

    ``` properties
    TELEGRAM_BOT_TOKEN=
    TELEGRAM_BOT_USERNAME=
    GITLAB_PERSONAL_TOKEN=
    TELEGRAM_PERSON_ID=
    GITLAB_URL=
    DATASOURCE_USERNAME=
    DATASOURCE_PASSWORD=
    ```

Теперь запустите композ:

``` shell
docker compose up -d
```

## Запуск Docker
Команда для запуска выглядит следующим образом:

``` docker 
docker run --name gitlab-notify  \
    --env TELEGRAM_BOT_TOKEN=value \
    --env TELEGRAM_BOT_USERNAME=value \
    --env GITLAB_PERSONAL_TOKEN=value \
    --env TELEGRAM_PERSON_ID=value \
    --env GITLAB_URL=value \
    --env DATASOURCE_URL=jdbc:postgresql://localhost:5432/gitlab_bot \
    --env DATASOURCE_USERNAME=postgres \
    --env DATASOURCE_PASSWORD=value \
    --network="host" upagge/gitlab-telegram-notify:latest
```

## Запуск в IDEA

## Запуск JAR релиза

Скачать актуальный jar-файл всегда можно на странице релизов GitHub.

``` shell
java -DTELEGRAM_BOT_USERNAME=value \
                    -DTELEGRAM_BOT_TOKEN=value \
                    -DTELEGRAM_PERSON_ID=value \
                    -DDATASOURCE_URL=value \
                    -DDATASOURCE_PASSWORD=value \
                    -DDATASOURCE_USERNAME=value \
                    -DGITLAB_PERSONAL_TOKEN=value \
                    -DGITLAB_URL=value \
                    -jar gitlab-notification.jar    
```