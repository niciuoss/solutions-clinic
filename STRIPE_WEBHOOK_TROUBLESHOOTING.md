# Resolução de Problemas: Webhooks do Stripe

## Problema: Webhook não está processando eventos

### Sintomas

Você vê nos logs que eventos estão chegando, mas a subscription não está sendo ativada:
```
INFO - Processando evento do Stripe - type: charge.succeeded, id: evt_xxx
INFO - Processando evento do Stripe - type: payment_intent.succeeded, id: evt_xxx
```

Mas não vê o evento `checkout.session.completed` sendo processado.

### Causa

O código só processa eventos específicos:
- ✅ `checkout.session.completed` - **OBRIGATÓRIO** para ativar a subscription
- ✅ `customer.subscription.updated` - Atualiza informações da subscription
- ✅ `customer.subscription.deleted` - Cancela a subscription

Outros eventos (como `charge.succeeded`, `payment_intent.succeeded`) são recebidos mas **não processados** pelo código.

### Solução

**O evento `checkout.session.completed` é disparado quando você COMPLETA o checkout no navegador.**

#### Passos para garantir que o checkout seja completado:

1. **Crie uma sessão de checkout** (já está funcionando - você vê nos logs: "Sessão de checkout criada com sucesso")

2. **Complete o checkout no navegador:**
   - Você será redirecionado para o Stripe Checkout
   - Preencha os dados do cartão de teste: `4242 4242 4242 4242`
   - Preencha data de validade (qualquer data futura): `12/25`
   - Preencha CVC: `123`
   - Preencha CEP: `12345-678`
   - **CLIQUE em "Pagar" ou "Confirmar"**

3. **Aguarde o redirecionamento:**
   - Após completar o pagamento, você será redirecionado para `/plan-selection/success`
   - O evento `checkout.session.completed` será disparado neste momento

4. **Verifique os logs:**
   - Você deve ver: `Processando checkout.session.completed - sessionId: cs_test_xxx`
   - E depois: `Subscription ativada - subscriptionId: xxx, tenantId: xxx`

### Testando Eventos Manualmente

Se você quiser testar o evento `checkout.session.completed` sem completar um checkout real, use o Stripe CLI:

```bash
stripe trigger checkout.session.completed
```

Isso disparará o evento e você deve ver nos logs:
```
INFO - Processando checkout.session.completed - sessionId: cs_test_xxx
```

**Nota:** Este teste manual pode não funcionar se não houver uma subscription no banco com o sessionId correspondente.

### Verificando se o Checkout foi Completado

1. **Verifique no painel do Stripe:**
   - Acesse https://dashboard.stripe.com/test/payments
   - Veja se há pagamentos completados

2. **Verifique os logs do Stripe CLI:**
   - No terminal onde está rodando `stripe listen`, você verá os eventos sendo encaminhados
   - Procure por `checkout.session.completed`

3. **Verifique os logs do backend:**
   - Procure por: `Processando checkout.session.completed`

### Eventos que o Código Processa vs Não Processa

#### ✅ Eventos Processados (ativam/atualizam subscriptions):
- `checkout.session.completed` - **Principal!** Ativa a subscription
- `customer.subscription.updated` - Atualiza informações da subscription
- `customer.subscription.deleted` - Cancela a subscription

#### ⚠️ Eventos Recebidos mas Não Processados:
- `charge.succeeded` - Cobrança bem-sucedida (interno do Stripe)
- `payment_intent.succeeded` - Intenção de pagamento bem-sucedida (interno)
- `payment_intent.created` - Intenção de pagamento criada (interno)
- Outros eventos do Stripe

Estes eventos são normais e fazem parte do fluxo do Stripe, mas **não são usados** pelo código para ativar subscriptions.

### Checklist de Troubleshooting

- [ ] Stripe CLI está rodando? (`stripe listen --forward-to localhost:8080/v1/subscriptions/webhook`)
- [ ] Backend está rodando na porta 8080?
- [ ] Webhook secret está configurado corretamente?
- [ ] Você completou o checkout no navegador (clicou em "Pagar")?
- [ ] Você vê o evento `checkout.session.completed` nos logs?
- [ ] Se não vê, verifique se o checkout foi realmente completado no painel do Stripe
