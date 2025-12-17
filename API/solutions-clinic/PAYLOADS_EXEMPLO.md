# Payloads de Exemplo para Testes

## POST /v1/auth/signup/clinic-owner

### Payload Completo (todos os campos)

```json
{
  "firstName": "João",
  "lastName": "Silva",
  "email": "joao.silva@clinicamedica.com.br",
  "password": "senhaSegura123",
  "name": "Clínica Médica São Paulo",
  "cnpj": "12345678000190",
  "planType": "Premium",
  "address": "Rua das Flores, 123, Centro, São Paulo - SP",
  "phone": "(11) 98765-4321",
  "subdomain": "clinicamedica-sp"
}
```

### Payload Mínimo (apenas campos obrigatórios)

```json
{
  "firstName": "Maria",
  "lastName": "Santos",
  "email": "maria.santos@clinicaexemplo.com",
  "password": "minhasenha123",
  "name": "Clínica Exemplo",
  "cnpj": "98765432000123",
  "subdomain": "clinica-exemplo"
}
```

### Exemplo 2 - Clínica com dados completos

```json
{
  "firstName": "Carlos",
  "lastName": "Oliveira",
  "email": "carlos.oliveira@saudeplus.com.br",
  "password": "senhaForte@2024",
  "name": "Saúde Plus Clínica",
  "cnpj": "11223344000155",
  "planType": "Enterprise",
  "address": "Av. Paulista, 1000, Bela Vista, São Paulo - SP, CEP: 01310-100",
  "phone": "(11) 3456-7890",
  "subdomain": "saudeplus"
}
```

---

## POST /v1/auth/signup/solo

### Payload Completo (todos os campos)

```json
{
  "firstName": "Ana",
  "lastName": "Costa",
  "email": "ana.costa@doutorana.com.br",
  "password": "senhaSegura456",
  "name": "Dr. Ana Costa - Cardiologia",
  "cpf": "12345678901",
  "planType": "Básico",
  "address": "Rua dos Médicos, 456, Jardim das Flores, Rio de Janeiro - RJ",
  "phone": "(21) 99876-5432",
  "subdomain": "doutorana"
}
```

### Payload Mínimo (apenas campos obrigatórios)

```json
{
  "firstName": "Pedro",
  "lastName": "Ferreira",
  "email": "pedro.ferreira@medico.com",
  "password": "minhasenha456",
  "name": "Dr. Pedro Ferreira",
  "cpf": "98765432100",
  "subdomain": "dr-pedro"
}
```

### Exemplo 2 - Profissional solo completo

```json
{
  "firstName": "Juliana",
  "lastName": "Martins",
  "email": "juliana.martins@psicologa.com.br",
  "password": "senhaForte@2024",
  "name": "Juliana Martins - Psicologia Clínica",
  "cpf": "11122233344",
  "planType": "Profissional",
  "address": "Rua da Saúde, 789, Centro, Belo Horizonte - MG",
  "phone": "(31) 98765-4321",
  "subdomain": "juliana-martins"
}
```

---

## Exemplos de Teste com cURL

### Teste 1: Cadastro de Clínica

```bash
curl -X POST http://localhost:8080/v1/auth/signup/clinic-owner \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "João",
    "lastName": "Silva",
    "email": "joao.silva@clinicamedica.com.br",
    "password": "senhaSegura123",
    "name": "Clínica Médica São Paulo",
    "cnpj": "12345678000190",
    "planType": "Premium",
    "address": "Rua das Flores, 123, Centro, São Paulo - SP",
    "phone": "(11) 98765-4321",
    "subdomain": "clinicamedica-sp"
  }'
```

### Teste 2: Cadastro de Profissional Solo

```bash
curl -X POST http://localhost:8080/v1/auth/signup/solo \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Ana",
    "lastName": "Costa",
    "email": "ana.costa@doutorana.com.br",
    "password": "senhaSegura456",
    "name": "Dr. Ana Costa - Cardiologia",
    "cpf": "12345678901",
    "planType": "Básico",
    "address": "Rua dos Médicos, 456, Jardim das Flores, Rio de Janeiro - RJ",
    "phone": "(21) 99876-5432",
    "subdomain": "doutorana"
  }'
```

---

## Resposta Esperada (Sucesso)

```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "tenantId": "660e8400-e29b-41d4-a716-446655440000",
  "email": "joao.silva@clinicamedica.com.br",
  "tenantName": "Clínica Médica São Paulo",
  "subdomain": "clinicamedica-sp"
}
```

---

## Validações Importantes

### Para `/v1/auth/signup/clinic-owner`:
- ✅ `firstName`: 2-50 caracteres, obrigatório
- ✅ `lastName`: 2-50 caracteres, obrigatório
- ✅ `email`: formato válido, obrigatório, único
- ✅ `password`: mínimo 8 caracteres, obrigatório
- ✅ `name`: 2-100 caracteres, obrigatório
- ✅ `cnpj`: exatamente 14 dígitos, obrigatório, único
- ✅ `subdomain`: 3-64 caracteres, apenas letras minúsculas, números e hífens, obrigatório, único
- ⚪ `planType`: máximo 100 caracteres, opcional
- ⚪ `address`: máximo 200 caracteres, opcional
- ⚪ `phone`: máximo 20 caracteres, opcional

### Para `/v1/auth/signup/solo`:
- ✅ `firstName`: 2-50 caracteres, obrigatório
- ✅ `lastName`: 2-50 caracteres, obrigatório
- ✅ `email`: formato válido, obrigatório, único
- ✅ `password`: mínimo 8 caracteres, obrigatório
- ✅ `name`: 2-100 caracteres, obrigatório
- ✅ `cpf`: exatamente 11 dígitos, obrigatório
- ✅ `subdomain`: 3-64 caracteres, apenas letras minúsculas, números e hífens, obrigatório, único
- ⚪ `planType`: máximo 100 caracteres, opcional
- ⚪ `address`: máximo 200 caracteres, opcional
- ⚪ `phone`: máximo 20 caracteres, opcional

---

## Exemplos de Erros

### Email já existe
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email já está em uso: joao.silva@clinicamedica.com.br"
}
```

### CNPJ já existe (apenas para clinic-owner)
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "CNPJ já está em uso: 12345678000190"
}
```

### Subdomain já existe
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Subdomínio já está em uso: clinicamedica-sp"
}
```

### Validação de campo (exemplo: password muito curta)
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "O campo [password] deve ter no mínimo 8 caracteres"
}
```

