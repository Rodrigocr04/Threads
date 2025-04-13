import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        // crear la biblioteca
        LibraryThreads biblioteca = new LibraryThreads();

        // agregar libros a la biblioteca
        String[] titulos = {
            "Cien años de soledad",
            "El principito",
            "1984",
            "Don Quijote",
            "Orgullo y prejuicio",
            "Crimen y castigo",
            "El señor de los anillos"
        };
        
        String[] autores = {
            "Gabriel García Márquez",
            "Antoine de Saint-Exupéry",
            "George Orwell",
            "Miguel de Cervantes",
            "Jane Austen",
            "Fiódor Dostoyevski",
            "J.R.R. Tolkien"
        };

        for (int i = 0; i < titulos.length; i++) {
            biblioteca.addBook(new BookThreads(titulos[i], autores[i]));
        }

        // crear y registrar usuarios
        String[] nombresPatrones = {"Ana", "Carlos", "Luisa", "Pedro", "Rodrigo"};
        PatronThreads[] patrones = new PatronThreads[nombresPatrones.length];

        for (int i = 0; i < nombresPatrones.length; i++) {
            patrones[i] = new PatronThreads(nombresPatrones[i], biblioteca);
            biblioteca.addPatron(patrones[i]);
        }

        // iniciar los hilos
        System.out.println("INICIANDO SIMULACION DE BIBLIOTECA");
        System.out.println("Libros disponibles: " + biblioteca.getAvailableBooks().size());
        System.out.println("Patrones registrados: " + biblioteca.listPatrons().size());
        System.out.println("----------------------------------------");

        for (PatronThreads patron : patrones) {
            new Thread(patron).start();
        }

        // programar finalizacion automatica despues de 1 minuto
        new Thread(() -> {
            try {
                TimeUnit.MINUTES.sleep(1);
                System.out.println("\n===FINALIZANDO SIMULACION===");
                System.exit(0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}