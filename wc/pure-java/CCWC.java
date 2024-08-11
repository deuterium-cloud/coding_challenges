import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.util.stream.Stream;

public class CCWC {

    /**
     * Example:
     * 1. cat text.txt | java CCWC
     * 2. java CCWC text.txt
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        boolean isNotKeyboard = System.in.available() != 0;
        boolean isFile = args.length > 0 && Paths.get(args[0]).toFile().exists();

        if (isNotKeyboard) {

            byte[] allBytes = System.in.readAllBytes();
            int byteCounter = allBytes.length;

            try (InputStream is = new ByteArrayInputStream(allBytes);
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr)) {

                String line;

                int lineCounter = 0;
                int wordCounter = 0;

                while ((line = br.readLine()) != null) {
                    lineCounter++;
                    wordCounter = wordCounter + countWordsUsingStringTokenizer(line);
                }

                System.out.printf("\t%d\t%d\t%d%n", lineCounter, wordCounter, byteCounter);
                return;
            }
        }

        if (isFile) {

            Path path = Paths.get(args[0]);

            try (Stream<String> lines1 = Files.lines(path);
                    Stream<String> lines2 = Files.lines(path)) {

                long lineCounter = lines1.count();
                int wordCounter = lines2
                        .map(String::trim)
                        .mapToInt(CCWC::countWordsUsingStringTokenizer)
                        .sum();

                long byteCounter = path.toFile().length();

                System.out.printf("\t%d\t%d\t%d%n", lineCounter, wordCounter, byteCounter);
                return;
            }
        }

        String message = args.length == 0 ? "File name is required. Usage: ccwc <file_name>"
                : "File does not exist: '%s'".formatted(args[0]);
        System.err.println(message);
        System.exit(1);
    }

    public static int countWordsUsingStringTokenizer(String line) {
        if (line == null || line.isEmpty()) {
            return 0;
        }
        StringTokenizer tokens = new StringTokenizer(line.trim());
        return tokens.countTokens();
    }

}
