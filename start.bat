@echo off
echo ========================================
echo   Solutions Clinic - Docker Start
echo ========================================
echo.

:: Verifica se .env existe
if not exist .env (
    echo [!] Arquivo .env nao encontrado. Copiando .env.example...
    copy .env.example .env
    echo [OK] Arquivo .env criado. Configure as variaveis se necessario.
    echo.
)

:: Inicia os containers
echo [*] Iniciando containers...
docker-compose up -d --build

echo.
echo ========================================
echo   Servicos iniciados!
echo ========================================
echo.
echo   PostgreSQL: localhost:5432
echo   Backend:    http://localhost:8080
echo   Frontend:   http://localhost:3000
echo.
echo   Para ver logs: docker-compose logs -f
echo   Para parar:    docker-compose down
echo ========================================
