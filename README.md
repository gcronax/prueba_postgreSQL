importante 

1. Ve a File > Project Structure
2. En el menú lateral, selecciona Libraries y haz clic en + (para añadir una).
3. Selecciona From Maven e introduce: org.postgresql:postgresql:42.7.1
4. Haz clic en OK y espera a que se descargue el driver.

para que el programa funcione bien es necesario poner bien el nombre de la tabla en   --->   tablas.menutablas(aqui nombre que sale en los sout, aqui nombre de la tabla si o si exacto)

 System.out.println("dime que deseas hacer: " +
                    "\n 1 Gestion pisos" +
                    "\n 2 Gestion propietarios" +
                    "\n 3 Gestion inquilinos" +
                    "\n 0 Finalizar programa");

            aux=scan.nextInt();

            switch (aux){
                case 1 -> tablas.menuTablas("piso","pisos");
                case 2 -> tablas.menuTablas("propietario","propietarios");
                case 3 -> tablas.menuTablas("inquilino","inquilinos");
            }

  y a partir de la linea 15 en la clase tablas modificar segun convenga,
  puedes modificar a tu gusto la url el user y el passwd si quieres conectarte a tu base de datos.


  private static final String URL =
            "jdbc:postgresql://89.36.214.106:5432/geo_1cfsl_3267g";
    private static final String USER = "geo_1cfsl_3267g";
    private static final String PASSWORD = "geo_1cfsl_3267g";

    
