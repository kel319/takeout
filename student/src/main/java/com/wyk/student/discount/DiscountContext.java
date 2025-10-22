package com.wyk.student.discount;

import com.wyk.student.discount.imp.BuyNGetOneDiscount;
import com.wyk.student.discount.imp.FixedDiscount;
import com.wyk.student.discount.imp.FullDiscount;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class DiscountContext {

    private final List<Discount> discounts;

    public DiscountContext(List<Discount> discounts) {
        this.discounts = discounts != null ? discounts : List.of();
    }

    public double calculateFinalPrice(double price, int count) {
        double result = price * count;
        if (discounts.isEmpty()) return result;
        Map<DiscountType, List<Discount>> discountTypeListMap = discounts.stream()
                .collect(Collectors.groupingBy(Discount::discountType));
        for (DiscountType discountType : DiscountType.values()) {
            List<Discount> discountList = discountTypeListMap.get(discountType);
            for (Discount discount : discountList) {
                result = discount.applyDiscount(price,count,result);
            }
        }
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<Discount> discounts = new ArrayList<>();

        public Builder() {
        }

        public Builder addDiscount(Discount discount) {
            this.discounts.add(discount);
            return this;
        }
        public Builder addFixedDiscount(double fixedDiscount) {
            this.discounts.add(new FixedDiscount(fixedDiscount));
            return this;
        }
        public Builder addFullDiscount(double full, double price) {
            this.discounts.add(new FullDiscount(full,price));
            return this;
        }
        public Builder addBuyNGetOneDiscount(double buyThreshold) {
            this.discounts.add(new BuyNGetOneDiscount(buyThreshold));
            return this;
        }
        public DiscountContext build() {
            return new DiscountContext(this.discounts);
        }
    }

}
