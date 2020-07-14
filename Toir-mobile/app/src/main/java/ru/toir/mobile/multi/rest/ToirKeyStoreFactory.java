package ru.toir.mobile.multi.rest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.acra.security.KeyStoreFactory;

import java.security.KeyStore;

public class ToirKeyStoreFactory implements KeyStoreFactory {

    @Nullable
    @Override
    public KeyStore create(@NonNull Context context) {
        return ToirAPIFactory.getKeyStore();
    }
}
