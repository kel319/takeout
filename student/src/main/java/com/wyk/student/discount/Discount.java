package com.wyk.student.discount;

public interface Discount {

    public double applyDiscount(double original,int count,double currentTotal);
    default public DiscountType discountType() {
        return DiscountType.MAIN;
    };

}
