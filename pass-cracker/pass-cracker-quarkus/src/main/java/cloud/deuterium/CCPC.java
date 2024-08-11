package cloud.deuterium;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Optional;
import java.util.stream.Stream;

import static picocli.CommandLine.*;

@Command(name = "ccpc", mixinStandardHelpOptions = true, description = "Code Challenge Password Cracker")
public class CCPC implements Runnable {

    private final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVXYZ".toCharArray();

    @Parameters(paramLabel = "<name>", defaultValue = "",
            description = "Password to hash.")
    String input;

    @Option(names = {"-h", "--hash"}, description = "Hash input with MD5 hashing algorithm")
    boolean hash;

    @Option(names = {"-b", "--brute"}, description = "Find text for given hash")
    boolean brute;

    @Option(names = {"-p", "--path"}, description = "Path to word list")
    String path;

    @Override
    public void run() {
        try {

            if (input.isBlank()) {
                System.out.println("Invalid input. Try --help for more information.");
                return;
            }

            // Hashing input with MD5 hash algorithm
            if (hash) {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(input.getBytes());
                byte[] digest = md.digest();

                HexFormat hexFormat = HexFormat.of();
                String hex = hexFormat.formatHex(digest);

                System.out.println(hex);
                return;
            }

            // Brutal force attack but only for 4 char password
            if (brute) {
                MessageDigest md = MessageDigest.getInstance("MD5");
                HexFormat hexFormat = HexFormat.of();
                byte[] inputArray = hexFormat.parseHex(input);
                long start = System.nanoTime();

                for (char c1 : CHARACTERS) {
                    for (char c2 : CHARACTERS) {
                        for (char c3 : CHARACTERS) {
                            for (char c4 : CHARACTERS) {
                                String combination = String.valueOf(new char[]{c1, c2, c3, c4});
                                md.update(combination.getBytes());
                                byte[] digest = md.digest();

                                boolean equals = Arrays.equals(digest, inputArray);
                                if (equals) {
                                    System.out.println(combination);
                                    System.out.println("Finished in " + (System.nanoTime() - start) / 1000000 + "ms");
                                }
                                md.reset();
                            }
                        }
                    }
                }
                return;
            }

            // Check against common passwords
            if (!path.isBlank()) {
                Path inputPath = Paths.get(path);
                File file = inputPath.toFile();
                if (!file.exists()) {
                    System.err.println("File does not exist: " + file);
                    System.exit(1);
                }

                MessageDigest md = MessageDigest.getInstance("MD5");
                HexFormat hexFormat = HexFormat.of();
                byte[] inputArray = hexFormat.parseHex(input);

                long start = System.nanoTime();

                /**
                 *  zennin = 18bc9092d31b6c63827b88563f9291d6
                 *  Finished in 31810ms
                 */
//                String line;
//                try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));) {
//                    while ((line = br.readLine()) != null) {
//
//                        md.update(line.getBytes());
//                        byte[] digest = md.digest();
//                        boolean equals = Arrays.equals(digest, inputArray);
//                        if (equals) {
//                            System.out.println(line);
//                            System.out.println("Finished in " + (System.nanoTime() - start) / 1000000 + "ms");
//                            break;
//                        }
//                        md.reset();
//                    }
//
//                } catch (IOException ex) {
//                    throw new RuntimeException(ex);
//                }

                /**
                 *  zennin = 18bc9092d31b6c63827b88563f9291d6
                 *  Finished in 28527ms
                 */
                try (Stream<String> lines = Files.lines(inputPath, StandardCharsets.ISO_8859_1)) {

                    Optional<String> first = lines
                            .filter(line -> {
                                md.update(line.getBytes());
                                byte[] digest = md.digest();
                                boolean equals = Arrays.equals(digest, inputArray);
                                md.reset();
                                return equals;
                            })
                            .findFirst();

                    first.ifPresent(line -> {
                        System.out.println(line);
                        System.out.println("Finished in " + (System.nanoTime() - start) / 1000000 + "ms");
                    });
                }
                return;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
