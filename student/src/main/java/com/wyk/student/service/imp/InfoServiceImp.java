package com.wyk.student.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wyk.student.domain.entity.InfoEntity;
import com.wyk.student.mapper.InfoMapper;
import com.wyk.student.service.InfoService;
import org.springframework.stereotype.Service;

@Service
public class InfoServiceImp extends ServiceImpl<InfoMapper, InfoEntity> implements InfoService {

}
