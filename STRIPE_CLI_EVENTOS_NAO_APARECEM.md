# Problema: Stripe CLI não mostra eventos mesmo com tudo configurado

## Situação

- ✅ Stripe CLI mostra "Ready! Your webhook signing secret is..."
- ✅ `stripe login` foi executado
- ✅ Usando chaves de teste (`sk_test_...`)
- ✅ Checkout foi completado no navegador
- ❌ Eventos NÃO aparecem no terminal do Stripe CLI

## Possíveis Causas

### 1. **Webhook configurado no painel do Stripe está interceptando eventos**

**Causa:** Se você configurou um webhook no painel do Stripe (em "Developers" > "Webhooks"), os eventos podem estar sendo enviados diretamente para esse webhook em vez de serem capturados pelo Stripe CLI.

**Solução:**
1. Acesse o painel do Stripe: https://dashboard.stripe.com/test/webhooks
2. Verifique se há webhooks configurados
3. **Para desenvolvimento local:** Desative ou exclua os webhooks configurados no painel
4. **OU:** Configure o Stripe CLI para usar a mesma conta

**Importante:** O Stripe CLI funciona como um "webhook local" que captura eventos. Se você tem um webhook configurado no painel, os eventos podem estar indo para o webhook do painel em vez do Stripe CLI.

### 2. **Checkout está sendo feito em uma conta/projeto diferente**

**Causa:** O checkout pode estar sendo feito com chaves de uma conta diferente da autenticada no Stripe CLI.

**Solução:**
1. Verifique se as chaves no backend (`STRIPE_API_SECRET`) são da mesma conta que está autenticada no Stripe CLI
2. Verifique no painel do Stripe qual conta está sendo usada: https://dashboard.stripe.com/test/apikeys
3. Compare as chaves no backend com as chaves mostradas no painel

### 3. **Eventos estão sendo enviados direto para o backend (sem passar pelo Stripe CLI)**

**Causa:** Se você configurou um webhook no painel do Stripe apontando para `localhost:8080`, os eventos podem estar indo direto para o backend (via túnel ou outra ferramenta).

**Solução:**
1. Verifique se há webhooks no painel: https://dashboard.stripe.com/test/webhooks
2. **Remova ou desative webhooks que apontam para localhost**
3. Use apenas o Stripe CLI para desenvolvimento local

### 4. **Checkout não foi realmente completado**

**Causa:** Pode parecer que o checkout foi completado, mas pode ter falhado ou não ter sido processado.

**Verificação:**
1. Verifique no painel do Stripe: https://dashboard.stripe.com/test/payments
2. Veja se há um pagamento bem-sucedido com o ID da sessão de checkout
3. Verifique os logs do backend - há eventos chegando?

### 5. **Stripe CLI não está "escutando" a conta correta**

**Causa:** Pode haver múltiplas contas do Stripe e o CLI está autenticado em uma diferente.

**Solução:**
1. Execute `stripe logout`
2. Execute `stripe login` novamente
3. Certifique-se de fazer login na mesma conta que está usando no backend

## Diagnóstico Passo a Passo

### Passo 1: Verificar Webhooks no Painel

1. Acesse: https://dashboard.stripe.com/test/webhooks
2. **Se houver webhooks configurados:**
   - Clique em cada webhook
   - Verifique a URL
   - **Para desenvolvimento local:** Desative ou exclua temporariamente
3. **Importante:** Webhooks no painel têm prioridade sobre o Stripe CLI

### Passo 2: Verificar Conta/Projeto

1. No painel do Stripe, vá em: https://dashboard.stripe.com/test/apikeys
2. Copie a "Secret key" (deve começar com `sk_test_`)
3. Compare com a chave no seu backend:
   ```bash
   # Windows PowerShell
   echo $env:STRIPE_API_SECRET
   
   # Windows CMD
   echo %STRIPE_API_SECRET%
   ```
4. **Devem ser IDÊNTICAS!**

### Passo 3: Verificar se Eventos Estão Chegando no Backend

1. Verifique os logs do backend
2. Você vê mensagens como "Recebendo webhook do Stripe"?
3. Se SIM: Os eventos estão chegando direto no backend (webhook no painel está interceptando)
4. Se NÃO: Os eventos não estão sendo gerados ou estão indo para outro lugar

### Passo 4: Testar Evento Manualmente

Enquanto o Stripe CLI está rodando, em outro terminal:

```bash
stripe trigger checkout.session.completed
```

**O que deve acontecer:**
- Terminal do Stripe CLI: Mostra evento sendo encaminhado
- Backend: Recebe o evento

**Se isso funcionar:**
- O Stripe CLI está funcionando corretamente
- O problema é que eventos do checkout não estão sendo capturados
- Provável causa: Webhook no painel está interceptando

**Se isso NÃO funcionar:**
- Há um problema com o Stripe CLI ou backend
- Verifique conexão/porta/firewall

### Passo 5: Verificar Logs do Stripe CLI com Verbose

Execute o Stripe CLI com mais detalhes:

```bash
stripe listen --forward-to http://localhost:8080/v1/subscriptions/webhook --verbose
```

Isso mostrará mais informações sobre conexões e eventos.

## Solução Mais Provável

Baseado na situação descrita, a causa mais provável é:

**Webhook configurado no painel do Stripe está interceptando os eventos antes do Stripe CLI.**

### Como Resolver:

1. **Acesse o painel do Stripe:** https://dashboard.stripe.com/test/webhooks
2. **Desative ou exclua webhooks que apontam para localhost ou desenvolvimento**
3. **Para desenvolvimento local, use APENAS o Stripe CLI**
4. **Para produção, configure webhooks no painel**

### Configuração Recomendada:

**Desenvolvimento Local:**
- ✅ Stripe CLI rodando: `stripe listen --forward-to http://localhost:8080/v1/subscriptions/webhook`
- ❌ NENHUM webhook no painel do Stripe

**Produção:**
- ❌ Stripe CLI não usado
- ✅ Webhook no painel apontando para `https://seu-dominio.com/v1/subscriptions/webhook`

## Teste Final

Depois de remover webhooks do painel:

1. Execute o Stripe CLI: `stripe listen --forward-to http://localhost:8080/v1/subscriptions/webhook`
2. Faça um novo checkout
3. Complete o checkout no navegador
4. **Agora você deve ver eventos no terminal do Stripe CLI!**
