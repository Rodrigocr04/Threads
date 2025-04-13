import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PatronThreads implements Runnable { // representa un usuario
    private final String name; // nombre
    private final List<BookThreads> checkedOutBooks; // libros actualmente prestados
    private final LibraryThreads library; // referencia a la biblioteca
    private final Random random; // generador de numeros random
    private final int patronId; // ID
    private static int nextId = 1; // contador para IDs

    public PatronThreads(String name, LibraryThreads library) { // constructor
        this.name = name;
        this.library = library;
        this.checkedOutBooks = new ArrayList<>();
        this.random = new Random();
        this.patronId = nextId++; // asigna ID y incrementa contador
    }

    @Override
    public void run() { // metodo principal del hilo
        try {
            while (true) {
                performRandomAction(); // se realiza una accion random
                // delay entre acciones
                TimeUnit.MILLISECONDS.sleep(1000 + random.nextInt(2000));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(name + " (Usuario #" + patronId + ") detuvo sus actividades.");
        }
    }

    private void performRandomAction() { // decide random que accion realizar
        if (checkedOutBooks.isEmpty() || random.nextBoolean()) {
            borrowRandomBook(); // preferencia por pedir libros si no tiene
        } else {
            returnRandomBook(); // si ya tiene libros puede devolver
        }
    }

    public synchronized boolean borrowRandomBook() { // tomar un libro random disponible
        List<BookThreads> availableBooks = library.getAvailableBooks();
        if (!availableBooks.isEmpty()) {
            BookThreads book = availableBooks.get(random.nextInt(availableBooks.size()));
            return borrowBook(book);
        }
        return false;
    }

    public synchronized boolean borrowBook(BookThreads book) { // prestar un libro
        if (library.checkOutBook(this, book, 14)) {
            checkedOutBooks.add(book);
            System.out.println(name + " tomó prestado: " + book.getTitle());
            return true;
        }
        return false;
    }

    public synchronized boolean returnRandomBook() { // devuelve un libro random
        if (!checkedOutBooks.isEmpty()) {
            BookThreads book = checkedOutBooks.get(random.nextInt(checkedOutBooks.size()));
            return returnBook(book);
        }
        return false;
    }

    public synchronized boolean returnBook(BookThreads book) { // devuelve un libro
        if (library.returnBook(this, book)) {
            checkedOutBooks.remove(book);
            double fine = library.calculateFine(this);
            if (fine > 0) {
                System.out.println(name + " devolvió: " + book.getTitle() + " con multa: $" + fine);
            } else {
                System.out.println(name + " devolvió: " + book.getTitle());
            }
            return true;
        }
        return false;
    }

    public synchronized String getName() { // obtiene nombre del usuario
        return name;
    }

    public synchronized List<BookThreads> getCheckedOutBooks() { // obtiene copia de libros prestados
        return new ArrayList<>(checkedOutBooks);
    }

    public synchronized boolean hasCheckedOutBook(BookThreads book) { // verifica si tiene un libro
        return checkedOutBooks.contains(book);
    }

    public int getPatronId() { // obtiene ID del usuario
        return patronId;
    }
}