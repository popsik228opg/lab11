import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Лабораторное занятие №11. Часть 2: консольное приложение «Библиотека книг».
 * Данные хранятся в файле library.txt в формате «Название;Автор;Год;Жанр».
 * Выполнил: Бубнов Александр, группа ПИ2-1.
 */
public class LibraryApp {

    private static final String FILE_NAME = "library.txt";

    private final BookStorage storage = new BookStorage(FILE_NAME);
    private final BufferedReader console =
            new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    public static void main(String[] args) {
        new LibraryApp().run();
    }

    private void run() {
        System.out.println("=== Библиотека книг ===");
        try {
            while (true) {
                printMenu();
                String choice = readLine("Ваш выбор: ");
                if (choice == null) {            // конец ввода (Ctrl+D / Ctrl+Z)
                    break;
                }
                switch (choice.trim()) {
                    case "1" -> addBook();
                    case "2" -> showBooks();
                    case "3" -> deleteBook();
                    case "4" -> searchByAuthor();
                    case "0" -> {
                        System.out.println("До свидания!");
                        return;
                    }
                    default -> System.out.println("Неизвестный пункт меню, введите число от 0 до 4.");
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка ввода-вывода: " + e.getMessage());
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. Добавить книгу");
        System.out.println("2. Показать все книги");
        System.out.println("3. Удалить книгу по названию");
        System.out.println("4. Поиск книг по автору");
        System.out.println("0. Выход");
    }

    /** Выводит приглашение и читает строку; null — если ввод закончился. */
    private String readLine(String prompt) throws IOException {
        System.out.print(prompt);
        return console.readLine();
    }

    // ---------- 1. Добавление ----------
    private void addBook() throws IOException {
        String title = readLine("Название: ");
        String author = readLine("Автор: ");
        String yearText = readLine("Год издания: ");
        String genre = readLine("Жанр: ");
        if (title == null || author == null || yearText == null || genre == null) {
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearText.trim());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: год должен быть целым числом. Книга не добавлена.");
            return;
        }

        try {
            Book book = new Book(title, author, year, genre);
            storage.append(book);
            System.out.println("Книга добавлена: " + book);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage() + " Книга не добавлена.");
        } catch (IOException e) {
            System.out.println("Не удалось записать файл: " + e.getMessage());
        }
    }

    // ---------- 2. Просмотр ----------
    private void showBooks() {
        List<Book> books = loadBooks();
        if (books == null) {
            return;
        }
        if (books.isEmpty()) {
            System.out.println("Список книг пуст.");
            return;
        }
        System.out.println("Список книг (" + books.size() + "):");
        printNumbered(books);
    }

    // ---------- 3. Удаление ----------
    private void deleteBook() throws IOException {
        String title = readLine("Введите название книги для удаления: ");
        if (title == null) {
            return;
        }
        title = title.trim();
        if (title.isEmpty()) {
            System.out.println("Название не может быть пустым.");
            return;
        }

        List<Book> books = loadBooks();
        if (books == null) {
            return;
        }

        // Выбираем все точные совпадения названия, остальные оставляем.
        List<Book> matches = new ArrayList<>();
        List<Book> rest = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().equals(title)) {
                matches.add(book);
            } else {
                rest.add(book);
            }
        }

        if (matches.isEmpty()) {
            System.out.println("Книга с названием «" + title + "» не найдена.");
            return;
        }

        System.out.println("Найдено совпадений: " + matches.size());
        printNumbered(matches);
        String answer = readLine("Удалить все найденные записи? (д/н): ");
        if (answer == null || !isYes(answer)) {
            System.out.println("Удаление отменено.");
            return;
        }

        try {
            storage.saveAll(rest);
            System.out.println("Удалено записей: " + matches.size());
        } catch (IOException e) {
            System.out.println("Не удалось записать файл: " + e.getMessage());
        }
    }

    // ---------- 4. Поиск по автору (самостоятельная работа) ----------
    private void searchByAuthor() throws IOException {
        String query = readLine("Введите автора (или часть имени): ");
        if (query == null) {
            return;
        }
        query = query.trim();
        if (query.isEmpty()) {
            System.out.println("Запрос не может быть пустым.");
            return;
        }

        List<Book> books = loadBooks();
        if (books == null) {
            return;
        }

        // Сравнение без учёта регистра.
        String needle = query.toLowerCase(Locale.ROOT);
        List<Book> found = new ArrayList<>();
        for (Book book : books) {
            if (book.getAuthor().toLowerCase(Locale.ROOT).contains(needle)) {
                found.add(book);
            }
        }

        if (found.isEmpty()) {
            System.out.println("Книги автора «" + query + "» не найдены.");
        } else {
            System.out.println("Найдено книг: " + found.size());
            printNumbered(found);
        }
    }

    // ---------- вспомогательные методы ----------
    /** Загружает книги; при ошибке доступа сообщает пользователю и возвращает null. */
    private List<Book> loadBooks() {
        try {
            return storage.load();
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл " + storage.getFile().getName()
                    + ": " + e.getMessage());
            return null;
        }
    }

    private void printNumbered(List<Book> books) {
        for (int i = 0; i < books.size(); i++) {
            System.out.println((i + 1) + ". " + books.get(i));
        }
    }

    private boolean isYes(String answer) {
        String a = answer.trim().toLowerCase(Locale.ROOT);
        return a.equals("д") || a.equals("да") || a.equals("y") || a.equals("yes");
    }
}
