# Troubleshooting: Stripe CLI não está recebendo eventos

## Problema: Stripe CLI não mostra logs ao fazer pagamento

Se você executa `stripe listen --forward-to http://localhost:8080/v1/subscriptions/webhook` mas não vê eventos sendo encaminhados, siga este guia.

## Diagnóstico Passo a Passo

### 1. Verificar Autenticação do Stripe CLI

O Stripe CLI precisa estar autenticado com a mesma conta do Stripe que você está usando no backend.

**Verificar se está autenticado:**
```bash
stripe config --list
```

**Autenticar novamente (se necessário):**
```bash
stripe login
```

Isso abrirá o navegador para fazer login.

### 2. Verificar se está usando chaves de TESTE

**IMPORTANTE:** O Stripe CLI só captura eventos de contas de TESTE!

**Verificar as chaves que está usando:**
- Backend: As chaves devem começar com `sk_test_` (secret key) e `pk_test_` (publishable key)
- No painel do Stripe: Certifique-se de estar em modo "Test mode" (há um toggle no canto superior direito)

**Verificar chaves no backend:**
```bash
# Windows PowerShell
echo $env:STRIPE_API_SECRET

# Windows CMD
echo %STRIPE_API_SECRET%

# Linux/macOS
echo $STRIPE_API_SECRET
```

A chave deve começar com `sk_test_` (não `sk_live_`).

### 3. Verificar se o Backend está Rodando

O Stripe CLI precisa conseguir acessar o backend.

**Testar se o backend está acessível:**
```bash
# Testar se a porta 8080 está aberta
curl http://localhost:8080/v1/subscriptions/webhook
```

Se não funcionar, verifique:
- O backend está rodando?
- A porta é 8080?
- O endpoint está correto: `/v1/subscriptions/webhook`?

### 4. Verificar o Comando do Stripe CLI

**Comando correto:**
```bash
stripe listen --forward-to http://localhost:8080/v1/subscriptions/webhook
```

**Não use `https://` localmente** - use `http://`:
```bash
# ❌ ERRADO
stripe listen --forward-to https://localhost:8080/v1/subscriptions/webhook

# ✅ CORRETO
stripe listen --forward-to http://localhost:8080/v1/subscriptions/webhook
```

### 5. Verificar Saída do Stripe CLI

Quando você executa `stripe listen`, você deve ver algo como:

```
> Ready! Your webhook signing secret is whsec_xxxxxxxxxxxxx (^C to quit)
```

**Se você não vê esta mensagem:**
- O Stripe CLI não está funcionando corretamente
- Tente reinstalar: `stripe --version`
- Tente logout e login novamente: `stripe logout` depois `stripe login`

### 6. Verificar se está fazendo o Checkout CORRETO

O Stripe CLI só captura eventos de checkouts feitos na mesma conta.

**Verificar:**
1. Você está usando o painel do Stripe em modo TESTE?
2. O checkout está sendo feito com chaves de teste?
3. Você completou o checkout no navegador? (não apenas criou a sessão)

### 7. Testar Evento Manualmente

Para verificar se o Stripe CLI está funcionando, teste manualmente:

```bash
# Em um terminal separado, enquanto o Stripe CLI está rodando:
stripe trigger checkout.session.completed
```

**Se isso funcionar:**
- Você verá o evento no terminal do Stripe CLI
- O backend receberá o evento
- O Stripe CLI está funcionando

**Se isso NÃO funcionar:**
- O Stripe CLI não está conectado corretamente
- Verifique a autenticação novamente

### 8. Verificar Versão do Stripe CLI

Certifique-se de que está usando uma versão atualizada:

```bash
stripe --version
```

**Atualizar (se necessário):**
- Windows: `scoop update stripe`
- macOS: `brew upgrade stripe/stripe-cli/stripe`
- Linux: Baixar nova versão do site

## Checklist Rápido

- [ ] Stripe CLI está autenticado? (`stripe login`)
- [ ] Está usando chaves de TESTE? (`sk_test_...`)
- [ ] Backend está rodando na porta 8080?
- [ ] Comando está correto? (`stripe listen --forward-to http://localhost:8080/v1/subscriptions/webhook`)
- [ ] Vê a mensagem "Ready! Your webhook signing secret is..."?
- [ ] Testou evento manualmente? (`stripe trigger checkout.session.completed`)
- [ ] Está em modo TESTE no painel do Stripe?

## Problemas Comuns

### Problema: "No events received"

**Causa:** Você não completou o checkout no navegador, ou está usando chaves de produção.

**Solução:**
1. Certifique-se de usar chaves de teste
2. Complete o checkout no navegador (não apenas crie a sessão)
3. Verifique se está em modo TESTE no painel

### Problema: "Connection refused"

**Causa:** O backend não está rodando ou não está acessível.

**Solução:**
1. Verifique se o backend está rodando
2. Teste: `curl http://localhost:8080/v1/subscriptions/webhook`
3. Verifique a porta (deve ser 8080)

### Problema: "Invalid signature" no backend

**Causa:** O webhook secret não corresponde ao gerado pelo Stripe CLI.

**Solução:**
1. Quando você inicia `stripe listen`, ele mostra um secret (começa com `whsec_`)
2. Configure este secret na aplicação: `STRIPE_WEBHOOK_SECRET=whsec_...`
3. Reinicie o backend

## Exemplo de Fluxo Correto

**Terminal 1 - Stripe CLI:**
```bash
stripe listen --forward-to http://localhost:8080/v1/subscriptions/webhook
```
Saída esperada:
```
> Ready! Your webhook signing secret is whsec_xxxxxxxxxxxxx (^C to quit)
```

**Terminal 2 - Backend:**
```bash
cd API/solutions-clinic
export STRIPE_WEBHOOK_SECRET=whsec_xxxxxxxxxxxxx  # Cole o secret do Terminal 1
./gradlew bootRun
```

**Terminal 3 - Teste:**
```bash
stripe trigger checkout.session.completed
```

**Resultado esperado:**
- Terminal 1 (Stripe CLI) mostra o evento sendo encaminhado
- Terminal 2 (Backend) mostra "Recebendo webhook do Stripe"
- Terminal 3 mostra "Triggered: checkout.session.completed"

## Se Nada Funcionar

1. **Reinstalar Stripe CLI:**
   ```bash
   # Windows
   scoop uninstall stripe
   scoop install stripe
   
   # macOS
   brew uninstall stripe/stripe-cli/stripe
   brew install stripe/stripe-cli/stripe
   ```

2. **Logout e Login novamente:**
   ```bash
   stripe logout
   stripe login
   ```

3. **Verificar logs do Stripe CLI:**
   - Adicione flag `--verbose` para mais detalhes:
   ```bash
   stripe listen --forward-to http://localhost:8080/v1/subscriptions/webhook --verbose
   ```

4. **Verificar firewall/antivírus:**
   - Pode estar bloqueando conexões locais
   - Tente desabilitar temporariamente para testar
