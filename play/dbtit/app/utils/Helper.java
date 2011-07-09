package utils;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class Helper {
    /**
     * Convert a number written in hexadecimal in a number written with a radix of 62
     *
     * @param str String containing the hexadecimal number
     * @return The same number written in radix 62
     */
    public static String hexTo62(String str) {
        BigInteger num = new BigInteger(str, 16);
        return bigIntTo62(num);
    }

    public static String bigIntTo62(BigInteger num) {
        StringBuffer buffer = new StringBuffer();

        BigInteger[] tmp;

        do {
            tmp = num.divideAndRemainder(new BigInteger("62"));
            buffer.insert(0, numToChar(Integer.parseInt(tmp[1].toString())));
            num = tmp[0];
        } while (!tmp[0].equals(new BigInteger("0")));

        return buffer.toString();
    }

    public static char numToChar(int digit) {
        if (digit < 10) {
            return (char) ('0' + digit);
        } else if (digit < 36) {
            return (char) ('a' + digit - 10);
        } else {
            return (char) ('A' + digit - 36);
        }
    }

    /**
     * Helper to get a minimal date.
     *
     * @return A small date (1970/01/01)
     */
    public static Date getMinDate() {
        Calendar cal = new GregorianCalendar();
        cal.clear();
        return cal.getTime();
    }

    public static <T> String join(final Collection<T> objs, final String delimiter) {
        if (objs == null || objs.isEmpty())
            return "";
        Iterator<T> iter = objs.iterator();
        StringBuffer buffer = new StringBuffer(iter.next().toString());
        while (iter.hasNext())
            buffer.append(delimiter).append(iter.next().toString());
        return buffer.toString();
    }
}
