package cloud.deuterium;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.io.input.BoundedInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringTokenizer;
import java.util.stream.Stream;

/**
 * Created by Milan Stojkovic 11-Aug-2024
 */

@ApplicationScoped
public class WCWorker {

    public long countBytes(File file) {
        return file.length();
    }

    public long countLines(Path path) {
        try (Stream<String> lines = Files.lines(path)) {
            return lines.count();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return 0;
        }
    }

    public long countWords(Path path) {
        try (Stream<String> lines = Files.lines(path)) {
            return lines
                    .map(String::trim)
                    .mapToInt(WCWorker::countWordsUsingStringTokenizer)
                    .sum();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return 0;
        }
    }

    public long countCharacters(Path path) {
        try (Stream<String> lines = Files.lines(path)) {
            return lines
                    .mapToInt(String::length)
                    .sum();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return 0;
        }
    }

    public All countAll(Path path) {

        try (Stream<String> lines1 = Files.lines(path);
             Stream<String> lines2 = Files.lines(path)) {

            long countLines = lines1.count();
            int countWords = lines2
                    .map(String::trim)
                    .mapToInt(WCWorker::countWordsUsingStringTokenizer)
                    .sum();
            long countBytes = path.toFile().length();

            return new All(countLines, countWords, countBytes);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    public All countFromPipe(){
        try (
                BoundedInputStream bis = BoundedInputStream.builder()
                        .setInputStream(System.in)
                        .get();
                InputStreamReader is = new InputStreamReader(bis);
                BufferedReader br = new BufferedReader(is);
        ) {

            String line;
            int lineCounter = 0;
            int wordCounter = 0;

            while ((line = br.readLine()) != null) {
                lineCounter++;
                wordCounter = wordCounter + countWordsUsingStringTokenizer(line);
            }
            long byteCounter = bis.getCount();
            return new All(lineCounter, wordCounter, byteCounter);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    public static int countWordsUsingStringTokenizer(String line) {
        if (line == null || line.isEmpty()) {
            return 0;
        }
        StringTokenizer tokens = new StringTokenizer(line);
        return tokens.countTokens();
    }
}
