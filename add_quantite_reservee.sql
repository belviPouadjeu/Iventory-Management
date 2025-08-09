-- Script pour ajouter la colonne quantite_reservee si elle n'existe pas
ALTER TABLE article ADD COLUMN IF NOT EXISTS quantite_reservee BIGINT DEFAULT 0;