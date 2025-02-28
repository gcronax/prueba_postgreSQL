import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Scanner;

public class tablasrefactorizado {
    public static String entityName;
    public static String tableName;
    public static String[] headers;
    public static int[] columnTypes;
    private static JFrame frame = null;
    private static final String URL = "jdbc:postgresql://89.36.214.106:5432/geo_1cfsl_3267g";
    private static final String USER = "geo_1cfsl_3267g";
    private static final String PASSWORD = "geo_1cfsl_3267g";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
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
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public static void menuTablas(String entity, String table) {
        entityName = entity;
        tableName = table;
        try {
            headers = getHeaders();
            columnTypes = getColumnTypes();
            Scanner scanner = new Scanner(System.in);
            int option;
            do {
                System.out.println("¿Qué deseas hacer en " + tableName + "?:" +
                        "\n 1. Consultar datos" +
                        "\n 2. Añadir " + entityName +
                        "\n 3. Eliminar " + entityName +
                        "\n 4. Actualizar " + entityName +
                        "\n 0. Finalizar consulta");
                option = scanner.nextInt();
                switch (option) {
                    case 1 -> queryData();
                    case 2 -> insertData();
                    case 3 -> deleteData();
                    case 4 -> updateData();
                }
            } while (option != 0);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void queryData() {
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;

        try {
            conn = connect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            String[] columns = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columns[i - 1] = metaData.getColumnName(i);
            }

            DefaultTableModel model = new DefaultTableModel(columns, 0);
            JTable table = new JTable(model);
            model.setRowCount(0);

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }
            if (frame == null) {
                frame = new JFrame("Listado de " + tableName);
                frame.setSize(900, 400);
                JScrollPane scrollPane = new JScrollPane(table);
                frame.add(scrollPane);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            } else {
                frame.getContentPane().removeAll();
                frame.setTitle("Listado de " + tableName);
                JScrollPane scrollPane = new JScrollPane(table);
                frame.add(scrollPane);
                frame.revalidate();
                frame.repaint();
            }
            frame.setVisible(true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) disconnect(conn);
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static void insertData() {
        Statement stmt = null;
        ResultSet rs = null;
        String[] columns = new String[0];
        int[] types = new int[0];
        Connection conn = null;

        try {
            conn = connect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            columns = new String[columnCount];
            types = new int[columnCount];

            for (int i = 1; i <= columnCount; i++) {
                columns[i - 1] = metaData.getColumnName(i);
                types[i - 1] = metaData.getColumnType(i);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        Scanner scanner = new Scanner(System.in);
        String[] fieldValues = new String[columns.length];

        for (int i = 1; i < columns.length; i++) {
            System.out.print("Ingrese " + columns[i] + ": ");
            fieldValues[i] = scanner.nextLine();
        }

        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
        StringBuilder values = new StringBuilder(" VALUES (");

        for (int i = 1; i < columns.length; i++) {
            sql.append(columns[i]);
            values.append("?");
            if (i < columns.length - 1) {
                sql.append(", ");
                values.append(", ");
            }
        }
        sql.append(") ");
        values.append(") ");
        sql.append(values);

        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement(sql.toString());

            for (int i = 1; i < fieldValues.length; i++) {
                if (types[i] == 12) {
                    pstmt.setString(i, fieldValues[i]);
                }
                if (types[i] == 4) {
                    pstmt.setInt(i, Integer.parseInt(fieldValues[i]));
                }
                if (types[i] == 2) {
                    pstmt.setDouble(i, Double.parseDouble(fieldValues[i]));
                }
                if (types[i] == 91) {
                    pstmt.setDate(i, Date.valueOf(fieldValues[i]));
                }
            }

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println(entityName + " insertado exitosamente.");
            }
        } catch (Exception e) {
            System.out.println("Error al insertar " + entityName + ": " + e.getMessage());
        } finally {
            try {
                if (conn != null) disconnect(conn);
                if (pstmt != null) pstmt.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static void deleteData() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el ID de " + entityName + " que desea eliminar: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        PreparedStatement pstmt = null;
        Connection conn = null;

        try {
            conn = connect();
            String sql = "DELETE FROM " + tableName + " WHERE " + headers[0] + " = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println(entityName + " eliminado exitosamente.");
            } else {
                System.out.println("No se encontró una " + entityName + " con el ID proporcionado.");
            }
        } catch (Exception e) {
            System.out.println("Error al eliminar el " + entityName + ": " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static void updateData() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el ID del " + entityName + " que desea actualizar: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.println("¿Qué campo quieres actualizar?");
        for (int i = 1; i < headers.length; i++) {
            System.out.println(i + " " + headers[i]);
        }

        int selection = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Ingrese el valor a cambiar: ");
        String newValue = scanner.nextLine();

        PreparedStatement pstmt = null;
        Connection conn = null;

        try {
            conn = connect();
            String sql = "UPDATE " + tableName + " SET " + headers[selection] + " = ? WHERE " + headers[0] + " = ?";
            pstmt = conn.prepareStatement(sql);

            if (columnTypes[selection] == 12) {
                pstmt.setString(1, newValue);
            }
            if (columnTypes[selection] == 4) {
                pstmt.setInt(1, Integer.parseInt(newValue));
            }
            if (columnTypes[selection] == 2) {
                pstmt.setDouble(1, Double.parseDouble(newValue));
            }
            if (columnTypes[selection] == 91) {
                pstmt.setDate(1, Date.valueOf(newValue));
            }

            pstmt.setInt(2, id);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println(entityName + " actualizado exitosamente.");
            } else {
                System.out.println("No se encontró el " + entityName + " con el ID proporcionado.");
            }
        } catch (Exception e) {
            System.out.println("Error al actualizar el " + entityName + ": " + e.getMessage());
        } finally {
            disconnect(conn);
            try {
                if (pstmt != null) pstmt.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static String[] getHeaders() {
        Statement stmt = null;
        ResultSet rs = null;
        String[] columns = new String[0];
        Connection conn = null;

        try {
            conn = connect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            columns = new String[columnCount];

            for (int i = 1; i <= columnCount; i++) {
                columns[i - 1] = metaData.getColumnName(i);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) disconnect(conn);
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        return columns;
    }

    public static int[] getColumnTypes() {
        Statement stmt = null;
        ResultSet rs = null;
        int[] types = new int[0];
        Connection conn = null;

        try {
            conn = connect();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            types = new int[columnCount];

            for (int i = 1; i <= columnCount; i++) {
                types[i - 1] = metaData.getColumnType(i);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) disconnect(conn);
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        return types;
    }
}