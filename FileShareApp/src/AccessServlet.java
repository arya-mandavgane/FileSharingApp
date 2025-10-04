import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/AccessServlet")
public class AccessServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/filesharingdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "aam2024";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        String password = request.getParameter("password");

        if (id == null || password == null || id.length() != 6) {
            request.setAttribute("error", "Invalid file ID or password.");
            request.getRequestDispatcher("access.jsp").forward(request, response);
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try(Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

                String sql = "SELECT filename, upload_time FROM files WHERE id = ? AND password = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, id);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    Timestamp uploadTime = rs.getTimestamp("upload_time");
                    long elapsed = (System.currentTimeMillis() - uploadTime.getTime()) / 1000;
                    long expirationTime = 86400; // 24 hours

                    if (elapsed >= expirationTime) {
                        PreparedStatement delPs = conn.prepareStatement("DELETE FROM files WHERE id = ?");
                        delPs.setString(1, id);
                        delPs.executeUpdate();

                        request.setAttribute("error", "File not found! Either credentials are wrong or file expired!");
                    } else {
                        long remainingSeconds = expirationTime - elapsed;
                        request.setAttribute("filename", rs.getString("filename"));
                        request.setAttribute("remainingSeconds", remainingSeconds);
                        request.setAttribute("id", id);
                    }
                } else {
                    request.setAttribute("error", "File not found! Either credentials are wrong or file expired!");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
        }

        request.getRequestDispatcher("access.jsp").forward(request, response);
    }
}