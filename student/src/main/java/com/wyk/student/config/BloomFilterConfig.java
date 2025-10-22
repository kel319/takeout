package com.wyk.student.config;

import com.wyk.student.interceptor.BloomFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicLongArray;

@Configuration
public class BloomFilterConfig {

    @Bean
    public BloomFilter bloomFilter() {
        int size = 1000000;
        int hashCount = 5;
        int arrayLength = (size+63)/64;
        AtomicLongArray atomicLongArray = new AtomicLongArray(arrayLength);
        return new BloomFilter(atomicLongArray,size,hashCount);
    }
}
