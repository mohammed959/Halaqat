package halaqat.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Validator {
    public static boolean nationalId(String nationalId) {
        return nationalId != null && nationalId.matches("^[1|2]\\d{9}$");
    }

    public static boolean mobileNo(String mobileNo) {
        return mobileNo != null && mobileNo.matches("^05\\d{8}$");
    }

    public static boolean words(String toMatch) {
        return toMatch != null && toMatch.matches("^[\\p{InArabic}|\\s]+$");
    }

    public static boolean date(String toBeValidated, Date min, Date max) {
        if (toBeValidated == null)
            return false;
        Date date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = simpleDateFormat.parse(toBeValidated);
        } catch (ParseException e) {
            return false;
        }
        if (min != null && date.before(min)) // If minimum date is passed, check that date is not before it
            return false;
        if (max != null && date.after(max)) // If maximum date is passed, check that date is not after it
            return false;
        return true;
    }


}
