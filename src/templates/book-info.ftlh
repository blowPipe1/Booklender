<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Информация о книге</title>
</head>
<body>
<h1>Книга: ${book.title}</h1>

<#if book.imageUrl?? && book.imageUrl != "">
    <div style="max-width: 300px; overflow: hidden; margin-bottom: 7px;">
        <img src="${book.imageUrl}" alt="Обложка книги ${book.title}" style="max-width: 300px; max-height: 200px; object-fit: cover;">
    </div>

</#if>

<p>ISBN: ${book.isbn}</p>

<h2>Описание</h2>
<div style=" max-width: 200px; margin-bottom: 3px; margin-top: 3px; border: 2px black; padding: 2px;">
    ${book.description! "Описание отсутствует."}
</div>

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