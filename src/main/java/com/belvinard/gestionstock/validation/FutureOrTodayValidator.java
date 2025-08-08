package com.belvinard.gestionstock.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

/**
 * Validateur pour l'annotation @FutureOrToday
 * Vérifie qu'une date LocalDate n'est pas antérieure à aujourd'hui
 */
public class FutureOrTodayValidator implements ConstraintValidator<FutureOrToday, LocalDate> {

    @Override
    public void initialize(FutureOrToday constraintAnnotation) {
        // Pas d'initialisation nécessaire
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        // Si la valeur est null, on laisse @NotNull gérer cette validation
        if (value == null) {
            return true;
        }
        
        // Vérifier que la date n'est pas antérieure à aujourd'hui
        LocalDate today = LocalDate.now();
        return !value.isBefore(today);
    }
}
