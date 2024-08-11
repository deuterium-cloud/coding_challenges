import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import org.apache.commons.io.input.BoundedInputStream;

/**
 * Example: cat text.txt | java -cp .:commons-io-2.16.1.jar WCTool
 */
public class WCTool {

    public static void main(String[] args) throws Exception {

        BoundedInputStream bis = BoundedInputStream.builder()
                .setInputStream(System.in)
                .get();

        InputStreamReader is = new InputStreamReader(bis);

        try (BufferedReader br = new BufferedReader(is)) {
            String line;
            int lineCounter = 0;
            int wordCounter = 0;

            while ((line = br.readLine()) != null) {
                lineCounter++;
                wordCounter = wordCounter + countWordsUsingStringTokenizer(line);
            }

            long byteCounter = bis.getCount();

            System.out.printf("%d %d %d%n", lineCounter, wordCounter, byteCounter);
        }
    }

    public static int countWordsUsingStringTokenizer(String line) {
        if (line == null || line.isEmpty()) {
            return 0;
        }
        StringTokenizer tokens = new StringTokenizer(line.trim());
        return tokens.countTokens();
    }

}