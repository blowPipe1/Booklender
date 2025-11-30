<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Возврат книги</title>
</head>
<body>
<h1>Возврат книги</h1>

<#if userBooks?size == 0>
    <p>У вас нет выданных книг.</p>
<#else>
    <p>Выберите книгу для возврата:</p>
    <form action="/return-book" method="post">
        <label for="isbn_select">Выберите книгу:</label>
        <select name="isbn" id="isbn_select">
            <#list userBooks as book>
                <option value="${book.isbn}">${book.title} (ISBN: ${book.isbn})</option>
            </#list>
        </select>
        <br><br>
        <input type="submit" value="Вернуть выбранную книгу">
    </form>
</#if>
<p><a href="/profile">Назад в профиль</a></p>
</body>
</html>