package com.example.springsimplestorev1.infrastructure.payment;

import com.example.springsimplestorev1.application.port.out.PaymentGateway;
import com.example.springsimplestorev1.domain.exception.PaymentIntegrationException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Component
public class StripePaymentGateway implements PaymentGateway {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String secretKey;

    public StripePaymentGateway(@Value("${payment.stripe.secret-key:}") String secretKey) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.secretKey = secretKey;
    }

    @Override
    public CheckoutSession createCheckoutSession(CreateCheckoutSessionCommand command) {
        if (secretKey == null || secretKey.isBlank()) {
            throw new PaymentIntegrationException("Stripe secret key is not configured");
        }

        StringBuilder body = new StringBuilder();
        appendForm(body, "payment_method_types[0]", "card");
        appendForm(body, "mode", "payment");
        appendForm(body, "success_url", command.successUrl());
        appendForm(body, "cancel_url", command.cancelUrl());
        appendForm(body, "client_reference_id", command.clientReferenceId());

        for (int i = 0; i < command.lineItems().size(); i++) {
            LineItem item = command.lineItems().get(i);
            appendForm(body, "line_items[" + i + "][price_data][currency]", command.currency());
            appendForm(body, "line_items[" + i + "][price_data][unit_amount]", String.valueOf(item.unitAmountCents()));
            appendForm(body, "line_items[" + i + "][price_data][product_data][name]", item.name());
            if (item.imageUrl() != null && !item.imageUrl().isBlank()) {
                appendForm(body, "line_items[" + i + "][price_data][product_data][images][0]", item.imageUrl());
            }
            appendForm(body, "line_items[" + i + "][quantity]", String.valueOf(item.quantity()));
        }

        String responseBody = sendStripeRequest(
                "https://api.stripe.com/v1/checkout/sessions",
                "POST",
                body.toString()
        );

        try {
            JsonNode json = objectMapper.readTree(responseBody);
            return new CheckoutSession(
                    json.path("id").asText(),
                    json.path("url").asText()
            );
        } catch (Exception ex) {
            throw new PaymentIntegrationException("Cannot parse Stripe checkout session response", ex);
        }
    }

    @Override
    public PaymentSession getSession(String sessionId) {
        if (secretKey == null || secretKey.isBlank()) {
            throw new PaymentIntegrationException("Stripe secret key is not configured");
        }

        String responseBody = sendStripeRequest(
                "https://api.stripe.com/v1/checkout/sessions/" + encode(sessionId),
                "GET",
                null
        );

        try {
            JsonNode json = objectMapper.readTree(responseBody);
            return new PaymentSession(
                    json.path("id").asText(),
                    json.path("payment_status").asText(),
                    json.path("client_reference_id").asText()
            );
        } catch (Exception ex) {
            throw new PaymentIntegrationException("Cannot parse Stripe payment session response", ex);
        }
    }

    private String sendStripeRequest(String url, String method, String formBody) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + secretKey);

            if ("POST".equalsIgnoreCase(method)) {
                requestBuilder.header("Content-Type", "application/x-www-form-urlencoded");
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(formBody == null ? "" : formBody));
            } else {
                requestBuilder.GET();
            }

            HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new PaymentIntegrationException("Stripe API error: HTTP " + response.statusCode() + " - " + response.body());
            }
            return response.body();
        } catch (PaymentIntegrationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new PaymentIntegrationException("Stripe API request failed", ex);
        }
    }

    private void appendForm(StringBuilder body, String key, String value) {
        if (body.length() > 0) {
            body.append('&');
        }
        body.append(encode(key)).append('=').append(encode(value));
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
