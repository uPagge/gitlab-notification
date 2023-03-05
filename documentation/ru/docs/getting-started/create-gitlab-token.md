# Создание токена GitLab

Для взаимодействия с GitLab необходим персональный токен доступа.

Чтобы его получить перейдите по адресу: [https://gitlab.com/-/profile/personal_access_tokens](https://gitlab.com/-/profile/personal_access_tokens)

!!! tip "Корпоративный GitLab"

    Замените `https://gitlab.com/` на адрес своего GitLab, если вы используете self-host решение.

* Придумайте название токену, например, `GitLab Notify`. 
* Выдайте права на чтение - `read_api`

!!! info "Уровень разрешений"

    Выберете уровень разрешения `api`, если планируете пользоваться такими функциями, как: [ответ в треде](../features/interaction-gitlab.md#ответ-в-треде)

* Нажмите кнопку `Create personal access token`.
* Сохраните полученный токен.