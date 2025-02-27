import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;

import static javax.management.remote.JMXConnectorFactory.connect;

public class tablas {
    public static String auxname;
    public static String auxnametabla;
    public static String[] cabecera;
    public static int[] tipos;
    private static JFrame frame = null;
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

    public static void menuTablas(String name,String nametable) {
        tablas.auxname=name;
        tablas.auxnametabla=nametable;
        try {
            tablas.cabecera=obtenerCabecera();
            tablas.tipos=obtenerTipo();
            Scanner scan= new Scanner(System.in);
            int aux;
            do {
                System.out.println("dime que deseas hacer en "+auxnametabla+": " +
                        "\n 1 consultar datos" +
                        "\n 2 añadir "+auxname+"" +
                        "\n 3 eliminar "+auxname+"" +
                        "\n 4 actualizar "+auxname+"" +
                        "\n 0 finalizar consulta");
                aux=scan.nextInt();
                switch (aux){
                    case 1 -> consultar();
                    case 2 -> insertar();
                    case 3 -> eliminar();
                    case 4 -> actualizar();
                }
            }while (aux!=0);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void consultar() {
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;

        try {
            conn = connect(); //abrir conexión

// Crear una declaración
            stmt = conn.createStatement();
// Ejecutar consulta SQL
            rs = stmt.executeQuery("SELECT * FROM "+auxnametabla+"");
// Procesar los resultados
            ResultSetMetaData metaData = rs.getMetaData();
            // Obtener el número de columnas
            int columnCount = metaData.getColumnCount();
            // Definir los nombres de las columnas
            String[] columnas = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnas[i - 1] = metaData.getColumnName(i); // Guardar el valor de la columna en el array
            }

            DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
            JTable tabla = new JTable(modelo);
            modelo.setRowCount(0);

            while (rs.next()) {
                // Crear un array para almacenar los valores de la fila
                Object[] fila = new Object[columnCount];

                // Recorrer cada columna y guardar su valor en el array
                for (int i = 1; i <= columnCount; i++) {
                    fila[i - 1] = rs.getObject(i); // Guardar el valor de la columna en el array
                }
                modelo.addRow(fila);
            }
            if (frame == null) {
                frame = new JFrame("Listado de " + auxnametabla + "");
                frame.setSize(900, 400);
                JScrollPane scrollPane = new JScrollPane(tabla);
                frame.add(scrollPane);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            } else {
                frame.getContentPane().removeAll();
                frame.setTitle("Listado de "+ auxnametabla);
                JScrollPane scrollPane = new JScrollPane(tabla);
                frame.add(scrollPane);
                frame.revalidate();
                frame.repaint();
            }
            // Mostrar la ventana
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
    public static void insertar() {
        Statement stmt = null;
        ResultSet rs = null;
        String[] columnasaux = new String[0];
        int[] auxtipo =new  int[0];
        Connection conn = null;

        try {
            conn = connect(); //abrir conexión
// Crear una declaración
            stmt = conn.createStatement();
// Ejecutar consulta SQL
            rs = stmt.executeQuery("SELECT * FROM "+auxnametabla+"");
// Procesar los resultados
            ResultSetMetaData metaData = rs.getMetaData();
            // Obtener el número de columnas
            int columnCount = metaData.getColumnCount();
            // Definir los nombres de las columnas
            String[] columnas = new String[columnCount];
            int[] columnastipo = new int[columnCount];

            for (int i = 1; i <= columnCount; i++) {
                columnas[i - 1] = metaData.getColumnName(i); // Guardar el valor de la columna en el array
                columnastipo[i - 1] = metaData.getColumnType(i); // Guardar el valor de la columna en el array

            }
            columnasaux=columnas;
            auxtipo=columnastipo;

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
        String[] nombresCampos=new String[columnasaux.length];

        for (int i =1;i<columnasaux.length;i++){

            System.out.print("ingrese "+columnasaux[i]+": ");
            nombresCampos[i] = scanner.nextLine();

        }

        StringBuilder sql = new StringBuilder("INSERT INTO " + auxnametabla + " (");
        StringBuilder values = new StringBuilder(" VALUES (");

        for (int i = 1; i < columnasaux.length; i++) {
            sql.append(columnasaux[i]);
            values.append("?");
            if (i < columnasaux.length - 1) {
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

            for (int i = 1; i < nombresCampos.length; i++) {
                //System.out.println(auxtipo[i]); //ayuda a saber el tipo de de dato
                if(auxtipo[i]==12){
                    pstmt.setString(i, nombresCampos[i]);
                }if(auxtipo[i]==4){
                    pstmt.setInt(i, Integer.parseInt(nombresCampos[i]));
                }if(auxtipo[i]==2){
                    pstmt.setDouble(i, Double.parseDouble(nombresCampos[i]));
                }if(auxtipo[i]==91){
                    pstmt.setDate(i, Date.valueOf(nombresCampos[i]));
                }

            }

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println(""+auxname+" insertado exitosamente.");
            }
        } catch (Exception e) {
            System.out.println("Error al insertar "+auxname+": " + e.getMessage());
        } finally {
            try {
                if (conn != null) disconnect(conn);
                if (pstmt != null) pstmt.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    public static void eliminar() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el ID de "+auxname+" que desea eliminar: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        PreparedStatement pstmt = null;

        Connection conn = null;

        try {
            conn = connect(); //abrir conexión
// Preparar la sentencia SQL para eliminar el empleado por ID
            String sql = "DELETE FROM "+auxnametabla+" WHERE "+tablas.cabecera[0]+" = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
// Ejecutar el DELETE
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println(""+auxname+" eliminado exitosamente.");
            } else {
                System.out.println("No se encontró una "+auxname+" con el nombre proporcionado.");
            }
        } catch (Exception e) {
            System.out.println("Error al eliminar el "+auxname+": " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    public static void actualizar() {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el ID del "+auxname+" que desea actualizar: ");
        int id = scanner.nextInt();
        scanner.nextLine();


        System.out.println("Dime que campo quieres actualizar: ");
        for (int i = 1; i < cabecera.length; i++) {
            System.out.println(i+" "+cabecera[i]);
        }

        int select = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Ingrese el valor a cambiar: ");
        String fi = scanner.nextLine();


        PreparedStatement pstmt = null;

        Connection conn = null;

        try {
            conn = connect();
// Preparar la sentencia SQL para actualizar el nombre del empleado
            String sql = "UPDATE "+auxnametabla+" SET "+cabecera[select]+" = ? WHERE "+cabecera[0]+" = ?";
            pstmt = conn.prepareStatement(sql);

            //System.out.println(auxtipo[i]); //ayuda a saber el tipo de de dato
            if(tipos[select]==12){
                pstmt.setString(1, fi);
            }if(tipos[select]==4){
                pstmt.setInt(1, Integer.parseInt(fi));
            }if(tipos[select]==2){
                pstmt.setDouble(1, Double.parseDouble(fi));
            }if(tipos[select]==91){
                pstmt.setDate(1, Date.valueOf(fi));
            }

            pstmt.setInt(2, id);
// Ejecutar el UPDATE
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println(""+auxname+" actualizado exitosamente.");
            } else {
                System.out.println("No se encontró el "+auxname+" con el ID proporcionado.");
            }
        } catch (Exception e) {
            System.out.println("Error al actualizar el "+auxname+": " + e.getMessage());
        } finally {
            disconnect(conn);
            try {
                if (pstmt != null) pstmt.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    public static String[] obtenerCabecera(){
        Statement stmt = null;
        ResultSet rs = null;
        String[] columnasaux = new String[0];
        int[] auxtipo =new  int[0];
        Connection conn = null;

        try {
            conn = connect(); //abrir conexión
// Crear una declaración
            stmt = conn.createStatement();
// Ejecutar consulta SQL
            rs = stmt.executeQuery("SELECT * FROM "+auxnametabla+"");
// Procesar los resultados
            ResultSetMetaData metaData = rs.getMetaData();
            // Obtener el número de columnas
            int columnCount = metaData.getColumnCount();
            // Definir los nombres de las columnas
            String[] columnas = new String[columnCount];
            int[] columnastipo = new int[columnCount];

            for (int i = 1; i <= columnCount; i++) {
                columnas[i - 1] = metaData.getColumnName(i); // Guardar el valor de la columna en el array
                columnastipo[i - 1] = metaData.getColumnType(i); // Guardar el valor de la columna en el array

            }
            columnasaux=columnas;
            auxtipo=columnastipo;

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
        //System.out.println(Arrays.toString(columnasaux));
        return columnasaux;
    }
    public static int[] obtenerTipo(){
        Statement stmt = null;
        ResultSet rs = null;
        String[] columnasaux = new String[0];
        int[] auxtipo =new  int[0];
        Connection conn = null;

        try {
            conn = connect(); //abrir conexión
// Crear una declaración
            stmt = conn.createStatement();
// Ejecutar consulta SQL
            rs = stmt.executeQuery("SELECT * FROM "+auxnametabla+"");
// Procesar los resultados
            ResultSetMetaData metaData = rs.getMetaData();
            // Obtener el número de columnas
            int columnCount = metaData.getColumnCount();
            // Definir los nombres de las columnas
            String[] columnas = new String[columnCount];
            int[] columnastipo = new int[columnCount];

            for (int i = 1; i <= columnCount; i++) {
                columnas[i - 1] = metaData.getColumnName(i); // Guardar el valor de la columna en el array
                columnastipo[i - 1] = metaData.getColumnType(i); // Guardar el valor de la columna en el array

            }
            columnasaux=columnas;
            auxtipo=columnastipo;

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
        //System.out.println(Arrays.toString(columnasaux));
        return auxtipo;
    }
}