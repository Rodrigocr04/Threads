import java.time.LocalDate;

public class BookThreads { // clase que representa un libro
    private String title;
    private String author; 
    private boolean isCheckedOut;
    private LocalDate dueDate;

    public BookThreads(String title, String author) { // crea una nueva instancia de libro
        this.title = title;
        this.author = author;
        this.isCheckedOut = false; // por defecto no esta prestado
        this.dueDate = null; // no tiene fecha de vencimiento inicial
    }

    public synchronized String getTitle() {
        return title;
    }

    public synchronized String getAuthor() {
        return author;
    }

    public synchronized boolean isCheckedOut() {
        return isCheckedOut;
    }

    public synchronized LocalDate getDueDate() { 
        return dueDate;
    }

    public synchronized void checkOut(int daysToDue) { // prestar el libro
        if (isCheckedOut) {
            throw new IllegalStateException("El libro ya está prestado");
        }
        isCheckedOut = true;
        dueDate = LocalDate.now().plusDays(daysToDue); // calcular la fecha de vencimiento
    }

    public synchronized void returnBook() { // devolver el libro a la biblioteca
        if (!isCheckedOut) {
            throw new IllegalStateException("El libro no estaba prestado");
        }
        isCheckedOut = false;
        dueDate = null;
    }

    public synchronized void setDueDate(LocalDate dueDate) { // modificar la fecha de devolucion
        if (!isCheckedOut) {
            throw new IllegalStateException("No se puede cambiar fecha: libro no prestado");
        }
        if (dueDate == null) {
            throw new IllegalArgumentException("Fecha no puede ser nula");
        }
        this.dueDate = dueDate;
    }

    public synchronized boolean isOverdue() { // verificar si el prestamo esta vencido
        return isCheckedOut && dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    @Override
    public synchronized String toString() { // imprimir la info del libro
        return "Libro{" +
               "título='" + title + '\'' +
               ", autor='" + author + '\'' +
               ", prestado=" + isCheckedOut +
               ", fechaDevolución=" + dueDate +
               '}';
    }
}