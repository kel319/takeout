package com.wyk.student.discount.imp;

import com.wyk.student.discount.Discount;

public class FullDiscount implements Discount {
    private final double full;
    private final double price;

    public FullDiscount(double full, double price) {
        this.full = full;
        this.price = price;
    }

    public double fullReduction(double full, double price,double currentTotal) {
        if (currentTotal >= full) return currentTotal - price;
        return currentTotal;
    }

    @Override
    public double applyDiscount(double original, int count, double currentTotal) {
        return fullReduction(full,price,currentTotal);
    }
}
