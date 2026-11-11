import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Чтение и запись списка книг в текстовый файл (UTF-8, одна книга — одна строка). */
public class BookStorage {

    private final File file;

    public BookStorage(String fileName) {
        this.file = new File(fileName);
    }

    public File getFile() {
        return file;
    }

    /**
     * Читает все книги из файла. Если файла ещё нет — возвращает пустой список.
     * Повреждённые строки пропускаются с предупреждением.
     */
    public List<Book> load() throws IOException {
        List<Book> books = new ArrayList<>();
        if (!file.exists()) {
            return books;
        }
        try (BufferedReader reader = new BufferedReader(
                new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                try {
                    books.add(Book.fromFileLine(line));
                } catch (IllegalArgumentException e) {
                    System.out.println("Предупреждение: строка " + lineNumber
                            + " пропущена (" + e.getMessage() + ").");
                }
            }
        }
        return books;
    }

    /** Добавляет одну книгу в конец файла (файл создаётся, если его нет). */
    public void append(Book book) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(file, StandardCharsets.UTF_8, true))) {
            writer.write(book.toFileLine());
            writer.newLine();
        }
    }

    /** Полностью перезаписывает файл переданным списком книг. */
    public void saveAll(List<Book> books) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(file, StandardCharsets.UTF_8, false))) {
            for (Book book : books) {
                writer.write(book.toFileLine());
                writer.newLine();
            }
        }
    }
}
