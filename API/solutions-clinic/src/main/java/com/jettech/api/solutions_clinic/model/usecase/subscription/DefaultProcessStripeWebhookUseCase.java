package com.jettech.api.solutions_clinic.model.usecase.subscription;

import com.jettech.api.solutions_clinic.model.entity.Subscription;
import com.jettech.api.solutions_clinic.model.entity.SubscriptionStatus;
import com.jettech.api.solutions_clinic.model.entity.Tenant;
import com.jettech.api.solutions_clinic.model.entity.TenantStatus;
import com.jettech.api.solutions_clinic.model.repository.SubscriptionRepository;
import com.jettech.api.solutions_clinic.model.repository.TenantRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultProcessStripeWebhookUseCase implements ProcessStripeWebhookUseCase {

    private final SubscriptionRepository subscriptionRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public void execute(ProcessStripeWebhookRequest request) {
        Event event;
        try {
            event = Webhook.constructEvent(request.payload(), request.signature(), request.webhookSecret());
        } catch (SignatureVerificationException e) {
            log.error("Erro ao verificar assinatura do webhook do Stripe", e);
            throw new RuntimeException("Invalid signature", e);
        }

        log.info("Processando evento do Stripe - type: {}, id: {}", event.getType(), event.getId());

        // Processar apenas o evento mais importante: checkout.session.completed
        // Este evento é disparado quando o pagamento é aprovado
        if ("checkout.session.completed".equals(event.getType())) {
            handleCheckoutSessionCompleted(event);
        } else {
            log.debug("Evento não processado: {}", event.getType());
        }
    }

    private void handleCheckoutSessionCompleted(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        Session session = null;
        
        if (dataObjectDeserializer.getObject().isPresent()) {
            Object object = dataObjectDeserializer.getObject().get();
            if (object instanceof Session) {
                session = (Session) object;
            }
        }
        
        if (session == null) {
            log.error("Sessão não encontrada no evento checkout.session.completed");
            return;
        }

        log.info("Processando checkout.session.completed - sessionId: {}", session.getId());

        Subscription subscription = subscriptionRepository.findByStripeCheckoutSessionId(session.getId())
                .orElse(null);

        if (subscription == null) {
            log.warn("Subscription não encontrada para sessionId: {}", session.getId());
            return;
        }

        // Atualizar subscription com informações do Stripe
        if (session.getSubscription() != null) {
            subscription.setStripeSubscriptionId(session.getSubscription());
        }
        if (session.getCustomer() != null) {
            subscription.setStripeCustomerId(session.getCustomer());
        }

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription = subscriptionRepository.save(subscription);

        log.info("Subscription ativada - subscriptionId: {}, tenantId: {}", 
                subscription.getId(), subscription.getTenant().getId());

        // Atualizar tenant
        updateTenantAfterPayment(subscription);
    }

    private void updateTenantAfterPayment(Subscription subscription) {
        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            return;
        }

        Tenant tenant = subscription.getTenant();
        tenant.setPlanType(subscription.getPlanType());
        
        if (tenant.getStatus() == TenantStatus.PENDING_SETUP) {
            tenant.setStatus(TenantStatus.ACTIVE);
        }
        
        tenant.setActive(true);
        tenantRepository.save(tenant);

        log.info("Tenant atualizado após pagamento - tenantId: {}, planType: {}, status: {}", 
                tenant.getId(), tenant.getPlanType(), tenant.getStatus());
    }
}
