package com.belvinard.gestionstock.exceptions;

public class LigneCommandeNotFoundException extends RuntimeException {
    public LigneCommandeNotFoundException(String message) {
        super(message);
    }
}