# Payloads de Teste

Esta pasta contém exemplos de payloads JSON para testar os endpoints de cadastro.

## Como usar

### Com cURL

#### Teste 1: Cadastro de Clínica (completo)
```bash
curl -X POST http://localhost:8080/v1/auth/signup/clinic-owner \
  -H "Content-Type: application/json" \
  -d @clinic-owner-exemplo1.json
```

#### Teste 2: Cadastro de Clínica (mínimo)
```bash
curl -X POST http://localhost:8080/v1/auth/signup/clinic-owner \
  -H "Content-Type: application/json" \
  -d @clinic-owner-exemplo2.json
```

#### Teste 3: Cadastro de Profissional Solo (completo)
```bash
curl -X POST http://localhost:8080/v1/auth/signup/solo \
  -H "Content-Type: application/json" \
  -d @solo-exemplo1.json
```

#### Teste 4: Cadastro de Profissional Solo (mínimo)
```bash
curl -X POST http://localhost:8080/v1/auth/signup/solo \
  -H "Content-Type: application/json" \
  -d @solo-exemplo2.json
```

### Com Postman/Insomnia

1. Importe os arquivos JSON como body da requisição
2. Configure o método como `POST`
3. Configure a URL:
   - Para clínica: `http://localhost:8080/v1/auth/signup/clinic-owner`
   - Para solo: `http://localhost:8080/v1/auth/signup/solo`
4. Configure o header: `Content-Type: application/json`
5. Cole o conteúdo do arquivo JSON no body

### Com HTTPie

```bash
# Clínica
http POST localhost:8080/v1/auth/signup/clinic-owner < clinic-owner-exemplo1.json

# Solo
http POST localhost:8080/v1/auth/signup/solo < solo-exemplo1.json
```

## Importante

⚠️ **Altere os valores de email, CNPJ, CPF e subdomain antes de testar**, pois eles devem ser únicos no banco de dados.

Se você tentar usar os mesmos valores duas vezes, receberá um erro de validação.

