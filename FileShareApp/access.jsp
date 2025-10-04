<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Access File</title>
    <style>
        body { font-family: Arial; margin: 40px; }
        label, input, button { display: block; margin-top: 15px; font-size: 16px; }
        input, button { padding: 8px; }
        .error { color: red; margin-top: 20px; }
        #timer { font-weight: bold; color: green; margin-top: 10px; }
    </style>
    <script>
        let expirationSeconds;
        let timerInterval;

        function startTimer(seconds) {
            expirationSeconds = seconds;
            timerInterval = setInterval(() => {
                if (expirationSeconds <= 0) {
                    clearInterval(timerInterval);
                    alert("File expired or deleted.");
                    location.reload();
                    return;
                }
                let min = Math.floor(expirationSeconds / 60);
                let sec = expirationSeconds % 60;
                document.getElementById('timer').innerText = Expires in: ${min}m ${sec}s;
                expirationSeconds--;
            }, 1000);
        }

        function deleteFile(id) {
            if (!confirm("Are you sure you want to delete this file?")) return;
            fetch('DeleteServlet?id=' + id, {method: 'POST'})
                .then(response => response.text())
                .then(data => {
                    alert(data);
                    location.reload();
                });
        }
    </script>
</head>
<body>
<h2>Access File</h2>
<form action="AccessServlet" method="post">
    <label>File ID (6 digits):</label>
    <input type="text" name="id" maxlength="6" pattern="[0-9]{6}" required />

    <label>Password:</label>
    <input type="password" name="password" required />

    <button type="submit">Access</button>
</form>

<div>
    <p style="color:red;"><%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %></p>

    <%
        String filename = (String)request.getAttribute("filename");
        String id = (String)request.getAttribute("id");
        Long remainingSecondsObj = (Long)request.getAttribute("remainingSeconds");
    %>

    <% if (filename != null && id != null && remainingSecondsObj != null) { %>
        <p>File ready for download: <b><%= filename %></b></p>
        <p id="timer">Expires in: loading...</p>
        <button onclick="deleteFile('<%= id %>')">Delete File Now</button>
        <a href="DownloadServlet?id=<%= id %>&password=<%= request.getParameter("password") %>">Download File</a>

        <script>startTimer(<%= remainingSecondsObj %>);</script>
    <% } %>
</div>
</body>
</html>