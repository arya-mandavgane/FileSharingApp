<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Upload File</title>
    <style>
        body { font-family: Arial; margin: 40px; }
        label, input, button { display: block; margin-top: 10px; font-size: 16px; }
        input, button { padding: 8px; }
        .message { color: green; margin-top: 20px; }
        .error { color: red; margin-top: 20px; }
    </style>
</head>
<body>
<h2>Upload File</h2>
<form action="UploadServlet" method="post" enctype="multipart/form-data">
    <label>Password:</label>
    <input type="password" name="password" required />

    <label>Choose File:</label>
    <input type="file" name="file" required />

    <button type="submit">Upload</button>
</form>

<div>
    <p style="color:green;"><%= request.getAttribute("message") != null ? request.getAttribute("message") : "" %></p>
    <p style="color:red;"><%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %></p>
</div>
</body>
</html>
