import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Random;

@WebServlet("/UploadServlet")
@MultipartConfig(maxFileSize = 1024 * 1024 * 50) // 50MB max upload size
public class UploadServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/filesharingdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "aam2024";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String password = request.getParameter("password");
        Part filePart = request.getPart("file");

        if (filePart == null || password == null || password.isEmpty()) {
            request.setAttribute("error", "Password and file are required.");
            request.getRequestDispatcher("upload.jsp").forward(request, response);
            return;
        }

        String fileName = filePart.getSubmittedFileName();
        InputStream fileContent = filePart.getInputStream();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try(Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String uniqueId = generateUniqueId(conn);

                String sql = "INSERT INTO files (id, password, filename, filecontent) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, uniqueId);
                ps.setString(2, password);
                ps.setString(3, fileName);
                ps.setBlob(4, fileContent);
                ps.executeUpdate();

                request.setAttribute("message", "Upload successful! Your file ID is: " + uniqueId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
        }

        request.getRequestDispatcher("upload.jsp").forward(request, response);
    }

    private String generateUniqueId(Connection conn) throws SQLException {
        Random rnd = new Random();
        String id;
        PreparedStatement ps;
        ResultSet rs;
        do {
            int num = 100000 + rnd.nextInt(900000); // 6-digit
            id = Integer.toString(num);
            ps = conn.prepareStatement("SELECT COUNT(*) FROM files WHERE id = ?");
            ps.setString(1, id);
            rs = ps.executeQuery();
            rs.next();
        } while (rs.getInt(1) != 0);
        return id;
    }
}