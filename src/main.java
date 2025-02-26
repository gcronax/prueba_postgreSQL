import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        Scanner scan= new Scanner(System.in);

        int aux;
        do {
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
        }while (aux!=0);
    }
}
