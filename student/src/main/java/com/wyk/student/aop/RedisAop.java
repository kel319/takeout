package com.wyk.student.aop;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.wyk.exception.CustomizeException;
import com.wyk.student.cache.CacheMissHandler;
import com.wyk.student.cache.imp.ExceptionHandler;
import com.wyk.student.interceptor.BloomFilter;
import com.wyk.student.util.RedisUtil;
import com.wyk.student.util.SpELUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.DigestUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor

public class RedisAop {
    @Pointcut("@annotation(redisInterface)")
    public void redisCachePointcut(RedisInterface redisInterface){}
    private static final String NULL_VALUE = "__NULL__";
    private static final ConcurrentHashMap<String,Object> localLock = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String,ReentrantLock> reentrantLock = new ConcurrentHashMap<>();
    private final SpELUtil spELUtil;
    private final RedisUtil redisUtil;
    private final Map<String, CacheMissHandler> missHandler;
    private final ExceptionHandler defaultMissHandler;
    private final BloomFilter bloomFilter;

    @Around("redisCachePointcut(redisInterface)")
    public Object redisAop(ProceedingJoinPoint joinPoint,RedisInterface redisInterface) throws Throwable {
        String key = generateRedisKey(joinPoint, redisInterface);
        return switch (redisInterface.redisModel()) {
            case QUERY -> queryOrInsert(key,joinPoint,redisInterface);
            case UPDATE -> updateOrDelete(key,joinPoint,redisInterface);
        };
    }

    //生成key
    private String generateRedisKey(ProceedingJoinPoint joinPoint,RedisInterface redisInterface) {
        String key = spELUtil.parseSpEL(redisInterface.key(), redisInterface.defaultVal(), joinPoint);
        if (key == null) return null;
        return String.format("%s::%s",redisInterface.value(),
                DigestUtils.md5DigestAsHex(key.getBytes(StandardCharsets.UTF_8)));
//                key);
    }
    //查询或插入操作逻辑
    private Object queryOrInsert(String key,ProceedingJoinPoint joinPoint,RedisInterface redisInterface) throws Throwable {
        JavaType javaType = getJavaType(joinPoint);
        if (redisInterface.bloomKey() != null && !redisInterface.bloomKey().trim().isEmpty()) {
            Object o = spELUtil.parseBloomSpEL(redisInterface.bloomKey(), redisInterface.defaultVal(), joinPoint);
            Optional<Object> optionalBloom = bloomFilterHandler(o, redisInterface.handler(), key, javaType);
            if (optionalBloom.isPresent()) return optionalBloom.get();
        }
        Optional<Object> optionalRedis = redisGetHandler(redisInterface.handler(), key, javaType);
        if (optionalRedis.isPresent()) return optionalRedis.get();


        //分布式锁方案

        String value = UUID.randomUUID().toString();
        if (redisUtil.setDistributedLock(key, value, 3)) {
            try {
                log.debug("获取锁成功,key: {},value: {}",key,value);
                //这里是业务逻辑
            } finally {
                if (redisUtil.delDistributedLock(key,value)) {
                    log.debug("锁释放成功,key: {},value: {}",key,value);
                }
            }
        } else {
//            throw new CustomizeException("获取锁失败",HttpStatus.BAD_REQUEST.value());
            log.warn("获取锁失败");
        }



        //缓存击穿防护（缓存key级别锁）
//        Object lock = localLock.computeIfAbsent(key, k -> new Object());
//        synchronized (lock) {
//            try {
//                //重试
//                Object object = redisUtil.get(key, javaType);
//                if (Optional.ofNullable(object).filter(o -> !NULL_VALUE.equals(o)).isPresent())
//                    return object;
//                Object proceed = joinPoint.proceed();
//                if (TransactionSynchronizationManager.isSynchronizationActive()) {
//                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//                        @Override
//                        public void afterCommit() {
//                            redisSetHandler(proceed, key);
//                        }
//                    });
//                } else {
//                    redisSetHandler(proceed, key);
//                }
//                return proceed;
//            } finally {
//                localLock.remove(key);
//            }
//        }

        ReentrantLock lock2 = reentrantLock.computeIfAbsent(key, k -> new ReentrantLock());
        if (lock2.tryLock(2, TimeUnit.SECONDS)) {
            try {
                Object object = redisUtil.get(key, javaType);
                if (Optional.ofNullable(object).filter(o -> !NULL_VALUE.equals(o)).isPresent())
                    return object;
                Object proceed = joinPoint.proceed();
                if (TransactionSynchronizationManager.isSynchronizationActive()) {
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            redisSetHandler(proceed, key);
                        }
                    });
                } else {
                    redisSetHandler(proceed, key);
                }
                return proceed;
            } finally {
                if (!lock2.hasQueuedThreads())
                    reentrantLock.remove(key);
                lock2.unlock();
            }
        } else {
            log.warn("获取锁 {} 失败",key);
            throw new CustomizeException("服务器繁忙,请稍后重试",HttpStatus.BAD_REQUEST.value());
        }
    }
    //更新或删除逻辑
    private Object updateOrDelete(String key ,ProceedingJoinPoint joinPoint, RedisInterface redisInterface) throws Throwable {
        Object proceed = joinPoint.proceed();
        redisUtil.remove(key);
        if (proceed != null) {
            redisUtil.setDefault(key,proceed);
        }
        return proceed;
    }
    //获取方法返回值javaType
    private JavaType getJavaType(ProceedingJoinPoint joinPoint) {
        Type returnType = Optional.of(joinPoint)
                .map(ProceedingJoinPoint::getSignature)
                .filter(MethodSignature.class::isInstance)
                .map(MethodSignature.class::cast)
                .map(MethodSignature::getMethod)
                .map(Method::getGenericReturnType)
                .orElseThrow(() -> new CustomizeException("获取方法返回值失败", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        return TypeFactory.defaultInstance().constructType(returnType);
    }

    //查布隆
    private Optional<Object> bloomFilterHandler(Object o,String handler,String key,JavaType javaType) {
        if (o instanceof List<?> s) {
            boolean b = s.stream().anyMatch(list -> list instanceof Long l && bloomFilter.mightContain(l));
            if(!b) {
                log.debug("布隆过滤器未找到List: {},直接结束!", s);
                return Optional.ofNullable(resolveHandler(handler).handle(key, javaType));
            }
        } else if (o instanceof Long l) {
            if (!bloomFilter.mightContain(l)) {
                log.debug("布隆过滤器未找到Long: {},直接结束!", l);
                return Optional.ofNullable(resolveHandler(handler).handle(key, javaType));
            }
        }
        return Optional.empty();
    }

    //查缓存
    private Optional<Object> redisGetHandler(String handler,String key,JavaType javaType) {
        Object redisResult = redisUtil.get(key, javaType);
        if(Optional.ofNullable(redisResult)
                .filter(o -> !NULL_VALUE.equals(o))
                .isPresent()) return Optional.of(redisResult);
        else if (NULL_VALUE.equals(redisResult)) {
            log.info("空缓存,直接结束!");
            return Optional.ofNullable(resolveHandler(handler).handle(key, javaType));
        }
        return Optional.empty();
    }

    private void redisSetHandler(Object proceed,String key) {
        if (proceed == null) redisUtil.set(key);
        else redisUtil.setDefault(key, proceed);
    }
    //策略选择
    private CacheMissHandler resolveHandler(String handlerName) {
        return missHandler.getOrDefault(handlerName,defaultMissHandler);
    }
}
