import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;  

public class Main {
    public static void main(String[] args) {
        try {
            Path folder = Path.of(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).toURI()).getParent();

            System.out.println(folder);

            Path record = Files.list(folder)
                    .filter(path -> path.getFileName().toString().matches("record.*mp3"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Trying to rename a file which doesn't exist"));

            List<Path> sounds = Files.list(folder)
                    .filter(path -> path.getFileName().toString().matches("sound.*mp3"))
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());

            if (!sounds.isEmpty()) {

                int biggestNumber;
                try {
                    biggestNumber = Integer.parseInt(sounds.get(1).toString().split("\\.")[1]);
                } catch (IndexOutOfBoundsException | NumberFormatException e) {
                    // Seul le fichier sound.mp3 existe
                    biggestNumber = 0;
                }

                try {
                    Path oldSound = folder.resolve("sound.mp3");
                    Files.move(oldSound, oldSound.resolveSibling("sound." + String.format("%03d", ++biggestNumber) + ".mp3"));
                } catch (NoSuchFileException ignored) {
                    // Le fichier sound.mp3 n'existe pas
                }
            }

            Files.move(record, record.resolveSibling("sound.mp3"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
