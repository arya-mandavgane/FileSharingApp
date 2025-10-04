import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/DownloadServlet")
public class DownloadServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/filesharingdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "aam2024";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String id = request.getParameter("id");
        String password = request.getParameter("password");

        if (id == null || password == null || id.length() != 6) {
            response.getWriter().print("Invalid parameters.");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try(Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "SELECT filename, filecontent, upload_time FROM files WHERE id = ? AND password = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, id);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    Timestamp uploadTime = rs.getTimestamp("upload_time");
                    long elapsed = (System.currentTimeMillis() - uploadTime.getTime()) / 1000;
                    long expirationTime = 86400;

                    if (elapsed >= expirationTime) {
                        PreparedStatement delPs = conn.prepareStatement("DELETE FROM files WHERE id = ?");
                        delPs.setString(1, id);
                        delPs.executeUpdate();

                        response.getWriter().print("File expired and deleted.");
                    } else {
                        String filename = rs.getString("filename");
                        InputStream fileContent = rs.getBinaryStream("filecontent");

                        response.setContentType("application/octet-stream");
                        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

                        OutputStream out = response.getOutputStream();
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fileContent.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                        fileContent.close();
                        out.close();
                    }
                } else {
                    response.getWriter().print("File not found! Credentials may be wrong.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print("Error: " + e.getMessage());
        }
    }
}