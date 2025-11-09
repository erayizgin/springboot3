package com.pluralsight.ecommerce.dto;

public class StripeResponse {
    private String checkoutUrl;

    public StripeResponse(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    public StripeResponse() {
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }
}
