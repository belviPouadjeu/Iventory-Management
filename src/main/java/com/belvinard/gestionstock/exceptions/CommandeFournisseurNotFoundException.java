package com.belvinard.gestionstock.exceptions;

public class CommandeFournisseurNotFoundException extends RuntimeException {
    public CommandeFournisseurNotFoundException(String message) {
        super(message);
    }
}