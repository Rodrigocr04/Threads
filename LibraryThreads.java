import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LibraryThreads {
    private List<Book> books;
    private List<Patron> patrons;
    private HashMap<Patron, Book> checkedOutBooks; // mapa de libros prestados a los patron

    public LibraryThreads() { // constructor de la libreria
        books = new ArrayList<>();
        patrons = new ArrayList<>();
        checkedOutBooks = new HashMap<>();
    }

    // synchronized para que solo un thread pueda acceder a la funcion a la vez y evitar concurrencia
    
    public synchronized void addBook(Book book) { // agregar a la lista de libros
        books.add(book);
    }
    
    public synchronized void removeBook(Book book) { // eliminar de la lista de libros
        books.remove(book);
    }
    
    public synchronized void addPatron(Patron patron) { // agregar a la lista de patron
        patrons.add(patron);
    }
    
    public synchronized boolean checkOutBook(Patron patron, Book book, int daysToDue) {
        if (books.contains(book) && !book.isCheckedOut()) { // el libro esta en la biblioteca y no esta prestado
            book.checkOut(daysToDue); // se registra el libro como prestado
            checkedOutBooks.put(patron, book); // se registra en el hashmap
            return true;
        }
        return false;
    }
    
    public synchronized boolean returnBook(Patron patron) {
        Book book = checkedOutBooks.get(patron); // ve en el hashmap si el patron tiene un libro prestado
        if (book != null) { // si el patron tiene un libro prestado
            book.returnBook(); // se registra el libro como devuelto
            checkedOutBooks.remove(patron); // se elimina del hashmap
            return true;
        }
        return false;
    }
    
    public synchronized double calculateFine(Patron patron) {
        Book book = checkedOutBooks.get(patron); // ve en el hashmap si el patron tiene un libro prestado
        if (book != null && book.isCheckedOut()) { // si el patron tiene un libro prestado
            LocalDate today = LocalDate.now(); // fecha de hoy con LocalDate
            long daysOverdue = ChronoUnit.DAYS.between(book.getDueDate(), today); // calcula los dias de atraso

            if (daysOverdue > 0) {
                return daysOverdue * 0.50; // $0.50 por cada dia de atraso
            }
        }
        return 0.0;
    }
    
}