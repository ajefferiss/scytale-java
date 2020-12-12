package com.openmoments.scytale.exception;

public class InvalidKeystoreException extends Exception {
    public InvalidKeystoreException(String msg) {
        super("Could not create a valid keystore " + msg);
    }
}
