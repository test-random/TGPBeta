package org.telegram.ui.Components.Premium;

import com.android.billingclient.api.ProductDetails;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.BuildVars;
import org.telegram.tgnet.TLRPC;

public final class GiftPremiumBottomSheet$GiftTier {
    private int discount;
    public final TLRPC.TL_premiumGiftCodeOption giftCodeOption;
    public final TLRPC.TL_premiumGiftOption giftOption;
    public ProductDetails googlePlayProductDetails;
    private long pricePerMonth;
    private long pricePerMonthRegular;

    public GiftPremiumBottomSheet$GiftTier(TLRPC.TL_premiumGiftCodeOption tL_premiumGiftCodeOption) {
        this.giftOption = null;
        this.giftCodeOption = tL_premiumGiftCodeOption;
    }

    public GiftPremiumBottomSheet$GiftTier(TLRPC.TL_premiumGiftOption tL_premiumGiftOption) {
        this.giftOption = tL_premiumGiftOption;
        this.giftCodeOption = null;
    }

    public String getCurrency() {
        if (this.giftOption != null) {
            if (BuildVars.useInvoiceBilling() || this.giftOption.store_product == null) {
                return this.giftOption.currency;
            }
        } else if (this.giftCodeOption != null && (BuildVars.useInvoiceBilling() || this.giftCodeOption.store_product == null)) {
            return this.giftCodeOption.currency;
        }
        ProductDetails productDetails = this.googlePlayProductDetails;
        return productDetails == null ? "" : productDetails.getOneTimePurchaseOfferDetails().getPriceCurrencyCode();
    }

    public int getDiscount() {
        if (this.discount == 0) {
            if (getPricePerMonth() == 0) {
                return 0;
            }
            if (this.pricePerMonthRegular != 0) {
                double pricePerMonth = getPricePerMonth();
                double d = this.pricePerMonthRegular;
                Double.isNaN(pricePerMonth);
                Double.isNaN(d);
                int i = (int) ((1.0d - (pricePerMonth / d)) * 100.0d);
                this.discount = i;
                if (i == 0) {
                    this.discount = -1;
                }
            }
        }
        return this.discount;
    }

    public String getFormattedPrice() {
        TLRPC.TL_premiumGiftOption tL_premiumGiftOption;
        TLRPC.TL_premiumGiftCodeOption tL_premiumGiftCodeOption;
        return (BuildVars.useInvoiceBilling() || ((tL_premiumGiftOption = this.giftOption) != null && tL_premiumGiftOption.store_product == null) || ((tL_premiumGiftCodeOption = this.giftCodeOption) != null && tL_premiumGiftCodeOption.store_product == null)) ? BillingController.getInstance().formatCurrency(getPrice(), getCurrency()) : this.googlePlayProductDetails == null ? "" : BillingController.getInstance().formatCurrency(getPrice(), getCurrency(), 6);
    }

    public int getMonths() {
        TLRPC.TL_premiumGiftOption tL_premiumGiftOption = this.giftOption;
        if (tL_premiumGiftOption != null) {
            return tL_premiumGiftOption.months;
        }
        TLRPC.TL_premiumGiftCodeOption tL_premiumGiftCodeOption = this.giftCodeOption;
        if (tL_premiumGiftCodeOption != null) {
            return tL_premiumGiftCodeOption.months;
        }
        return 1;
    }

    public long getPrice() {
        if (this.giftOption != null) {
            if (BuildVars.useInvoiceBilling() || this.giftOption.store_product == null) {
                return this.giftOption.amount;
            }
        } else if (this.giftCodeOption != null && (BuildVars.useInvoiceBilling() || this.giftCodeOption.store_product == null)) {
            return this.giftCodeOption.amount;
        }
        ProductDetails productDetails = this.googlePlayProductDetails;
        if (productDetails == null) {
            return 0L;
        }
        return productDetails.getOneTimePurchaseOfferDetails().getPriceAmountMicros();
    }

    public long getPricePerMonth() {
        if (this.pricePerMonth == 0) {
            long price = getPrice();
            if (price != 0) {
                this.pricePerMonth = price / getMonths();
            }
        }
        return this.pricePerMonth;
    }

    public String getStoreProduct() {
        TLRPC.TL_premiumGiftOption tL_premiumGiftOption = this.giftOption;
        if (tL_premiumGiftOption != null) {
            return tL_premiumGiftOption.store_product;
        }
        TLRPC.TL_premiumGiftCodeOption tL_premiumGiftCodeOption = this.giftCodeOption;
        if (tL_premiumGiftCodeOption != null) {
            return tL_premiumGiftCodeOption.store_product;
        }
        return null;
    }

    public void setGooglePlayProductDetails(ProductDetails productDetails) {
        this.googlePlayProductDetails = productDetails;
    }

    public void setPricePerMonthRegular(long j) {
        this.pricePerMonthRegular = j;
    }
}
