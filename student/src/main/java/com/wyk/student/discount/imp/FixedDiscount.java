package com.wyk.student.discount.imp;

import com.wyk.student.discount.Discount;

public class FixedDiscount implements Discount {
    private final double discountRate;

    public FixedDiscount(double discountRate) {
        this.discountRate = discountRate;
    }
    public double fixedDiscount(double currentTotal) {
        return currentTotal * discountRate;
    }

    @Override
    public double applyDiscount(double original, int count,double currentTotal) {
        return fixedDiscount(currentTotal);
    }
}
