# 👥 Utilisateurs par Défaut - Gestion Stock

Ce document liste tous les utilisateurs créés automatiquement au démarrage de l'application.

## 🏢 Entreprise par Défaut
- **Nom** : Default Company
- **Description** : Entreprise par défaut du système
- **Code Fiscal** : DEFAULT001
- **Email** : contact@default.com
- **Téléphone** : +1234567890

## 👤 Utilisateurs par Défaut

### 🔴 ADMIN - Administrateur Système
- **Email** : `admin@gestionstock.com`
- **Mot de passe** : `admin123`
- **Nom** : Admin System
- **Username** : admin
- **Rôle** : ADMIN
- **Permissions** : Accès complet à toutes les fonctionnalités

### 📦 STOCK_MANAGER - Gestionnaire de Stock
- **Email** : `stock.manager@gestionstock.com`
- **Mot de passe** : `stock123`
- **Nom** : Stock Manager
- **Username** : stockmanager
- **Rôle** : STOCK_MANAGER
- **Permissions** : Supervision complète des mouvements de stock

### 💼 SALES_MANAGER - Responsable Commercial
- **Email** : `sales.manager@gestionstock.com`
- **Mot de passe** : `sales123`
- **Nom** : Sales Manager
- **Username** : salesmanager
- **Rôle** : SALES_MANAGER
- **Permissions** : Gestion des ventes et relations clients

### ⚙️ OPERATOR - Opérateur d'Entrepôt
- **Email** : `operator@gestionstock.com`
- **Mot de passe** : `operator123`
- **Nom** : Operator Warehouse
- **Username** : operator
- **Rôle** : OPERATOR
- **Permissions** : Exécution des opérations quotidiennes d'entrepôt

### 🛒 SALES_REP - Commercial/Vendeur
- **Email** : `sales.rep@gestionstock.com`
- **Mot de passe** : `salesrep123`
- **Nom** : Sales Representative
- **Username** : salesrep
- **Rôle** : SALES_REP
- **Permissions** : Consultation des stocks et création de commandes

### 👤 USER_BASE - Utilisateur de Base
- **Email** : `user@gestionstock.com`
- **Mot de passe** : `user123`
- **Nom** : User Base
- **Username** : userbase
- **Rôle** : USER_BASE
- **Permissions** : Accès aux fonctionnalités de base

## 🔐 Sécurité

- ⚠️ **IMPORTANT** : Changez ces mots de passe par défaut en production !
- 🔒 Tous les mots de passe sont encodés avec BCrypt
- 🛡️ Les mots de passe ne sont jamais exposés dans les réponses API

## 🧪 Test de Connexion

Pour tester la connexion avec un utilisateur :

```bash
curl -X 'POST' \
  'http://localhost:8082/api/v1/auth/public/signin' \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "admin@gestionstock.com",
    "password": "admin123"
  }'
```

## 📋 Logs de Création

Au démarrage de l'application, vous verrez ces logs :

```
✅ Rôle créé: ADMIN
✅ Rôle créé: STOCK_MANAGER
✅ Rôle créé: SALES_MANAGER
✅ Rôle créé: OPERATOR
✅ Rôle créé: SALES_REP
✅ Rôle créé: USER_BASE
✅ Utilisateur créé: admin@gestionstock.com avec le rôle ADMIN
✅ Utilisateur créé: stock.manager@gestionstock.com avec le rôle STOCK_MANAGER
✅ Utilisateur créé: sales.manager@gestionstock.com avec le rôle SALES_MANAGER
✅ Utilisateur créé: operator@gestionstock.com avec le rôle OPERATOR
✅ Utilisateur créé: sales.rep@gestionstock.com avec le rôle SALES_REP
✅ Utilisateur créé: user@gestionstock.com avec le rôle USER_BASE
```
