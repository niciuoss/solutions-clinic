# Guia: Configurando Webhooks do Stripe Localmente

## Problema

O Stripe não consegue acessar `localhost` diretamente. Para testar webhooks localmente durante o desenvolvimento, você precisa usar o **Stripe CLI** para criar um túnel que encaminha os eventos do Stripe para sua aplicação local.

## Solução: Usar Stripe CLI

### 1. Instalar o Stripe CLI

**Windows (PowerShell):**
```powershell
# Usando Scoop
scoop install stripe

# Ou baixar direto do site
# https://stripe.com/docs/stripe-cli
```

**macOS:**
```bash
brew install stripe/stripe-cli/stripe
```

**Linux:**
```bash
# Download direto
wget https://github.com/stripe/stripe-cli/releases/latest/download/stripe_*_linux_x86_64.tar.gz
tar -xvf stripe_*_linux_x86_64.tar.gz
sudo mv stripe /usr/local/bin
```

### 2. Autenticar no Stripe CLI

```bash
stripe login
```

Isso abrirá seu navegador para fazer login na sua conta do Stripe.

### 3. Executar o Stripe CLI para ouvir eventos

Em um terminal separado, execute:

```bash
stripe listen --forward-to localhost:8080/v1/subscriptions/webhook
```

**⚠️ IMPORTANTE:** O comando acima irá gerar um **webhook signing secret** na saída. Você verá algo como:

```
> Ready! Your webhook signing secret is whsec_xxxxxxxxxxxxx (^C to quit)
```

### 4. Configurar o Webhook Secret na Aplicação

Você tem duas opções:

#### Opção A: Usar Variável de Ambiente (Recomendado)

1. Copie o webhook secret gerado pelo Stripe CLI (começa com `whsec_`)
2. Configure como variável de ambiente:

**Windows (PowerShell):**
```powershell
$env:STRIPE_WEBHOOK_SECRET="whsec_xxxxxxxxxxxxx"
```

**Windows (CMD):**
```cmd
set STRIPE_WEBHOOK_SECRET=whsec_xxxxxxxxxxxxx
```

**macOS/Linux:**
```bash
export STRIPE_WEBHOOK_SECRET=whsec_xxxxxxxxxxxxx
```

#### Opção B: Atualizar application.yaml (Apenas para desenvolvimento)

**⚠️ NÃO RECOMENDADO PARA PRODUÇÃO** - Use apenas para testes locais:

Edite `API/solutions-clinic/src/main/resources/application.yaml`:

```yaml
stripe:
  webhook:
    secret: ${STRIPE_WEBHOOK_SECRET:whsec_SEU_SECRET_AQUI}
```

### 5. Iniciar sua Aplicação Backend

Certifique-se de que o backend está rodando na porta 8080:

```bash
cd API/solutions-clinic
./gradlew bootRun
```

### 6. Testar o Webhook

1. **Execute o Stripe CLI** em um terminal (passo 3)
2. **Inicie o backend** em outro terminal (passo 5)
3. **Inicie o frontend** em outro terminal:
   ```bash
   cd Front/front-web-clinic
   npm run dev
   ```
4. **Faça uma assinatura de teste** no frontend
5. **Observe os logs** no terminal do Stripe CLI - você verá os eventos sendo encaminhados

## Eventos Esperados vs Recebidos

### ⚠️ IMPORTANTE: Eventos que o código processa

O código **só processa** estes eventos específicos:

- ✅ `checkout.session.completed` - **OBRIGATÓRIO** - Ativa a subscription após pagamento
- ✅ `customer.subscription.updated` - Atualiza informações da subscription
- ✅ `customer.subscription.deleted` - Cancela a subscription

### ⚠️ Eventos que chegam mas não são processados

Estes eventos são normais e fazem parte do fluxo do Stripe, mas **não ativam** subscriptions:

- `charge.succeeded` - Cobrança bem-sucedida
- `payment_intent.succeeded` - Intenção de pagamento bem-sucedida
- `payment_intent.created` - Intenção de pagamento criada

**Estes eventos NÃO ativam a subscription!** Apenas o evento `checkout.session.completed` ativa a subscription.

### ✅ Como garantir que o evento `checkout.session.completed` seja disparado:

