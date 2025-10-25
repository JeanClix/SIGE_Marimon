#!/bin/bash
# Script para ejecutar ktlint localmente y verificar el estilo del código

echo "🔍 Ejecutando ktlint check..."
./gradlew ktlintCheck

if [ $? -eq 0 ]; then
    echo "✅ ktlint check: PASSED"
else
    echo "❌ ktlint check: FAILED"
    echo ""
    echo "💡 Para arreglar automáticamente los errores de estilo, ejecuta:"
    echo "   ./gradlew ktlintFormat"
    exit 1
fi
