<!DOCTYPE html>
<html>
<head>
    <title>Информация о сотруднике: ${employee.name}</title>
</head>
<body>
<div>
    <#if appName??>
        <p><a href="/books">&lt; Назад к списку книг</a> | ${appName}</p>
    <#else>
        <p><a href="/books">&lt; Назад к списку книг</a></p>
    </#if>

    <h1>${employee.name} (ID: ${employee.id})</h1>

    <h2>Текущие выданные книги</h2>

    <#if issuedBooks?has_content>
        <table>
            <thead>
            <tr>
                <th>ISBN</th>
                <th>Название</th>
                <th>Действие</th>
            </tr>
            </thead>
            <tbody>
            <#list issuedBooks as book>
                <tr>
                    <td>${book.isbn}</td>
                    <td>${book.title}</td>
                    <td><a href="/book-info?isbn=${book.isbn}">Подробнее о книге</a></td>
                </tr>
            </#list>
            </tbody>
        </table>
    <#else>
        <p>У данного сотрудника нет выданных книг на данный момент.</p>
    </#if>
</div>
</body>
</html>