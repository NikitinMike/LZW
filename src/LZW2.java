import java.util.*;

public class LZW2 {
    /** Compress a string to a list of output symbols. */
    public static List<Integer> compress(String uncompressed) {

        // Build the dictionary.
        int dictSize = 256;
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
        int dictSize = 256;
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
 
    public static void main(String[] args) {
        List<Integer> compressed = compress("TOBEORNOTTOBEORTOBEORNOT");
        System.out.println(compressed);
        String decompressed = decompress(compressed);
        System.out.println(decompressed);
    }
}
