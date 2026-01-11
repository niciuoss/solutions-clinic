# Integração Stripe - Gateway de Pagamento

## Resumo

A integração do Stripe como gateway de pagamento foi implementada para processar os pagamentos dos planos selecionados pelos tenants. A solução utiliza o Stripe Checkout (a forma mais simples) e webhooks para processar pagamentos aprovados.

## Componentes Implementados

### Backend (Spring Boot)

1. **Entidades:**
   - `Subscription` - Rastreia assinaturas e pagamentos
   - `SubscriptionStatus` - Enum para status das assinaturas (PENDING, ACTIVE, CANCELED, PAST_DUE, UNPAID)

2. **Repositories:**
   - `SubscriptionRepository` - Acesso aos dados de assinaturas

3. **Use Cases:**
   - `CreateCheckoutSessionUseCase` - Cria sessão de checkout do Stripe
   - `ProcessStripeWebhookUseCase` - Processa eventos do webhook do Stripe

4. **Controllers:**
   - `TenantController` - Endpoint para criar checkout (`POST /v1/tenants/{tenantId}/checkout`)
   - `SubscriptionController` - Webhook do Stripe (`POST /v1/subscriptions/webhook`)

5. **Configuração:**
   - `StripeConfig` - Configuração da API do Stripe
   - `application.yaml` - Variáveis de ambiente para Stripe

### Frontend (Next.js)

1. **Actions:**
   - `createCheckoutSessionAction` - Cria sessão de checkout

2. **Componentes:**
   - `PlanSelection.tsx` - Atualizado para usar Stripe Checkout
   - `plan-selection/success/page.tsx` - Página de confirmação após pagamento

3. **Routes:**
   - `/plan-selection/success` - Página de sucesso do pagamento

## Configuração Necessária

### Variáveis de Ambiente (Backend)

Adicione as seguintes variáveis de ambiente no `application.yaml` ou nas variáveis de ambiente do sistema:

```yaml
stripe:
  api:
    key: ${STRIPE_API_KEY:}        # Chave pública do Stripe
    secret: ${STRIPE_API_SECRET:}   # Chave secreta do Stripe
  webhook:
    secret: ${STRIPE_WEBHOOK_SECRET:}  # Secret do webhook do Stripe
    success:
      path: ${STRIPE_WEBHOOK_SUCCESS_PATH:/plan-selection/success}
    cancel:
      path: ${STRIPE_WEBHOOK_CANCEL_PATH:/plan-selection}

app:
  frontend:
    url: ${FRONTEND_URL:http://localhost:3000}
```

### Configuração no Stripe

1. **Criar conta no Stripe:**
   - Acesse https://stripe.com
   - Crie uma conta (ou use a conta de testes)

2. **Obter chaves de API:**
   - No painel do Stripe, vá em "Developers" > "API keys"
   - Copie a "Publishable key" (para usar no frontend se necessário)
   - Copie a "Secret key" (para usar como `STRIPE_API_SECRET`)

3. **Configurar Webhook:**
   - No painel do Stripe, vá em "Developers" > "Webhooks"
   - Clique em "Add endpoint"
   - URL do endpoint: `https://seu-dominio.com/v1/subscriptions/webhook`
   - Eventos a serem ouvidos:
     - `checkout.session.completed` (obrigatório) - quando o pagamento é aprovado
     - `customer.subscription.updated` (recomendado) - quando a subscription é atualizada/renovada
     - `customer.subscription.deleted` (recomendado) - quando a subscription é cancelada
   - Copie o "Signing secret" (para usar como `STRIPE_WEBHOOK_SECRET`)

## Fluxo de Pagamento

1. **Seleção do Plano:**
   - O tenant seleciona um plano (BASIC ou PRO) na página `/plan-selection`
   - O frontend chama `createCheckoutSessionAction`
   - O backend cria uma sessão de checkout do Stripe

2. **Checkout:**
   - O usuário é redirecionado para o checkout do Stripe
   - O usuário preenche os dados de pagamento
   - O Stripe processa o pagamento

3. **Webhook (Pagamento Aprovado):**
   - Quando o pagamento é aprovado, o Stripe envia um evento `checkout.session.completed`
   - O webhook recebe o evento e:
     - Atualiza a subscription para `ACTIVE`
     - Atualiza o tenant para `ACTIVE`
     - Define o `planType` do tenant

4. **Redirecionamento:**
   - Após o pagamento, o usuário é redirecionado para `/plan-selection/success`
   - A página aguarda o processamento do webhook
   - Redireciona para o dashboard após confirmação

## Preços dos Planos

Os preços estão configurados no `DefaultCreateCheckoutSessionUseCase`:

- **BASIC:** R$ 299,00/mês
- **PRO:** R$ 599,00/mês
- **CUSTOM:** Não pode ser pago via checkout (requer contato com vendas)

## Segurança

- O webhook valida a assinatura do Stripe usando o `Stripe-Signature` header
- O endpoint do webhook está configurado como `permitAll()` no SecurityConfig (necessário para o Stripe acessar)
- Todas as comunicações devem usar HTTPS em produção

## Testes

Para testar em desenvolvimento:

1. Use as chaves de teste do Stripe (começam com `pk_test_` e `sk_test_`)
2. Use o Stripe CLI para testar webhooks localmente:
   ```bash
   stripe listen --forward-to localhost:8080/v1/subscriptions/webhook
   ```
3. Use cartões de teste do Stripe:
   - Sucesso: `4242 4242 4242 4242`
   - Falha: `4000 0000 0000 0002`

## Eventos Processados

O sistema processa os seguintes eventos do Stripe:

1. **checkout.session.completed**
   - Disparado quando o pagamento é aprovado
   - Ativa a subscription e o tenant
   - Salva o stripeSubscriptionId e stripeCustomerId

2. **customer.subscription.updated**
   - Disparado quando a subscription é atualizada ou renovada
   - Atualiza `currentPeriodStart` e `currentPeriodEnd` (datas de vencimento)
   - Atualiza o status da subscription baseado no status do Stripe
   - Permite rastrear quando o plano vence

3. **customer.subscription.deleted**
   - Disparado quando a subscription é cancelada
   - Marca a subscription como `CANCELED`
   - Salva a data de cancelamento (`canceledAt`)
   - Desativa o tenant

## Próximos Passos (Opcional)

- Implementar upgrade/downgrade de planos
- Adicionar página de gerenciamento de assinatura
- Implementar notificações por email após pagamento
- Adicionar logs mais detalhados para auditoria
- Implementar sincronização periódica com Stripe para garantir dados atualizados