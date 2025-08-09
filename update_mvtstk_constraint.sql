-- Script pour mettre à jour la contrainte CHECK sur la table mvtstk
-- pour inclure les nouveaux types de mouvements RESERVATION et ANNULATION_RESERVATION

-- Supprimer l'ancienne contrainte
ALTER TABLE mvtstk DROP CONSTRAINT IF EXISTS mvtstk_typemvt_check;

-- Ajouter la nouvelle contrainte avec les nouveaux types
ALTER TABLE mvtstk ADD CONSTRAINT mvtstk_typemvt_check 
CHECK (typemvt IN ('ENTREE', 'SORTIE', 'CORRECTION_POS', 'CORRECTION_NEG', 'RESERVATION', 'ANNULATION_RESERVATION'));