import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;


public class BinaryLZWCompression {
    private static final int MAX_TABLE_SIZE = 4096*8;

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

    public static double entropy(byte[] input) {
        if (input.length == 0) {
            return 0.0;
        }

        int[] charCounts = new int[256];

        for (byte b : input) {
            charCounts[b & 0xFF]++;
        }

        double entropy = 0.0;
        for (int i = 0; i < 128; ++i) {

            if (charCounts[i] == 0.0) {
                continue;
            }
            double freq = (double) charCounts[i] / input.length;
            entropy -= freq * (Math.log(freq) / Math.log(2));
        }

        return entropy;
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
        
        System.out.println( "**********************");
        System.out.println( "Tamanho arquivo de entrada: " + inputFile.length());
        System.out.println( "Tamanho arquivo de saida: " + compressedFile.length());
        
        
        try {
        double inputEntropy = entropy(Files.readAllBytes(inputFile.toPath()));
        double outputEntropy = entropy(Files.readAllBytes(compressedFile.toPath()));
        
        System.out.println( "Entropia de entrada: " + inputEntropy);
        System.out.println( "Entropia de saida: " + outputEntropy);

        double rateCompression = inputEntropy / outputEntropy;
        System.out.println( "Taxa de compressao em relacao a entropia: " + rateCompression);
        // Continue processing with the calculated entropies
        } catch (IOException e) {
        // Handle the exception
        e.printStackTrace(); // Print the stack trace (optional)
        // Additional error handling logic if needed
        }

        


    }

    private static Map<List<Byte>, Short> createDictionary() {
        Map<List<Byte>, Short> dictionary = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(Collections.singletonList((byte) i), (short) i);
        }
        return dictionary;
    }
}
