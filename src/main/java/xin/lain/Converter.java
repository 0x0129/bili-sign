package xin.lain;

public class Converter {
    private static final String BASE58_TABLE = "FcwAPNKTMug3GV5Lj7EJnHpWsx4tb8haYeviqBz6rkCy12mUSDQX9RdoZf";
    private static final long AVID_MAX = (1L << 51) - 1;
    private static final long XOR_CODE = 23442827791579L;

    public static String av2bv(long aid) {
        char[] bytes = new char[]{'B', 'V', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0'};
        int bv_idx = bytes.length - 1;

        long tmp = (aid | AVID_MAX) ^ XOR_CODE;

        while (tmp > 0) {
            int index = (int) (tmp % 58);
            bytes[bv_idx--] = BASE58_TABLE.charAt(index);
            tmp /= 58;
        }

        swap(bytes, 3, 9);
        swap(bytes, 4, 7);

        return new String(bytes);
    }

    public static long bv2av(String bvid) {
        char[] chars = bvid.toCharArray();
        swap(chars, 3, 9);
        swap(chars, 4, 7);

        String processedBvid = new String(chars, 3, chars.length - 3);
        long tmp = 0;

        for (int i = 0; i < processedBvid.length(); i++) {
            int idx = BASE58_TABLE.indexOf(processedBvid.charAt(i));
            tmp = tmp * 58 + idx;
        }

        return (tmp & AVID_MAX) ^ XOR_CODE;
    }

    private static void swap(char[] array, int i, int j) {
        char temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
