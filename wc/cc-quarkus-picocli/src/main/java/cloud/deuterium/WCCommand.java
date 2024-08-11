package cloud.deuterium;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static picocli.CommandLine.*;

@Command(name = "ccwc", mixinStandardHelpOptions = true, version = "ccwc 0.1")
public class WCCommand implements Runnable {

    @Inject
    WCWorker worker;

    @Parameters(paramLabel = "<file>", defaultValue = "", description = "File to load.")
    String filePath;

    @Option(names = {"-c", "--bytes"}, description = "Count bytes in File")
    boolean countBytes;

    @Option(names = {"-l", "--lines"}, description = "Count lines in File")
    boolean countLines;

    @Option(names = {"-w", "--words"}, description = "Count words in File")
    boolean countWords;

    @Option(names = {"-m", "--chars"}, description = "Count characters in File")
    boolean countCharacters;

    @Override
    public void run() {

        boolean isKeyboard;

        try {
            isKeyboard = System.in.available() == 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Take bytes from pipe
        if (filePath.isBlank() && !isKeyboard) {
            All all = worker.countFromPipe();
            System.out.printf("%d %d %d%n", all.lineCount(), all.wordCount(), all.byteCount());
            return;
        }

        if (filePath.isBlank()) {
            System.out.println("File name is required");
            return;
        }

        Path inputPath = Paths.get(filePath);
        File file = inputPath.toFile();
        if (!file.exists()) {
            System.err.println("File does not exist: " + file);
            System.exit(1);
        }

        if (countBytes) {
            long length = worker.countBytes(file);
            System.out.println(length);
            return;
        }

        if (countLines) {
            long counted = worker.countLines(inputPath);
            System.out.println(counted);
            return;
        }

        if (countWords) {
            long counted = worker.countWords(inputPath);
            System.out.println(counted);
            return;
        }

        if (countCharacters) {
            long counted = worker.countCharacters(inputPath);
            System.out.println(counted);
            return;
        }

        All all = worker.countAll(inputPath);
        System.out.printf("%d %d %d%n", all.lineCount(), all.wordCount(), all.byteCount());
    }

}
