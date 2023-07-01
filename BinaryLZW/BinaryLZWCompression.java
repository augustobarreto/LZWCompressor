import java.io.*;
import java.util.*;

public class BinaryLZWCompression {
    private static final int MAX_TABLE_SIZE = 4096;

    public static void compress(File inputFile, File compressedFile) {
        try (InputStream inputStream = new FileInputStream(inputFile);
             DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(compressedFile))) {

            Map<List<Byte>, Short> dictionary = createDictionary();

            int nextCode = 256;
            List<Byte> currentSequence = new ArrayList<>();
            int currentByte = 0;
            byte[] buffer = new byte[1024];

            while ((currentByte = inputStream.read(buffer)) != -1) {
                for (int i = 0; i < currentByte; i++) {
                    byte b = buffer[i];
                    List<Byte> sequence = new ArrayList<>(currentSequence);
                    sequence.add(b);

                    if (dictionary.containsKey(sequence)) {
                        currentSequence = sequence;
                    } else {
                        short code = dictionary.get(currentSequence);
                        outputStream.writeShort(code);

                        if (nextCode < MAX_TABLE_SIZE) {
                            dictionary.put(sequence, (short) nextCode++);
                        }

                        currentSequence = new ArrayList<>();
                        currentSequence.add(b);
                    }
                }
            }

            if (!currentSequence.isEmpty()) {
                short code = dictionary.get(currentSequence);
                outputStream.writeShort(code);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java BinaryLZWCompression <inputFile> <compressedFile>");
            return;
        }

        String inputFilePath = args[0];
        String compressedFilePath = args[1];

        File inputFile = new File(inputFilePath);
        File compressedFile = new File(compressedFilePath);

        compress(inputFile, compressedFile);

        System.out.println("Compression completed successfully.");
    }

    private static Map<List<Byte>, Short> createDictionary() {
        Map<List<Byte>, Short> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(Collections.singletonList((byte) i), (short) i);
        }
        return dictionary;
    }
}
