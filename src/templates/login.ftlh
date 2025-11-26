<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Вход в систему</title>
</head>
<body>
<h1>Вход в систему</h1>

<#if errorMessage??>
    <p style="color: red; font-weight: bold;">${errorMessage}</p>
</#if>

<form action="/login" method="POST">
    <label for="email">Идентификатор (Email):</label><br>
    <input type="email" id="email" name="email" required><br><br>
    <label for="password">Пароль:</label><br>
    <input type="password" id="password" name="password" required><br><br>
    <button type="submit">Войти</button>
</form>
<p><a href="/register">Зарегистрироваться</a></p>
</body>
</html>