1. **Complete o checkout no navegador:**
   - Você será redirecionado para o Stripe Checkout
   - Preencha os dados do cartão: `4242 4242 4242 4242`
   - **CLIQUE em "Pagar" ou "Confirmar"**
   - Aguarde o redirecionamento para `/plan-selection/success`

2. **Verifique os logs:**
   - Você deve ver: `Processando checkout.session.completed - sessionId: cs_test_xxx`
   - E depois: `Subscription ativada`

## Resolução de Problemas

### Eventos estão chegando mas subscription não é ativada

**Causa:** O evento `checkout.session.completed` não está sendo recebido.

**Solução:** 
- Verifique se você completou o checkout no navegador (clicou em "Pagar")
- O evento `checkout.session.completed` só é disparado quando o checkout é realmente completado
- Veja mais detalhes em `STRIPE_WEBHOOK_TROUBLESHOOTING.md`

### Erro: "Invalid signature"

**Causa:** O webhook secret na aplicação não corresponde ao secret gerado pelo Stripe CLI.

**Solução:**
1. Pare o Stripe CLI (Ctrl+C)
2. Reinicie com `stripe listen --forward-to localhost:8080/v1/subscriptions/webhook`
3. Copie o novo secret gerado
4. Atualize a variável de ambiente `STRIPE_WEBHOOK_SECRET`
5. Reinicie o backend

### Erro: "Connection refused"

**Causa:** O backend não está rodando ou está em uma porta diferente.

**Solução:**
1. Verifique se o backend está rodando na porta 8080
2. Verifique se o endereço no comando `stripe listen` está correto

### Webhook não está recebendo eventos

**Verificações:**
1. ✅ Stripe CLI está rodando?
2. ✅ Backend está rodando na porta correta?
3. ✅ Webhook secret está configurado corretamente?
4. ✅ Endpoint `/v1/subscriptions/webhook` está acessível?
5. ✅ Verifique os logs do Stripe CLI para ver se os eventos estão sendo encaminhados

### Testar Eventos Manualmente

Você pode testar eventos manualmente usando o Stripe CLI:

```bash
# Testar evento checkout.session.completed
stripe trigger checkout.session.completed

# Testar evento customer.subscription.updated
stripe trigger customer.subscription.updated

# Testar evento customer.subscription.deleted
stripe trigger customer.subscription.deleted
```

## Diferenças entre Desenvolvimento e Produção

### Desenvolvimento Local (usando Stripe CLI)
- **Webhook Secret:** Gerado pelo Stripe CLI (começa com `whsec_`)
- **URL do Webhook:** `localhost:8080/v1/subscriptions/webhook`
- **Como funciona:** Stripe CLI cria um túnel e encaminha eventos

### Produção
- **Webhook Secret:** Configurado no painel do Stripe (começa com `whsec_`)
- **URL do Webhook:** `https://seu-dominio.com/v1/subscriptions/webhook`
- **Como funciona:** Stripe envia eventos diretamente via HTTPS

## Fluxo Completo de Teste Local

1. **Terminal 1 - Stripe CLI:**
   ```bash
   stripe listen --forward-to localhost:8080/v1/subscriptions/webhook
   ```
   - Copie o webhook secret gerado

2. **Terminal 2 - Backend:**
   ```bash
   cd API/solutions-clinic
   export STRIPE_WEBHOOK_SECRET=whsec_xxxxxxxxxxxxx  # Cole o secret do terminal 1
   ./gradlew bootRun
   ```

3. **Terminal 3 - Frontend:**
   ```bash
   cd Front/front-web-clinic
   npm run dev
   ```

4. **No navegador:**
   - Acesse a página de seleção de plano
   - Faça uma assinatura de teste
   - Use cartão de teste: `4242 4242 4242 4242`

5. **Observar logs:**
   - Terminal 1 (Stripe CLI): Verá os eventos sendo encaminhados
   - Terminal 2 (Backend): Verá os logs de processamento do webhook

## Cartões de Teste do Stripe

Use estes cartões para testar pagamentos:

- **Sucesso:** `4242 4242 4242 4242`
- **Falha:** `4000 0000 0000 0002`
- **Requer autenticação 3D Secure:** `4000 0025 0000 3155`

**Outros dados de teste:**
- Data de validade: Qualquer data futura (ex: `12/25`)
- CVC: Qualquer 3 dígitos (ex: `123`)
- CEP: Qualquer CEP válido (ex: `12345-678`)
