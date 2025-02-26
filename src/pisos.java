import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;

import static javax.management.remote.JMXConnectorFactory.connect;

public class pisos {
    public static String auxname;
    public static String auxnametabla;

    public static void menuPisos(String name,String nametable) {
        pisos.auxname=name;
        pisos.auxnametabla=nametable;
        try {
            Scanner scan= new Scanner(System.in);
            int aux;
            do {
                System.out.println("dime que deseas hacer: " +
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
            conn = aux.connect(); //abrir conexión

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

            JFrame frame = new JFrame("Listado de "+auxnametabla+"");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 400);


            // Crear modelo de la tabla
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
            JTable tabla = new JTable(modelo);
            while (rs.next()) {
                // Crear un array para almacenar los valores de la fila
                Object[] fila = new Object[columnCount];

                // Recorrer cada columna y guardar su valor en el array
                for (int i = 1; i <= columnCount; i++) {
                    fila[i - 1] = rs.getObject(i); // Guardar el valor de la columna en el array
                }
                modelo.addRow(fila);
            }
            JScrollPane scrollPane = new JScrollPane(tabla);
            frame.add(scrollPane);
            // Mostrar la ventana
            frame.setVisible(true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) aux.disconnect(conn);
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
            conn = aux.connect(); //abrir conexión
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
                if (conn != null) aux.disconnect(conn);
                if (pstmt != null) pstmt.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    public static void eliminar() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el ID de "+auxname+" que desea eliminar: ");
        int id_contrato = scanner.nextInt();
        scanner.nextLine();
        PreparedStatement pstmt = null;

        Connection conn = null;

        try {
            conn = aux.connect(); //abrir conexión
// Preparar la sentencia SQL para eliminar el empleado por ID
            String sql = "DELETE FROM "+auxnametabla+" WHERE id_contrato = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id_contrato);
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
        Statement stmt = null;
        ResultSet rs = null;
        String[] columnasaux = new String[0];
        int[] auxtipo =new  int[0];

        Connection conn = null;

        try {
            conn = aux.connect(); //abrir conexión
// Crear una declaración
            stmt = conn.createStatement();
// Ejecutar consulta SQL
            rs = stmt.executeQuery("SELECT * FROM " + auxnametabla + "");
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
            columnasaux = columnas;
            auxtipo = columnastipo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //System.out.println(Arrays.toString(columnasaux));

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el ID del "+auxname+" que desea actualizar: ");
        String id_contrato = scanner.nextLine();




        System.out.print("Ingrese el pago_mensual actualizado del inquilino: ");
        int pago_mensual = scanner.nextInt();
        PreparedStatement pstmt = null;





        try {
// Preparar la sentencia SQL para actualizar el nombre del empleado
            String sql = "UPDATE "+auxnametabla+" SET pago_mensual = ? WHERE "+columnasaux[0]+" = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, pago_mensual);
            pstmt.setString(2, id_contrato);
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
            aux.disconnect(conn);
            try {
                if (pstmt != null) pstmt.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}