package com.demofactory.syscontrol.service.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demofactory.syscontrol.api.SysDomainService;
import com.demofactory.syscontrol.common.Result;
import com.demofactory.syscontrol.dao.SysDomainDao;
import com.demofactory.syscontrol.domain.SysDomain;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class SysDomainServiceImpl extends ServiceImpl<SysDomainDao, SysDomain> implements SysDomainService {
    @Resource
    private SysDomainDao sysDomainDao;
    @Override
    public Result selectDomainList() {
        Result result = new Result();
        QueryWrapper<SysDomain> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status",1);
        List<SysDomain> sysDomains = sysDomainDao.selectList(queryWrapper);
        if (sysDomains.isEmpty()){
            return result;
        }
        result.setData(sysDomains);
        result.setSuccess(true);
        return result;
    }
}
