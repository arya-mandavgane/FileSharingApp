import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/DeleteServlet")
public class DeleteServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/filesharingdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "aam2024";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String id = request.getParameter("id");

        if (id == null || id.length() != 6) {
            response.getWriter().print("Invalid file ID.");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try(Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM files WHERE id = ?");
                ps.setString(1, id);
                int rows = ps.executeUpdate();

                if (rows > 0) {
                    response.getWriter().print("File deleted successfully.");
                } else {
                    response.getWriter().print("File not found or already deleted.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print("Error: " + e.getMessage());
        }
    }
}