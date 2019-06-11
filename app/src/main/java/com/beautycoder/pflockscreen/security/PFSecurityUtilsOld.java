package com.beautycoder.pflockscreen.security;

import android.content.Context;
import android.security.KeyPairGeneratorSpec.Builder;
import android.support.annotation.NonNull;
import android.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Calendar;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

public class PFSecurityUtilsOld implements IPFSecurityUtils {
    private static final String RSA_MODE = "RSA/ECB/PKCS1Padding";
    private static final PFSecurityUtilsOld ourInstance = new PFSecurityUtilsOld();

    public static PFSecurityUtilsOld getInstance() {
        return ourInstance;
    }

    private PFSecurityUtilsOld() {
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

    public String encode(@NonNull Context context, String alias, String input, boolean isAuthorizationRequared) throws PFSecurityException {
        try {
            return Base64.encodeToString(rsaEncrypt(context, input.getBytes(), alias), 2);
        } catch (Exception e) {
            e.printStackTrace();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Error while encoding : ");
            stringBuilder.append(e.getMessage());
            throw new PFSecurityException(stringBuilder.toString());
        }
    }

    private byte[] rsaEncrypt(@NonNull Context context, byte[] secret, String keystoreAlias) throws Exception {
        KeyStore keyStore = loadKeyStore();
        generateKeyIfNecessary(context, keyStore, keystoreAlias, false);
        PrivateKeyEntry privateKeyEntry = (PrivateKeyEntry) keyStore.getEntry(keystoreAlias, null);
        Cipher inputCipher = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL");
        inputCipher.init(1, privateKeyEntry.getCertificate().getPublicKey());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, inputCipher);
        cipherOutputStream.write(secret);
        cipherOutputStream.close();
        return outputStream.toByteArray();
    }

    public String decode(String alias, String encodedString) throws PFSecurityException {
        try {
            return new String(rsaDecrypt(Base64.decode(encodedString, 2), alias));
        } catch (Exception e) {
            e.printStackTrace();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Error while decoding: ");
            stringBuilder.append(e.getMessage());
            throw new PFSecurityException(stringBuilder.toString());
        }
    }

    private byte[] rsaDecrypt(byte[] encrypted, String keystoreAlias) throws Exception {
        PrivateKeyEntry privateKeyEntry = (PrivateKeyEntry) loadKeyStore().getEntry(keystoreAlias, null);
        Cipher output = Cipher.getInstance(RSA_MODE, "AndroidOpenSSL");
        output.init(2, privateKeyEntry.getPrivateKey());
        CipherInputStream cipherInputStream = new CipherInputStream(new ByteArrayInputStream(encrypted), output);
        ArrayList<Byte> values = new ArrayList();
        while (true) {
            int read = cipherInputStream.read();
            int nextByte = read;
            if (read == -1) {
                break;
            }
            values.add(Byte.valueOf((byte) nextByte));
        }
        byte[] bytes = new byte[values.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = ((Byte) values.get(i)).byteValue();
        }
        return bytes;
    }

    private boolean generateKey(Context context, String keystoreAlias, boolean isAuthenticationRequired) {
        return generateKeyOld(context, keystoreAlias, isAuthenticationRequired);
    }

    private boolean generateKeyOld(Context context, String keystoreAlias, boolean isAuthenticationRequired) {
        try {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(1, 25);
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            Builder alias = new Builder(context).setAlias(keystoreAlias);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("CN=");
            stringBuilder.append(keystoreAlias);
            keyGen.initialize(alias.setSubject(new X500Principal(stringBuilder.toString())).setSerialNumber(BigInteger.valueOf((long) Math.abs(keystoreAlias.hashCode()))).setEndDate(end.getTime()).setStartDate(start.getTime()).setSerialNumber(BigInteger.ONE).setSubject(new X500Principal("CN = Secured Preference Store, O = Devliving Online")).build());
            keyGen.generateKeyPair();
            return true;
        } catch (GeneralSecurityException exc) {
            exc.printStackTrace();
            return false;
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
