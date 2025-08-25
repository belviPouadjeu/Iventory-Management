#!/bin/bash
set -e  # Quitte le script si une commande échoue

echo "🔨 Building Spring Boot application for Render..."

# Installer Maven si nécessaire
if ! command -v mvn &> /dev/null; then
    echo "📥 Installing Maven..."
    apt-get update
    apt-get install -y maven
fi

# Compiler l'application
echo "📦 Compiling application..."
mvn clean package -DskipTests

echo "✅ Build completed!"
