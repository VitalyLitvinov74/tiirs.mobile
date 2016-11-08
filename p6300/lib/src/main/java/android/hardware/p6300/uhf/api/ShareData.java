/*
 * Decompiled with CFR 0_118.
 */
package android.hardware.p6300.uhf.api;

public class ShareData {
    public static final char Mark_Head = '\u00bb';

    public static void HexToDec(String str, char[] char_pwd) {
        String cmd = String.valueOf(str.substring(0, 2)) + " " + str.substring(2, 4) + " " + str.substring(4, 6) + " " + str.substring(6, 8) + " ";
        String[] strArray = cmd.split(" ");
        char[] data = new char[strArray.length];
        int i = 0;
        while (i < 4) {
            if (ShareData.StringToChar(str, data, strArray.length)) {
                System.out.print(Integer.toHexString(data[i]));
                char_pwd[i] = data[i];
            }
            ++i;
        }
    }

    public static String CharToString(char[] data, int len) {
        String str = "";
        int i = 0;
        while (i < len) {
            str = (data[i] >> 4 & 15) < 10 ? String.valueOf(str) + (char)(48 + (data[i] >> 4 & 15)) : String.valueOf(str) + (char)(65 + ((data[i] >> 4 & 15) - 10));
            str = (data[i] & 15) < 10 ? String.valueOf(str) + (char)(48 + (data[i] & 15)) : String.valueOf(str) + (char)(65 + ((data[i] & 15) - 10));
            str = String.valueOf(str) + " ";
            ++i;
        }
        return str;
    }

    public static boolean StringToChar(String str, char[] data, int length) {
        int strlen = str.length();
        int datalen = 0;
        int i = 0;
        while (i < strlen) {
            if (datalen >= length) {
                return true;
            }
            if (str.charAt(i) != ' ') {
                int value;
                switch (str.charAt(i)) {
                    case '0': {
                        value = 0;
                        break;
                    }
                    case '1': {
                        value = 1;
                        break;
                    }
                    case '2': {
                        value = 2;
                        break;
                    }
                    case '3': {
                        value = 3;
                        break;
                    }
                    case '4': {
                        value = 4;
                        break;
                    }
                    case '5': {
                        value = 5;
                        break;
                    }
                    case '6': {
                        value = 6;
                        break;
                    }
                    case '7': {
                        value = 7;
                        break;
                    }
                    case '8': {
                        value = 8;
                        break;
                    }
                    case '9': {
                        value = 9;
                        break;
                    }
                    case 'A': 
                    case 'a': {
                        value = 10;
                        break;
                    }
                    case 'B': 
                    case 'b': {
                        value = 11;
                        break;
                    }
                    case 'C': 
                    case 'c': {
                        value = 12;
                        break;
                    }
                    case 'D': 
                    case 'd': {
                        value = 13;
                        break;
                    }
                    case 'E': 
                    case 'e': {
                        value = 14;
                        break;
                    }
                    case 'F': 
                    case 'f': {
                        value = 15;
                        break;
                    }
                    default: {
                        return false;
                    }
                }
                data[datalen] = (char)(value << 4);
                if (i + 1 >= strlen) {
                    return false;
                }
                switch (str.charAt(i + 1)) {
                    case '0': {
                        value = 0;
                        break;
                    }
                    case '1': {
                        value = 1;
                        break;
                    }
                    case '2': {
                        value = 2;
                        break;
                    }
                    case '3': {
                        value = 3;
                        break;
                    }
                    case '4': {
                        value = 4;
                        break;
                    }
                    case '5': {
                        value = 5;
                        break;
                    }
                    case '6': {
                        value = 6;
                        break;
                    }
                    case '7': {
                        value = 7;
                        break;
                    }
                    case '8': {
                        value = 8;
                        break;
                    }
                    case '9': {
                        value = 9;
                        break;
                    }
                    case 'A': 
                    case 'a': {
                        value = 10;
                        break;
                    }
                    case 'B': 
                    case 'b': {
                        value = 11;
                        break;
                    }
                    case 'C': 
                    case 'c': {
                        value = 12;
                        break;
                    }
                    case 'D': 
                    case 'd': {
                        value = 13;
                        break;
                    }
                    case 'E': 
                    case 'e': {
                        value = 14;
                        break;
                    }
                    case 'F': 
                    case 'f': {
                        value = 15;
                        break;
                    }
                    default: {
                        return false;
                    }
                }
                data[datalen] = (char)(data[datalen] + value);
                ++i;
                ++datalen;
            }
            ++i;
        }
        i = datalen;
        while (i < length) {
            data[i] = '\u0000';
            ++i;
        }
        return true;
    }
}
