package hexlet.code.app.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryptor {
    public String encryptString(String password) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("MD5");

        byte[] messageDigest = md.digest(password.getBytes());

        BigInteger bigInt = new BigInteger(1, messageDigest);

        return bigInt.toString(16);
    }
}
