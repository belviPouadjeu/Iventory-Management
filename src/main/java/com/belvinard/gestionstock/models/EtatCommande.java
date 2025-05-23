package com.belvinard.gestionstock.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum EtatCommande {

  EN_PREPARATION,
  VALIDEE,
  LIVREE;

  @JsonCreator
  public static EtatCommande fromString(@JsonProperty("etatCommande") String value) {
    if (value == null) {
      return null;
    }

    try {
      return EtatCommande.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("Valeur inconnue pour EtatCommande : " + value);
    }
  }
}
