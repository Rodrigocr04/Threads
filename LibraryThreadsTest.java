import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class LibraryThreadsTests {
    
    @Test
    void testMultiplePatronsBorrowSameBook() throws InterruptedException {
        // configuracion inicial
        LibraryThreads library = new LibraryThreads();
        BookThreads popularBook = new BookThreads("El Principito", "Antoine de Saint-Exupéry");
        library.addBook(popularBook);
        
        PatronThreads patron1 = new PatronThreads("Ana", library);
        PatronThreads patron2 = new PatronThreads("Carlos", library);
        PatronThreads patron3 = new PatronThreads("Luisa", library);
        
        // sincronizar los hilos
        CountDownLatch latch = new CountDownLatch(3);
        
        // pool de hilos
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        // salida del sistema
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        try {
            // ejecucion de los hilos
            executor.execute(() -> { patron1.borrowBook(popularBook); latch.countDown(); });
            executor.execute(() -> { patron2.borrowBook(popularBook); latch.countDown(); });
            executor.execute(() -> { patron3.borrowBook(popularBook); latch.countDown(); });
            
            // espera a que todos los hilos terminen
            latch.await(2, TimeUnit.SECONDS);
            
            // verificaciones
            String output = outputStream.toString();
            int successCount = countOccurrences(output, "tomó prestado: El Principito");
            
            assertEquals(1, successCount, "Solo un usuario debería haber obtenido el libro");
            assertTrue(popularBook.isCheckedOut(), "El libro debería estar prestado");
            
            List<BookThreads> availableBooks = library.getAvailableBooks();
            assertFalse(availableBooks.contains(popularBook), "El libro no debería estar disponible");
            
        } finally {
            executor.shutdown();
            System.setOut(originalOut);
        }
    }
    
    @Test
    void testRandomBookReturns() throws InterruptedException {
        // configuracion inicial
        LibraryThreads library = new LibraryThreads();
        BookThreads book1 = new BookThreads("1984", "George Orwell");
        BookThreads book2 = new BookThreads("Fahrenheit 451", "Ray Bradbury");
        BookThreads book3 = new BookThreads("Un mundo feliz", "Aldous Huxley");
        
        library.addBook(book1);
        library.addBook(book2);
        library.addBook(book3);
        
        PatronThreads patron1 = new PatronThreads("María", library);
        PatronThreads patron2 = new PatronThreads("Pedro", library);
        
        // prestamos iniciales
        patron1.borrowBook(book1);
        patron1.borrowBook(book2);
        patron2.borrowBook(book3);
        
        // fechas de vencimiento para probar multas
        book1.setDueDate(LocalDate.now().minusDays(3));
        book2.setDueDate(LocalDate.now().plusDays(5)); 
        book3.setDueDate(LocalDate.now().minusDays(1));
        
        // salida del sistema
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        
        try {
            // devoluciones en hilos separados
            Thread t1 = new Thread(() -> patron1.returnRandomBook());
            Thread t2 = new Thread(() -> patron2.returnRandomBook());
            Thread t3 = new Thread(() -> patron1.returnRandomBook());
            
            t1.start();
            t2.start();
            t3.start();
            
            t1.join(1000);
            t2.join(1000);
            t3.join(1000);
            
            // verificaciones
            String output = outputStream.toString();
            
            assertTrue(output.contains("devolvió"), "Debería haber mensajes de devolución");
            
            assertTrue(output.contains("multa"), "Debería haber al menos una multa");
            
            assertTrue(book1.isCheckedOut() || book2.isCheckedOut() || book3.isCheckedOut(), 
                      "Al menos un libro debería estar prestado aún");
            
        } finally {
            System.setOut(originalOut);
        }
    }
}