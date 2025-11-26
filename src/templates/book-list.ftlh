<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${appName}</title>
</head>
<body>
<h1>Список книг</h1>
<table>
    <tr>
        <th>ISBN</th>
        <th>Название</th>
        <th>Статус</th>
    </tr>
    <#list books as book>
        <tr>
            <td>${book.isbn}</td>
            <td><a href="/book-info?isbn=${book.isbn}">${book.title}</a></td>
            <td>
                <#if book.issued>
                    Выдана (кому: ${book.issuedToEmployeeId!})
                <#else>
                    В наличии
                </#if>
            </td>
        </tr>
    </#list>
</table>
</body>
</html>