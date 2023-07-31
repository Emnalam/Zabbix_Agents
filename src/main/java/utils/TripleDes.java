package utils;

import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.xml.bind.DatatypeConverter;

public class TripleDes {

    private static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private KeySpec ks;
    private SecretKeyFactory skf;
    private Cipher cipher;
    byte[] arrayBytes;
    private String myEncryptionKey;
    private String myEncryptionScheme;
    SecretKey key;

    public TripleDes() throws Exception {
        myEncryptionKey = "ThisIsMySecretEncryptionDES3Key";
        myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
        arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
        ks = new DESedeKeySpec(arrayBytes);
        skf = SecretKeyFactory.getInstance(myEncryptionScheme);
        cipher = Cipher.getInstance(myEncryptionScheme);
        key = skf.generateSecret(ks);
    }


    
    /** 
     * @param unencryptedString
     * @return String
     * @throws Exception
     */
    public String encrypt(String unencryptedString) throws Exception {
        String encryptedString = null;
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
        byte[] encryptedText = cipher.doFinal(plainText);
        encryptedString = new String(toHexString(encryptedText));
        
        return "enc(" + encryptedString + ")";
    }


    
    /** 
     * @param encryptedString
     * @return String
     * @throws Exception
     */
    public String decrypt(String encryptedString) throws Exception {
        String decryptedText=null;
        encryptedString = encryptedString.replace("enc(", "").replace(")", "");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] encryptedText = toByteArray(encryptedString);
        byte[] plainText = cipher.doFinal(encryptedText);
        decryptedText= new String(plainText);
       
        return decryptedText;
    }
    
    
    /** 
     * @param array
     * @return String
     */
    public static String toHexString(byte[] array) {
        return DatatypeConverter.printHexBinary(array);
    }

    
    /** 
     * @param s
     * @return byte[]
     */
    public static byte[] toByteArray(String s) {
        return DatatypeConverter.parseHexBinary(s);
    }
}

