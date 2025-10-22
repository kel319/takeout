package com.wyk.student.service.imp;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyk.exception.CustomizeException;
import com.wyk.student.domain.entity.ComboEntity;
import com.wyk.student.domain.request.ComboRequest;
import com.wyk.student.domain.request.ComboQueryRequest;
import com.wyk.student.domain.vo.ComboDishVo;
import com.wyk.student.domain.vo.ComboVo;
import com.wyk.student.domain.vo.DishVo;
import com.wyk.student.domain.vo.PageVo;
import com.wyk.student.mapper.ComboMapper;
import com.wyk.student.mapstruct.ComboMapStruct;
import com.wyk.student.service.ComboService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ComboServiceImp extends ServiceImpl<ComboMapper, ComboEntity> implements ComboService {

    private final ComboMapStruct comboMapStruct;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertCombo(ComboRequest request) {
        ComboEntity comboEntity = comboMapStruct.requestToEntity(request);
        checkRows(baseMapper.insertCombo(comboEntity),"新增套餐表时失败,将回滚");
        request.setId(comboEntity.getId());
        if (!request.getComboDishes().isEmpty()) {
            checkRows(baseMapper.insertComboAndDish(request),"新增套餐_菜品表时失败,将回滚");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCombo(Long id) {
        Optional.of(id)
                .map(this::getById)
                .orElseThrow(() -> new CustomizeException("被删除的套餐不存在",HttpStatus.BAD_REQUEST.value()));
        checkRows(baseMapper.deleteComboDishByComboId(id),"删除套餐_菜品表时失败,将回滚");
        checkRows(baseMapper.delete(new LambdaQueryWrapper<ComboEntity>()
                .eq(ComboEntity::getId, id)),"删除套餐表时失败,将回滚");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCombo(ComboRequest request) {
        if (!request.getComboDishes().isEmpty()) {
            baseMapper.deleteComboDishByComboId(request.getId());
            checkRows(baseMapper.insertComboAndDish(request),"插入套餐_菜品表时失败,将回滚");
        }
        if (request.getName() != null || request.getPrice() != null || request.getStatus() != null) {
            ComboEntity comboEntity = comboMapStruct.requestToEntity(request);
            checkRows(baseMapper.updateCombo(comboEntity),"更新套餐表时失败,将回滚");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageVo<ComboVo> getCombo(Long pageNum,Long pageSize) {
        return getPageVo(new Page<>(pageNum,pageSize),new LambdaQueryWrapper<>());
    }

    @Override
    @Transactional(readOnly = true)
    public PageVo<ComboVo> getCombo(Long pageNum, Long pageSize, ComboQueryRequest request) {
        return getPageVo(new Page<>(pageNum,pageSize),ComboQueryRequest.getWrapper(request));
    }


    private void checkRows(int row, String message) {
        if (row < 1) throw new CustomizeException(message,HttpStatus.BAD_REQUEST.value());
    }

    private PageVo<ComboVo> getPageVo(Page<ComboEntity> page, Wrapper<ComboEntity> wrapper) {
        List<ComboVo> comboVoList = baseMapper.selectPage(page, wrapper)
                .getRecords().stream().map(comboMapStruct::entityToVo).toList();
        List<Long> comboIds = comboVoList.stream().map(ComboVo::getId).toList();
        Map<Long, List<ComboDishVo>> comboIdToDishMap = baseMapper.selectComboDishByComboId(comboIds)
                .stream().collect(Collectors.groupingBy(ComboDishVo::getComboId));
        comboVoList.forEach(comboVo -> comboVo.setComboDishVos(comboIdToDishMap.getOrDefault(comboVo.getId(),List.of())));
        return PageVo.of(page.getCurrent(),page.getSize(),comboVoList,page.getTotal());
    }
}
