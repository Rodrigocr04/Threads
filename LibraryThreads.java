import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LibraryThreads { // sistema de biblioteca
    private final List<BookThreads> books; // lista de todos los libros
    private final List<PatronThreads> patrons; // lista de usuarios registrados
    private final HashMap<PatronThreads, BookThreads> checkedOutBooks; // relacion de prestamos

    public LibraryThreads() { // inicializar una nueva biblioteca
        books = new ArrayList<>();
        patrons = new ArrayList<>();
        checkedOutBooks = new HashMap<>();
    }

    public synchronized void addBook(BookThreads book) { // añadir un libro
        books.add(book);
    }
    
    public synchronized void removeBook(BookThreads book) { // eliminar un libro
        books.remove(book);
    }
    
    public synchronized void addPatron(PatronThreads patron) { // registrar un nuevo usuario
        patrons.add(patron);
    }
    
    public synchronized boolean checkOutBook(PatronThreads patron, BookThreads book, int daysToDue) {
        if (books.contains(book) && !book.isCheckedOut()) {
            book.checkOut(daysToDue);
            checkedOutBooks.put(patron, book); // registrar el prestamo
            return true;
        }
        return false;
    }
    
    public synchronized boolean returnBook(PatronThreads patron, BookThreads book) {
        // proceso de devolucion de libro
        BookThreads checkedOutBook = checkedOutBooks.get(patron);
        if (checkedOutBook != null && checkedOutBook.equals(book)) {
            book.returnBook();
            checkedOutBooks.remove(patron); // eliminar registro de prestamo
            return true;
        }
        return false;
    }
    
    public synchronized double calculateFine(PatronThreads patron) {
        // calcula multa por retraso $0.50 por dia
        BookThreads book = checkedOutBooks.get(patron);
        if (book != null && book.isCheckedOut()) {
            long díasRetraso = ChronoUnit.DAYS.between(book.getDueDate(), LocalDate.now());
            return díasRetraso > 0 ? díasRetraso * 0.50 : 0.0;
        }
        return 0.0;
    }

    public synchronized List<BookThreads> getAvailableBooks() {
        // obtener lista de libros disponibles
        List<BookThreads> disponibles = new ArrayList<>();
        for (BookThreads book : books) {
            if (!book.isCheckedOut()) {
                disponibles.add(book);
            }
        }
        return disponibles;
    }

    public synchronized List<PatronThreads> listPatrons() {
        // obtener copia de la lista de usuarios
        return new ArrayList<>(patrons);
    }
}