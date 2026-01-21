#!/bin/bash

echo "========================================"
echo "  Solutions Clinic - Docker Start"
echo "========================================"
echo ""

# Verifica se .env existe
if [ ! -f .env ]; then
    echo "[!] Arquivo .env não encontrado. Copiando .env.example..."
    cp .env.example .env
    echo "[OK] Arquivo .env criado. Configure as variáveis se necessário."
    echo ""
fi

# Inicia os containers
echo "[*] Iniciando containers..."
docker-compose up -d --build

echo ""
echo "========================================"
echo "  Serviços iniciados!"
echo "========================================"
echo ""
echo "  PostgreSQL: localhost:5432"
echo "  Backend:    http://localhost:8080"
echo "  Frontend:   http://localhost:3000"
echo ""
echo "  Para ver logs: docker-compose logs -f"
echo "  Para parar:    docker-compose down"
echo "========================================"
