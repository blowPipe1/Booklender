<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Регистрация</title>
</head>
<body>
<h1>Регистрация нового сотрудника</h1>
<#if message??>
    <p style=" font-weight: bold;">${message}</p>
</#if>
<form action="/register" method="POST">
    <label for="email">Идентификатор (Email):</label><br>
    <input type="email" id="email" name="email" required><br><br>
    <label for="name">Имя пользователя:</label><br>
    <input type="text" id="name" name="name" required><br><br>
    <label for="password">Пароль:</label><br>
    <input type="password" id="password" name="password" required><br><br>
    <button type="submit">Зарегистрироваться</button>
</form>
<p><a href="/login">Уже зарегистрированы? Войти.</a></p>
</body>
</html>