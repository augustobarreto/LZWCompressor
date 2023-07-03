import java.io.*;
import java.util.*;

public class BinaryLZWDecompression {
    private static final int MAX_TABLE_SIZE =4096*16;

    public static void decompress(File compressedFile, File outputFile) {
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(compressedFile));
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {

            Map<Short, List<Byte>> dictionary = createDictionary();
            int nextCode = 256;
            List<Byte> previousSequence = new ArrayList<>();
            List<Byte> currentSequence = new ArrayList<>();

            while (inputStream.available() > 0) {
                short code = inputStream.readShort();
                if (dictionary.containsKey(code)) {
                    currentSequence = dictionary.get(code);
                    outputStream.write(toByteArray(currentSequence));
                    if (!previousSequence.isEmpty() && nextCode < MAX_TABLE_SIZE) {
                        List<Byte> extendedSequence = new ArrayList<>(previousSequence);
                        extendedSequence.add(currentSequence.get(0));
                        dictionary.put((short) nextCode++, extendedSequence);
                    }
                } else if (code == nextCode) {
                    currentSequence = new ArrayList<>(previousSequence);
                    currentSequence.add(previousSequence.get(0));
                    outputStream.write(toByteArray(currentSequence));
                    if (!previousSequence.isEmpty() && nextCode < MAX_TABLE_SIZE) {
                        List<Byte> extendedSequence = new ArrayList<>(previousSequence);
                        extendedSequence.add(previousSequence.get(0));
                        dictionary.put((short) nextCode++, extendedSequence);
                    }
                } else {
                    throw new IllegalStateException("Invalid compressed data.");
                }
                previousSequence = currentSequence;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java BinaryLZWDecompression <compressedFile> <outputFile>");
            return;
        }

        String compressedFilePath = args[0];
        String outputFilePath = args[1];

        File compressedFile = new File(compressedFilePath);
        File outputFile = new File(outputFilePath);

        decompress(compressedFile, outputFile);

        System.out.println("Decompression completed successfully.");
    }

    private static Map<Short, List<Byte>> createDictionary() {
        Map<Short, List<Byte>> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put((short) i, Collections.singletonList((byte) i));
        }
        return dictionary;
    }

    private static byte[] toByteArray(List<Byte> list) {
        byte[] array = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}
