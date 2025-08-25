#!/bin/bash
set -e

echo "🚀 Starting Spring Boot application..."

# Démarrer l'application avec le profil prod
java -jar target/*.jar --spring.profiles.active=prod

echo "✅ Application started!"