import java.io.*;
import java.util.*;

public class LZWDecompression {
    private static final int MAX_TABLE_SIZE = 4096;

    public static void decompress(File compressedFile, File outputFile) {
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(compressedFile));
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {

            Map<Integer, String> dictionary = createDictionary();
            int nextCode = 256; // Next available code in the dictionary
            List<Integer> compressedData = new ArrayList<>();

            while (inputStream.available() > 0) {
                int code = inputStream.readShort();
                compressedData.add(code);
            }

            String previousSequence = dictionary.get(compressedData.get(0));
            outputStream.write(previousSequence.getBytes());

            for (int i = 1; i < compressedData.size(); i++) {
                int currentCode = compressedData.get(i);
                String currentSequence;

                if (dictionary.containsKey(currentCode)) {
                    currentSequence = dictionary.get(currentCode);
                } else if (currentCode == nextCode) {
                    currentSequence = previousSequence + previousSequence.charAt(0);
                } else {
                    throw new IllegalStateException("Invalid compressed data.");
                }

                outputStream.write(currentSequence.getBytes());

                if (nextCode < MAX_TABLE_SIZE) {
                    dictionary.put(nextCode++, previousSequence + currentSequence.charAt(0));
                }

                previousSequence = currentSequence;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java LZWDecompression <compressedFile> <outputFile>");
            return;
        }

        String compressedFilePath = args[0];
        String outputFilePath = args[1];

        File compressedFile = new File(compressedFilePath);
        File outputFile = new File(outputFilePath);

        decompress(compressedFile, outputFile);

        System.out.println("Decompression completed successfully.");
    }

    private static Map<Integer, String> createDictionary() {
        Map<Integer, String> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(i, String.valueOf((char) i));
        }
        return dictionary;
    }
}