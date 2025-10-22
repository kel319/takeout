package com.wyk.student.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wyk.student.domain.entity.ComboEntity;
import com.wyk.student.domain.request.ComboRequest;
import com.wyk.student.domain.request.ComboQueryRequest;
import com.wyk.student.domain.vo.ComboVo;
import com.wyk.student.domain.vo.PageVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


public interface ComboService extends IService<ComboEntity> {
    void insertCombo(@Valid ComboRequest request);

    void deleteCombo(@NotNull(message = "删除的id不能为空") Long id);

    void updateCombo(ComboRequest request);

    PageVo<ComboVo> getCombo(Long pageNum,Long pageSize);
    PageVo<ComboVo> getCombo(Long pageNum, Long pageSize, ComboQueryRequest request);
}
