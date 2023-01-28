# Уведомления GitLab в Telegram

Этот проект позволяет оперативно получать и настраивать персональные уведомления о событиях из GitLab.

> Статья о проекте: https://struchkov.dev/blog/gitlab-telegram-bot/  
> Канал в Telegram, в который публикуется информация о разработке: https://t.me/gitlab_notification
>
> ⚠️На данный момент вся разработка ведется в моем Gitea: https://git.struchkov.dev/Telegram-Bots/gitlab-notification
> GitHub остается для удобства пользователей проекта, чтобы иметь возможность давать обратную связь по багам и
> предложениям.
>
> ⚠️Идет активная подготовка к выпуску версии 2.0.0, в котором будут исправлены все существующие баги, а также добавлена
> поддержка уведомлений Issue.
> На данный момент лучше использовать тег develop, вместо latest.

## Основные возможности

1. Уведомление о новых Merge Request.
2. Уведомление о возникновении конфликта в MergeRequest.
3. Уведомление о появлении нового проекта.
4. Уведомление о смене статуса вашего MergeRequest.
5. Уведомление о комментариях, в которых вас упоминают в формате @nickname.
6. Уведомит о новом комментарии-треде в вашем MR.
7. Уведомит о закрытии вашего треда в чужом MR.
8. Уведомление о результате сборки.

## Как запустить

1. Для начала нужно создать бота, который будет посылать вам уведомления. Делается это
   в [специальном боте](https://t.me/botfather)
2. После создания вы получите токен, сохраните его. Пример: `34534050345:FlfrleflerferfRE-ergerFLREF9ERF-NGjM`
3. Теперь необходимо получить персональный токен в вашем gitlab. Достаточно токена на чтение.
4. Можно приступать к запуску используя один из способов ниже.

### Переменные среды

* `TELEGRAM_BOT_TOKEN` -- токен, который вы получили при создание бота.
* `TELEGRAM_BOT_USERNAME` -- название, которое вы дали боту. Пример my_gitlab_bot.
* `GITLAB_PERSONAL_TOKEN` -- ваш персональный токен из GitLab.
* `TELEGRAM_PERSON_ID` -- ваш id в телеграм, можно узнать у [этого бота](https://t.me/myidbot)
* `GITLAB_URL` -- можно указать https://gitlab.com или url на ваш локальный/корпоративный GitLab строго в таком
  формате http://localhost:7990.
* `DATASOURCE_URL` -- ссылка на базу данных Postgres, в следующем формате: jdbc:postgresql://localhost:5432/gitlab_bot
* `DATASOURCE_USERNAME` -- пользовать базы данных
* `DATASOURCE_PASSWORD` -- пароль пользователя базы данных

### Запуск

Есть несколько способов запуска. Для удобства я собрал проект в Docker образ, а также подготовил Docker Compose.

#### Docker

Подойдет, если вы не хотите поднимать отдельный контейнер с базой данных

```
sudo docker run --name gitlab-notify  \ 
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

#### Docker Compose

Самый простой способ запуска.

docker-compose.yml
```yaml
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
    privileged: true
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

.env
```
TELEGRAM_BOT_TOKEN=
TELEGRAM_BOT_USERNAME=
GITLAB_PERSONAL_TOKEN=
TELEGRAM_PERSON_ID=
GITLAB_URL=
DATASOURCE_USERNAME=
DATASOURCE_PASSWORD=
```

После запуска необходимо отправить боту сообщение, чтобы пройти первичную настройку.
