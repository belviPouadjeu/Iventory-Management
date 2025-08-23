package com.belvinard.gestionstock.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Annotation de validation pour vérifier qu'une date n'est pas antérieure à aujourd'hui
 */
@Documented
@Constraint(validatedBy = FutureOrTodayValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureOrToday {
    
    String message() default "La date ne peut pas être antérieure à aujourd'hui";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
