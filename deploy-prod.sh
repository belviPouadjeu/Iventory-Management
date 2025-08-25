#!/bin/bash

echo "🚀 Déploiement en production avec isdrive.com"

# Arrêter les conteneurs existants
echo "📦 Arrêt des conteneurs..."
docker-compose down

# Construire l'image
echo "🔨 Construction de l'image..."
docker build -t gestionstock-prod .

# Démarrer avec la configuration production
echo "🌐 Démarrage en production..."
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d

echo "✅ Déploiement terminé!"
echo "📊 Application: http://localhost:8082"
echo "📋 Swagger: http://localhost:8082/swagger-ui/index.html"