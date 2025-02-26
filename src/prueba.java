import java.sql.*;

public class prueba {
    private static final String URL =
            "jdbc:postgresql://89.36.214.106:5432/geo_1cfsl_3267g";
    private static final String USER = "geo_1cfsl_3267g";
    private static final String PASSWORD = "geo_1cfsl_3267g";
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión establecida con la Base de datos");
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos");
            e.printStackTrace();
        }
        return conn;
    }
    public static void disconnect(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("Conexión cerrada");
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public static void consultar() {
        String sql = "SELECT codi, nom FROM instituts";
        Connection conn = null;
        try {
            conn = connect(); //abrir conexión
            System.out.println("Consultando institutos");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                System.out.println(rs.getString(1) + " | " + rs.getString(2) );
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            disconnect(conn); //cerrar conexión
        }
    }

    public static void main(String[] args) {
        consultar();
    }
}