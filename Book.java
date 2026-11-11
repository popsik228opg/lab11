import java.time.Year;

/**
 * Книга: название, автор, год издания, жанр.
 * В файле хранится одной строкой: Название;Автор;Год;Жанр
 * Разделитель ';' внутри полей запрещён.
 */
public class Book {

    public static final String SEPARATOR = ";";

    private final String title;
    private final String author;
    private final int year;
    private final String genre;

    /** Создаёт книгу; при некорректных данных бросает IllegalArgumentException. */
    public Book(String title, String author, int year, String genre) {
        this.title = checkField("Название", title);
        this.author = checkField("Автор", author);
        this.genre = checkField("Жанр", genre);
        int currentYear = Year.now().getValue();
        if (year < 1 || year > currentYear) {
            throw new IllegalArgumentException(
                    "Год издания должен быть от 1 до " + currentYear + ".");
        }
        this.year = year;
    }

    private static String checkField(String name, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Поле «" + name + "» не может быть пустым.");
        }
        if (value.contains(SEPARATOR)) {
            throw new IllegalArgumentException(
                    "Поле «" + name + "» не должно содержать символ «;».");
        }
        if (value.contains("\n") || value.contains("\r")) {
            throw new IllegalArgumentException(
                    "Поле «" + name + "» не должно содержать переводы строки.");
        }
        return value.trim();
    }

    /** Разбирает строку файла вида «Название;Автор;Год;Жанр». */
    public static Book fromFileLine(String line) {
        String[] parts = line.split(SEPARATOR, -1);
        if (parts.length != 4) {
            throw new IllegalArgumentException(
                    "ожидалось 4 поля через «;», найдено " + parts.length);
        }
        int year;
        try {
            year = Integer.parseInt(parts[2].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("год «" + parts[2] + "» не является числом");
        }
        return new Book(parts[0], parts[1], year, parts[3]);
    }

    /** Строка для записи в файл. */
    public String toFileLine() {
        return String.join(SEPARATOR, title, author, String.valueOf(year), genre);
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    @Override
    public String toString() {
        return "«" + title + "» — " + author + ", " + year + ", жанр: " + genre;
    }
}
