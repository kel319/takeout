package com.wyk.student.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyk.exception.CustomizeException;
import com.wyk.student.domain.entity.DishEntity;
import com.wyk.student.domain.request.DishRequest;
import com.wyk.student.domain.request.TermDishRequest;
import com.wyk.student.domain.vo.DishVo;
import com.wyk.student.domain.vo.GoodsVo;
import com.wyk.student.domain.vo.PageVo;
import com.wyk.student.mapper.DishMapper;
import com.wyk.student.mapstruct.DishMapStruct;
import com.wyk.student.service.DishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class DishServiceImp extends ServiceImpl<DishMapper, DishEntity> implements DishService {

    private final DishMapStruct dishMapStruct;
    @Override
    public void saveDish(DishRequest request) {
        DishEntity dish = Optional.of(request)
                .map(dishMapStruct::requestToEntity)
                .orElseThrow(() -> new CustomizeException("新增菜品失败", HttpStatus.BAD_REQUEST.value()));
        if (dish != null) {
            int i = baseMapper.insertByDishEntity(dish);
            if (i <= 0) log.warn("更新行数为0");
        }
    }

    @Override
    public void deleteById(Long id) {
        Optional.of(id)
                .filter(i -> exists(new LambdaQueryWrapper<DishEntity>().eq(DishEntity::getId,id)))
                .orElseThrow(() -> new CustomizeException("即将被删除的菜品未找到",HttpStatus.BAD_REQUEST.value()));
        this.removeById(id);



    }

    @Override
    public void updateByRequest(DishRequest request) {
        if (request.getId() != null) {
            if (request.getName() != null || request.getPrice() != null || request.getStatus() != null) {
                DishEntity dish = Optional.of(request)
                        .filter(req -> exists(new LambdaQueryWrapper<DishEntity>().eq(DishEntity::getId, req.getId())))
                        .map(dishMapStruct::requestToEntity)
                        .orElseThrow(() -> new CustomizeException("更新的菜品不存在", HttpStatus.BAD_REQUEST.value()));
                baseMapper.updateDishById(dish);
            } else throw new CustomizeException("传入的菜品信息为空", HttpStatus.BAD_REQUEST.value());
        } else throw new CustomizeException("未传入需要更新的菜品ID", HttpStatus.BAD_REQUEST.value());
    }

    @Override
    public PageVo<DishVo> getDish(Long pageNum, Long pageSize) {
        Page<DishEntity> page = baseMapper.selectPage(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<>());
        List<DishVo> dishVos = Optional.of(page)
                .map(Page::getRecords)
                .map(dishMapStruct::entityToVo)
                .orElse(null);
        return PageVo.of(page.getCurrent(), page.getSize(), dishVos, page.getTotal());
    }

    @Override
    public PageVo<DishVo> getDishByRequest(TermDishRequest request, Long pageNum, Long pageSize) {
        Page<DishVo> page = new Page<>(pageNum, pageSize);
/*
        List<DishVo> list = baseMapper.selectList(new LambdaQueryWrapper<DishEntity>()
                .select(DishEntity::getId, DishEntity::getName, DishEntity::getPrice, DishEntity::getInv,
                        DishEntity::getImg, DishEntity::getStatus, DishEntity::getCreateBy)
                .eq(request.getCreateBy() != null, DishEntity::getCreateBy, request.getCreateBy())
                .likeRight(request.getName() != null && !request.getName().trim().isEmpty(), DishEntity::getName, request.getName())
                .eq(request.getStatus() != null, DishEntity::getStatus, request.getStatus())
                .ge(request.getInv() != null && request.getInv() > 0, DishEntity::getInv, 0)
                .eq(request.getInv() != null && request.getInv() == 0, DishEntity::getInv, 0)
                .eq(request.getInv() != null && request.getInv() < 0, DishEntity::getInv, -1)
                .between(request.getPriceMax() != null && request.getPriceMin() != null, DishEntity::getPrice, request.getPriceMin(), request.getPriceMax())
                .ge(request.getPriceMax() == null && request.getPriceMin() != null, DishEntity::getPrice, request.getPriceMin())
                .le(request.getPriceMax() != null && request.getPriceMin() == null, DishEntity::getPrice, request.getPriceMax())
        ).stream().map(dishMapStruct::entityToVo).toList();
*/
        List<DishVo> dishVoList = baseMapper.selectDishVoByRequest(page,request);
        return PageVo.of(page.getCurrent(),page.getSize(),dishVoList,page.getTotal());
    }

    @Override
    public List<DishVo> getDish() {
        return lambdaQuery()
                .select(DishEntity::getId, DishEntity::getInv,DishEntity::getId,DishEntity::getImg,
                        DishEntity::getPrice,DishEntity::getStatus,DishEntity::getCreateBy)
                .list().stream()
                .map(dishMapStruct::entityToVo)
                .toList();
    }

    @Override
    public List<GoodsVo> getDishByComboId(Long comboId) {
        return baseMapper.selectDishByComboId(comboId);
    }
}
