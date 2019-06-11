package com.beautycoder.pflockscreen.security;

import android.os.Build.VERSION;

public class PFSecurityUtilsFactory {
    public static IPFSecurityUtils getPFSecurityUtilsInstance() {
        if (VERSION.SDK_INT >= 23) {
            return PFSecurityUtils.getInstance();
        }
        return PFSecurityUtilsOld.getInstance();
    }
}
