import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Лабораторное занятие №11. Часть 1: базовая работа с файлами.
 * Выполнил: Бубнов Александр, группа ПИ2-1.
 */
public class FileDemo {

    public static void main(String[] args) {
        Path path = Paths.get("data", "example.txt");
        System.out.println("Рабочий файл: " + path.toAbsolutePath());

        try {
            // Шаг 1. Создаём каталог (если его нет) и записываем несколько строк.
            // writeString без опций заменяет прежнее содержимое файла.
            Files.createDirectories(path.getParent());
            String text = "Первая строка" + System.lineSeparator()
                    + "Вторая строка" + System.lineSeparator()
                    + "Третья строка" + System.lineSeparator();
            Files.writeString(path, text, StandardCharsets.UTF_8);
            System.out.println("\n[1] Файл создан, текст записан.");

            // Шаг 2. Читаем весь файл одной строкой.
            String content = Files.readString(path, StandardCharsets.UTF_8);
            System.out.println("\n[2] Содержимое файла:");
            System.out.print(content);

            // Шаг 3. Дописываем строки в конец (CREATE + APPEND),
            // символ перевода строки добавляем явно.
            String more = "Четвёртая строка" + System.lineSeparator()
                    + "Пятая строка" + System.lineSeparator();
            Files.writeString(path, more, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("\n[3] Новые строки добавлены в конец файла.");

            // Шаг 4. Читаем построчно через BufferedReader (try-with-resources).
            // readLine() возвращает null, когда файл закончился.
            System.out.println("\n[4] Построчное чтение:");
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                String line;
                int number = 1;
                while ((line = reader.readLine()) != null) {
                    System.out.println(number + ": " + line);
                    number++;
                }
            }

            // Дополнительно: тот же результат списком строк.
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            System.out.println("\nВсего строк в файле: " + lines.size()
                    + ", размер: " + Files.size(path) + " байт");

        } catch (IOException e) {
            System.out.println("Ошибка при работе с файлом: " + e.getMessage());
        }
    }
}
