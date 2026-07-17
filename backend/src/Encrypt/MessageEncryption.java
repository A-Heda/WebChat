package Encrypt;

public class MessageEncryption {

    private static final String KEY = "encrypt_hirad_heda_26";

    public static String encrypt(String s) {
        if (s == null) return null;

        String out = "";
        for (int i = 0; i < s.length(); i++) {
            int temp = s.charAt(i) ^ KEY.charAt(i % KEY.length());
            out += temp;
            if (i < s.length() - 1) {
                out += ",";
            }
        }
        return out;
    }

    public static String decrypt(String s) {
        if (s == null || s.isEmpty()) return s;
        try {
            String[] parts = s.split(",");
            String out = "";
            for (int i = 0; i < parts.length; i++) {
                int val = Integer.parseInt(parts[i]);
                char original = (char) (val ^ KEY.charAt(i % KEY.length()));
                out += original;
            }
            return out;
        } catch (Exception e) {
            return s;
        }
    }
}
