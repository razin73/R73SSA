import java.util.*;

public class RAnalyseString {
    public static int[] analyse(String str) {
        int count = 0;
        int iList[] = null;
        if (!str.matches("[0-9,\\s\\-]+")) return null;
        String sItems[] = str.split(",");
        for (String s : sItems) {
            if (s.contains("-")) {
                String sRange[] = s.split("\\-");
                if (sRange.length != 2) return null;
                int i1 = getInt(sRange[0]);
                int i2 = getInt(sRange[1]);
                if (i1 < 1 || i2 < 1 || i1 >= i2) return null;
                count += i2 - i1 + 1;
            } else {
                int i = getInt(s);
                if (i < 1) return null;
                count++;
            }
        }
        if (count < 1) return null;
        iList = new int[count];
        count = 0;
        for (String s : sItems) {
            if (s.contains("-")) {
                String sRange[] = s.split("\\-");
                if (sRange.length != 2) return null;
                int i1 = getInt(sRange[0]);
                int i2 = getInt(sRange[1]);
                if (i1 < 1 || i2 < 1 || i1 >= i2) return null;
                for (int i = i1; i <= i2; i++) {
                    iList[count] = i;
                    count ++;
                }
            } else {
                int i = getInt(s);
                if (i < 1) return null;
                iList[count] = i;
                count++;
            }
        }
        return sort(iList);
    }
    public static int getInt(String str) {
        int res = 0;
        String str1 = str.trim();
        if (!str1.matches("[0-9]+")) return 0;
        res = Integer.parseInt(str1);
        return res;
    }
    public static int[] sort(int array[]) {
        if (array == null) return null;
        if (array.length < 1) return null;
        Arrays.sort(array);
        for (int i = 0; i < array.length - 1; i++)
            if (array[i] == array[i + 1]) return null;
        return array;
    }
}