package com.wyk.student.discount;

public class DiscountTest {


    public double shop(double price, int count) {
        return DiscountContext.builder()
                .addFixedDiscount(0.8)
                .addFullDiscount(50,10)
                .build().calculateFinalPrice(price, count);
    }


}
