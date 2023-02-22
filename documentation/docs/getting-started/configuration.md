Есть несколько способов запустить бота-помощника. Бот был спроектирован таким образом, чтобы работать локально на вашем ПК, но вы можете запустить его на сервере в режиме 24/4.

## Создание бота в Telegram
Перед запуском необходимо создать бота в Telegram. Для этого перейдите в официального бота [@GodFather](https://t.me/BotFather) и выполните команду `/newbot`.

## Конфигурация
Несмотря на то, какой вариант запуска вы виберете, необходимо будет указать следующие переменные среды:

* `TELEGRAM_BOT_TOKEN` — токен, который вы получили при создании бота.
* `TELEGRAM_BOT_USERNAME` — название, которое вы дали боту. Оканчивается на bot.
* `GITLAB_PERSONAL_TOKEN` — токен, который вы получили в GitLab
* `TELEGRAM_PERSON_ID` — ваш id в telegram, можно узнать тут.
* `GITLAB_URL` — url на gitlab. Локальный или облачный.
* `DATASOURCE_URL` — ссылка на базу данных Postgres, в следующем формате: jdbc:postgresql://localhost:5432/gitlab_bot
* `DATASOURCE_USERNAME` — пользователь БД
* `DATASOURCE_PASSWORD` — пароль от БД

## Запуск Docker Compose

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