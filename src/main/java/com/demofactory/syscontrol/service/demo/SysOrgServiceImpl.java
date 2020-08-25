package com.demofactory.syscontrol.service.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demofactory.syscontrol.api.SysOrgService;
import com.demofactory.syscontrol.common.Result;
import com.demofactory.syscontrol.dao.SysOrgDao;
import com.demofactory.syscontrol.domain.SysDomain;
import com.demofactory.syscontrol.domain.SysOrg;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysOrgServiceImpl extends ServiceImpl<SysOrgDao, SysOrg> implements SysOrgService {

    @Resource
    private SysOrgDao sysOrgDao;

    @Override
    public Result selectOrgListByDomain(SysDomain sysDomain) {
        Result result  = new Result();
        QueryWrapper<SysOrg> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("domain_id",sysDomain.getId());
        queryWrapper.eq("status",1);
        List<SysOrg> sysOrgs = sysOrgDao.selectList(queryWrapper);
        if (sysOrgs.isEmpty()){
            result.setMessage("查找结果为空");
            return result;
        }
        result.setData(sysOrgs);
        result.setSuccess(true);
        return result;
    }
}
