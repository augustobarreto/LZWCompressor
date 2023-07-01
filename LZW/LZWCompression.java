import java.io.*;
import java.util.*;

public class LZWCompression {
    private static final int MAX_TABLE_SIZE = 4096;

    public static void compress(File inputFile, File compressedFile) {
        try (InputStream inputStream = new FileInputStream(inputFile);
             DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(compressedFile))) {

            Map<String, Integer> dictionary = createDictionary();

            int nextCode = 256; 
            String currentSequence = ""; 
            int bytesRead;
            byte[] buffer = new byte[1024];

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    char currentChar = (char) (buffer[i] & 0xFF);
                    String sequence = currentSequence + currentChar;

                    if (dictionary.containsKey(sequence)) {
                        currentSequence = sequence;
                    } else {
                        int code = dictionary.get(currentSequence);
                        outputStream.writeShort(code); 

                        if (nextCode < MAX_TABLE_SIZE) {
                            dictionary.put(sequence, nextCode++);
                        }

                        currentSequence = String.valueOf(currentChar);
                    }
                }
            }

            if (!currentSequence.isEmpty()) {
                int code = dictionary.get(currentSequence);
                outputStream.writeShort(code); 
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java LZWCompression <inputFile> <compressedFile>");
            return;
        }

        String inputFilePath = args[0];
        String compressedFilePath = args[1];

        File inputFile = new File(inputFilePath);
        File compressedFile = new File(compressedFilePath);

        compress(inputFile, compressedFile);

        System.out.println("Compression completed successfully.");
    }

    private static Map<String, Integer> createDictionary() {
        Map<String, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(String.valueOf((char) i), i);
        }
        return dictionary;
    }
}
