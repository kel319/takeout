package com.wyk.student.discount.imp;

import com.wyk.student.discount.Discount;

public class BuyNGetOneDiscount implements Discount {

    private final double buyThreshold;

    public BuyNGetOneDiscount(double buyThreshold) {
        this.buyThreshold = buyThreshold;
    }

    private double twoForOne(double original, int count, double currentTotal) {
        return count > buyThreshold ? currentTotal - original : currentTotal;
    }

    @Override
    public double applyDiscount(double original, int count, double currentTotal) {
        return twoForOne(original,count,currentTotal);
    }
}
