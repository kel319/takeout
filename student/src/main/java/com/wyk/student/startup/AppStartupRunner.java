package com.wyk.student.startup;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wyk.student.domain.entity.UserEntity;
import com.wyk.student.domain.vo.DishVo;
import com.wyk.student.interceptor.BloomFilter;
import com.wyk.student.mapper.UserMapper;
import com.wyk.student.mapstruct.DishMapStruct;
import com.wyk.student.service.DishService;
import com.wyk.student.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class AppStartupRunner implements ApplicationRunner {

    private final StringRedisTemplate redisUtils;
    private final UserMapper userMapper;
    private final RedisUtil redisUtil;
    private final DishService dishService;
    private final BloomFilter bloomFilter;
    private final DishMapStruct dishMapStruct;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        long redisStartTime = System.currentTimeMillis();
        String object = null;
        try {
            object = redisUtils.opsForValue().get("DEMO-TEST");
        } catch (Exception e) {
            log.warn("redis连接异常或序列化失败");
        }
        if (object != null) log.info("redis预热成功,总耗时: {} ms",System.currentTimeMillis() - redisStartTime);
        long mvcStartTime = System.currentTimeMillis();
        List<UserEntity> userEntities = userMapper.selectList(new LambdaQueryWrapper<UserEntity>().select(UserEntity::getId)
                .eq(UserEntity::getDeleted, 0));
        List<Long> list = userEntities.stream().map(UserEntity::getId).toList();
        for (Long l : list) {
            bloomFilter.put(l);
        }
        List<DishVo> dish = dishService.getDish();
        if (!dish.isEmpty()) {
            dish.forEach(dishVo -> {
                        String stockKey = "stock:"+dishVo.getId();
                        String cacheKey = "cache:"+dishVo.getId();
                        redisUtil.set(stockKey,dishVo.getInv());
                        redisUtil.set(cacheKey,0);
                    });
        }


        log.info("Spring MVC预热成功,总耗时: {} ms",System.currentTimeMillis() - mvcStartTime);


    }
}
