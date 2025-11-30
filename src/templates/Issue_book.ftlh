<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Выдача книги</title>
</head>
<body>
<h1>Выдача книги сотруднику: ${userEmail}</h1>

<#if availableBooks?size == 0>
    <p>На данный момент свободных книг нет.</p>
<#else>
    <p>Выберите книгу для выдачи:</p>
    <form action="/issue-book" method="post">
        <label for="isbn_select">Выберите книгу:</label>
        <select name="isbn" id="isbn_select">
            <#list availableBooks as book>
                <option value="${book.isbn}">${book.title} (ISBN: ${book.isbn})</option>
            </#list>
        </select>
        <br><br>
        <input type="submit" value="Выдать выбранную книгу">
    </form>
</#if>

<p><a href="/profile">Назад в профиль</a></p>
<p><a href="/books">Список книг</a></p>
</body>
</html>