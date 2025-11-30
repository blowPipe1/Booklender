<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Профиль пользователя</title>
</head>
<body>
<div>
    <h1>Информация о пользователе</h1>

    <p>
        <strong>Имя:</strong>
        <span>${userName!""}</span>
    </p>

    <p>
        <strong>Email (Идентификатор):</strong>
        <span>${userEmail!""}</span>
    </p>

    <p><a href="/issue-book">Взять книжечку</a></p>
    <#if userBooks?size == 0>
        <p>У вас нет выданных книг.</p>
    <#else>
        <p><a href="/return-book">Вернуть книжечку</a></p>
    </#if>
</div>
</body>
</html>