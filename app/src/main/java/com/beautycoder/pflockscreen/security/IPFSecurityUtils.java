package com.beautycoder.pflockscreen.security;

import android.content.Context;
import android.support.annotation.NonNull;

public interface IPFSecurityUtils {
    String decode( String str , String str2 ) throws PFSecurityException;

    void deleteKey( String str ) throws PFSecurityException;

    String encode( @NonNull Context context , String str , String str2 , boolean z ) throws PFSecurityException;

    boolean isKeystoreContainAlias( String str ) throws PFSecurityException;
}
