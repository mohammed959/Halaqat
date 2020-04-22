package halaqat.utils;

import halaqat.AppConstants;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class Utils {

    public static boolean validateSession(int userType, HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        if ((int) session.getAttribute(AppConstants.USER_TYPE_KEY) != userType) {
            return false;
        }
        return true;
    }

    public static String hashPassword(String password){
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
        byte[] digest = digestSHA3.digest(password.getBytes());
        return Hex.toHexString(digest);
    }

}
