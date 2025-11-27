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
</div>
</body>
</html>