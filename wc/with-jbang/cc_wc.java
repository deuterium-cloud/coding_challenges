///usr/bin/env jbang "$0" "$@" ; exit $?

//JAVA 21+
//DEPS commons-io:commons-io:2.16.1

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.apache.commons.io.input.CountingInputStream;

/**
 * Example: cat test.txt | jbang cc_wc.java
 */
public class cc_wc {

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws Exception {

        // BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        CountingInputStream cis = new CountingInputStream(System.in);
        InputStreamReader is = new InputStreamReader(cis);

        int lineCounter;
        int wordCounter;
        long byteCounter;

        try (BufferedReader br = new BufferedReader(is)) {
            String line;
            lineCounter = 0;
            wordCounter = 0;
            byteCounter = 0;

            while ((line = br.readLine()) != null) {
                lineCounter++;
                wordCounter = wordCounter + countWordsUsingStringTokenizer(line);
            }

            byteCounter = cis.getByteCount();
        }

        System.out.printf("%d %d %d%n", lineCounter, wordCounter, byteCounter);
    }

    public static int countWordsUsingStringTokenizer(String line) {
        if (line == null || line.isEmpty()) {
            return 0;
        }
        StringTokenizer tokens = new StringTokenizer(line.trim());
        return tokens.countTokens();
    }
}
