import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LZW2 {

    static int DICTINIT = 256*256;
    /** Compress a string to a list of output symbols. */
    public static List<Integer> compress(String uncompressed) {

        // Build the dictionary.
        int dictSize = DICTINIT;
        Map<String,Integer> dictionary = new HashMap<>();
        for (int i = 0; i < dictSize; i++) dictionary.put("" + (char)i, i);

        List<Integer> result = new ArrayList<>();
        String w = "";
        for (char c : uncompressed.toCharArray()) {
            String wc = w + c;
            if (dictionary.containsKey(wc)) w = wc;
            else {
                result.add(dictionary.get(w));
                // Add wc to the dictionary.
                dictionary.put(wc, dictSize++);
                w = "" + c;
            }
        }
        // Output the code for w.
        if (!w.equals("")) result.add(dictionary.get(w));
        return result;
    }
 
    /** Decompress a list of output ks to a string. */
    public static String decompress(List<Integer> compressed) {

        // Build the dictionary.
        int dictSize = DICTINIT;
        Map<Integer,String> dictionary = new HashMap<>();
        for (int i = 0; i < dictSize; i++) dictionary.put(i, "" + (char)i);

        String w = "" + (char)(int)compressed.remove(0);
        StringBuilder result = new StringBuilder(w);
        for (int k : compressed) {
            String entry;
            if (dictionary.containsKey(k)) entry = dictionary.get(k);
            else if (k == dictSize) entry = w + w.charAt(0);
            else throw new IllegalArgumentException("Bad compressed k: " + k);
            result.append(entry);
            // Add w+entry[0] to the dictionary.
            dictionary.put(dictSize++, w + entry.charAt(0));
            w = entry;
        }
        return result.toString();
    }
 
    public static void main(String[] args) throws IOException {
        String content = Files.readString(Paths.get("hamlet.txt"), StandardCharsets.UTF_8);
        List<Integer> compressed = compress(content);
        System.out.println(compressed.size());
        String decompressed = decompress(compressed);
        System.out.println(decompressed.length());
    }
}
