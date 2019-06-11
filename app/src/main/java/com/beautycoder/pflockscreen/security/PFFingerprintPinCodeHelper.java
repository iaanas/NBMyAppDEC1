package com.beautycoder.pflockscreen.security;

import android.content.Context;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

public class PFFingerprintPinCodeHelper {
    private static final String FINGERPRINT_ALIAS = "fp_fingerprint_lock_screen_key_store";
    private static final String PIN_ALIAS = "fp_pin_lock_screen_key_store";
    private static final PFFingerprintPinCodeHelper ourInstance = new PFFingerprintPinCodeHelper();
    private final IPFSecurityUtils pfSecurityUtils = PFSecurityUtilsFactory.getPFSecurityUtilsInstance();

    public static PFFingerprintPinCodeHelper getInstance() {
        return ourInstance;
    }

    private PFFingerprintPinCodeHelper() {
    }

    public String encodePin(Context context, String pin) throws PFSecurityException {
        return this.pfSecurityUtils.encode(context, PIN_ALIAS, pin, false);
    }

    public boolean checkPin(Context context, String encodedPin, String pin) throws PFSecurityException {
        return this.pfSecurityUtils.decode(PIN_ALIAS, encodedPin).equals(pin);
    }

    private boolean isFingerPrintAvailable(Context context) {
        return FingerprintManagerCompat.from(context).isHardwareDetected();
    }

    private boolean isFingerPrintReady(Context context) {
        return FingerprintManagerCompat.from(context).hasEnrolledFingerprints();
    }

    public void delete() throws PFSecurityException {
        this.pfSecurityUtils.deleteKey(PIN_ALIAS);
    }

    public boolean isPinCodeEncryptionKeyExist() throws PFSecurityException {
        return this.pfSecurityUtils.isKeystoreContainAlias(PIN_ALIAS);
    }
}
