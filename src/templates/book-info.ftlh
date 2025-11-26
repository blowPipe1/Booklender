<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Информация о книге</title>
</head>
<body>
<h1>Книга: ${book.title}</h1>
<p>ISBN: ${book.isbn}</p>
<p>Статус:
    <#if book.issued>
        Выдана
    <#else>
        В наличии
    </#if>
</p>
<a href="/books">Вернуться к списку</a>
</body>
</html>