package com.beautycoder.pflockscreen.security;

import android.annotation.TargetApi;
import android.content.Context;
import android.security.keystore.KeyGenParameterSpec.Builder;
import android.support.annotation.NonNull;
import android.util.Base64;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource.PSpecified;

public class PFSecurityUtils implements IPFSecurityUtils {
    private static final PFSecurityUtils ourInstance = new PFSecurityUtils();

    public static PFSecurityUtils getInstance() {
        return ourInstance;
    }

    private PFSecurityUtils() {
    }

    private KeyStore loadKeyStore() throws PFSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            return keyStore;
        } catch (Exception e) {
            e.printStackTrace();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Can not load keystore:");
            stringBuilder.append(e.getMessage());
            throw new PFSecurityException(stringBuilder.toString());
        }
    }

    public String encode(@NonNull Context context, String alias, String input, boolean isAuthorizationRequared) throws PFSecurityException {
        try {
            return Base64.encodeToString(getEncodeCipher(context, alias, isAuthorizationRequared).doFinal(input.getBytes()), 2);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Error while encoding : ");
            stringBuilder.append(e.getMessage());
            throw new PFSecurityException(stringBuilder.toString());
        }
    }

    private Cipher getEncodeCipher(@NonNull Context context, String alias, boolean isAuthenticationRequired) throws PFSecurityException {
        Cipher cipher = getCipherInstance();
        KeyStore keyStore = loadKeyStore();
        generateKeyIfNecessary(context, keyStore, alias, isAuthenticationRequired);
        initEncodeCipher(cipher, alias, keyStore);
        return cipher;
    }

    private boolean generateKeyIfNecessary(@NonNull Context context, @NonNull KeyStore keyStore, String alias, boolean isAuthenticationRequired) {
        boolean z = false;
        try {
            if (!keyStore.containsAlias(alias)) {
                if (!generateKey(context, alias, isAuthenticationRequired)) {
                    return z;
                }
            }
            z = true;
            return z;
        } catch (KeyStoreException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean generateKey(Context context, String keystoreAlias, boolean isAuthenticationRequired) {
        return generateKey(keystoreAlias, isAuthenticationRequired);
    }

    private String decode(String encodedString, Cipher cipher) throws PFSecurityException {
        try {
            return new String(cipher.doFinal(Base64.decode(encodedString, 2)));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Error while decoding: ");
            stringBuilder.append(e.getMessage());
            throw new PFSecurityException(stringBuilder.toString());
        }
    }

    public String decode(String alias, String encodedString) throws PFSecurityException {
        try {
            Cipher cipher = getCipherInstance();
            initDecodeCipher(cipher, alias);
            return new String(cipher.doFinal(Base64.decode(encodedString, 2)));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Error while decoding: ");
            stringBuilder.append(e.getMessage());
            throw new PFSecurityException(stringBuilder.toString());
        }
    }

    @TargetApi(23)
    private boolean generateKey(String keystoreAlias, boolean isAuthenticationRequired) {
        try {
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            keyGenerator.initialize(new Builder(keystoreAlias, 3).setDigests(new String[]{"SHA-256", "SHA-512"}).setEncryptionPaddings(new String[]{"OAEPPadding"}).setUserAuthenticationRequired(isAuthenticationRequired).build());
            keyGenerator.generateKeyPair();
            return true;
        } catch (GeneralSecurityException exc) {
            exc.printStackTrace();
            return false;
        }
    }

    private Cipher getCipherInstance() throws PFSecurityException {
        try {
            return Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Can not get instance of Cipher object");
            stringBuilder.append(e.getMessage());
            throw new PFSecurityException(stringBuilder.toString());
        }
    }

    private void initDecodeCipher(Cipher cipher, String alias) throws PFSecurityException {
        try {
            cipher.init(2, (PrivateKey) loadKeyStore().getKey(alias, null));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Error while decoding: ");
            stringBuilder.append(e.getMessage());
            throw new PFSecurityException(stringBuilder.toString());
        }
    }

    private void initEncodeCipher(Cipher cipher, String alias, KeyStore keyStore) throws PFSecurityException {
        try {
            PublicKey key = keyStore.getCertificate(alias).getPublicKey();
            cipher.init(1, KeyFactory.getInstance(key.getAlgorithm()).generatePublic(new X509EncodedKeySpec(key.getEncoded())), new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSpecified.DEFAULT));
        } catch (GeneralSecurityException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Can not initialize Encode Cipher:");
            stringBuilder.append(e.getMessage());
            throw new PFSecurityException(stringBuilder.toString());
        }
    }

    public boolean isKeystoreContainAlias(String alias) throws PFSecurityException {
        try {
            return loadKeyStore().containsAlias(alias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            throw new PFSecurityException(e.getMessage());
        }
    }

    public void deleteKey(String alias) throws PFSecurityException {
        try {
            loadKeyStore().deleteEntry(alias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Can not delete key: ");
            stringBuilder.append(e.getMessage());
            throw new PFSecurityException(stringBuilder.toString());
        }
    }
}
