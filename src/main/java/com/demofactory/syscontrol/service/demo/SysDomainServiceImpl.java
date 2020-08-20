package com.demofactory.syscontrol.service.demo;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demofactory.syscontrol.api.SysDomainService;
import com.demofactory.syscontrol.dao.SysDomainDao;
import com.demofactory.syscontrol.domain.SysDomain;
import org.apache.dubbo.config.annotation.Service;


@Service
public class SysDomainServiceImpl extends ServiceImpl<SysDomainDao, SysDomain> implements SysDomainService {
}
