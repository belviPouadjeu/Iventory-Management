package com.belvinard.gestionstock.exceptions;

import lombok.Getter;

@Getter
public class InvalidEntityException extends RuntimeException {

    private final String entityName;
    private final String reason;

    public InvalidEntityException(String entityName, String reason) {
        super(String.format("Entité invalide [%s] : %s", entityName, reason));
        this.entityName = entityName;
        this.reason = reason;
    }

    public InvalidEntityException(String message) {
        super(message);
        this.entityName = "Inconnu";
        this.reason = message;
    }
}
