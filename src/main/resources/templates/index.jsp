<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login Page</title>
</head>
<body>
    <h1>Login Page</h1>
    <form action="<c:url value='/login'/>" method="post">
        <label for="username">Username:</label>
        <input type="text" id="username" name="username"/><br/>
        <label for="password">Password:</label>
        <input type="password" id="password" name="password"/><br/>
        <button type="submit">Login</button>
    </form>
</body>
</html>