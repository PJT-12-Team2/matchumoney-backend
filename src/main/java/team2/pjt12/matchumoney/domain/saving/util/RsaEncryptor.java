package team2.pjt12.matchumoney.domain.saving.util;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.X509EncodedKeySpec;

//암호화
public class RsaEncryptor {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String encryptRSA(String text, String publicKey) {
        try {
            byte[] decoded = Base64.decodeBase64(publicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(spec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return Base64.encodeBase64String(cipher.doFinal(text.getBytes()));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.RSA_ENCRYPTION_FAIL);
        }
    }
}
