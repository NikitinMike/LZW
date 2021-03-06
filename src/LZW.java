import java.io.*;
import java.util.HashMap;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.logging.Level.SEVERE;

public class LZW {

    HashMap<String, Short> compdic;
    HashMap<Short,String> decompdic;
    short lastcode = 0, dlastcode = 0;

    LZW(String fileName) {
        compdic = new HashMap<>();
        decompdic = new HashMap<>();
        createDictionary(fileName);
    }

    public static void main(String[] args) {
        String fileName = "hamlet.txt";
        LZW lzw = new LZW(fileName);
        lzw.compressFile(fileName);
        lzw.decompressFile(fileName);
    }

    public void createDictionary(String fileName) {
        try (InputStreamReader rdr = new InputStreamReader(new FileInputStream(fileName), UTF_8)) {
            for (short code; (code = (short) rdr.read()) != -1; ) {
                char ch = (char) code;
                if (!compdic.containsKey(ch)) {
                    compdic.put("" + ch, code);
                    decompdic.put(code, "" + ch);
                    if (code > lastcode) {
                        lastcode = code;
                        dlastcode = code;
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(LZW.class.getName()).log(SEVERE, null, ex);
        }
    }

    public void compressFile(String fileName) {
        try (InputStreamReader rdr = new InputStreamReader(new FileInputStream(fileName), UTF_8)) {
            System.out.print("Compressing...");
            ObjectOutputStream fout = new ObjectOutputStream(new FileOutputStream(fileName + "1.lzw"));
            String s = (char) rdr.read() + "";
            for (short code; (code = (short) rdr.read()) != -1; ) {
                char c = (char) code;
                if (!compdic.containsKey(s + c)) {
                    fout.writeShort(Integer.parseInt(compdic.get(s).toString()));
                    compdic.put(s + c, ++lastcode);
                    s = "" + c;
                } else s = s + c;
            }
            fout.writeShort(Integer.parseInt(compdic.get(s).toString()));
            fout.writeShort(0);
            fout.close();
            rdr.close();
            System.out.print("done");
        } catch (Exception ex) {
            Logger.getLogger(LZW.class.getName()).log(SEVERE, null, ex);
        }
    }

    public void decompressFile(String fileName) {
        try (ObjectInputStream fin = new ObjectInputStream(new FileInputStream(fileName + "1.lzw"))) {
            FileWriter fos = new FileWriter(fileName + "2.txt");
            System.out.print("\nDecompressing...");
            short priorcode = fin.readShort();
            fos.write(decompdic.get(priorcode));
            for (short codeword; (codeword = fin.readShort()) != -1; ) {
                if (codeword == 0) break;
                String priorstr = decompdic.get(priorcode);
                if (decompdic.containsKey(codeword)) {
                    String str = decompdic.get(codeword);
                    fos.write(str);
                    decompdic.put(++dlastcode, priorstr + str.charAt(0));
                } else {
                    decompdic.put(++dlastcode, priorstr + priorstr.charAt(0));
                    fos.write(priorstr + priorstr.charAt(0));
                }
                priorcode = codeword;
            }
            fos.close();
            fin.close();
            System.out.print("done\n");
        } catch (Exception ex) {
            //Logger.getLogger(LZW.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("\n\nError: " + ex.getMessage());
            System.out.print(dlastcode);
        }
    }
}