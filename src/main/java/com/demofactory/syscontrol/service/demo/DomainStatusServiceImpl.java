package com.demofactory.syscontrol.service.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demofactory.syscontrol.common.Result;
import com.demofactory.syscontrol.dao.*;
import com.demofactory.syscontrol.domain.SysDomain;
import com.demofactory.syscontrol.domain.SysOrg;
import com.demofactory.syscontrol.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author : Hanamaru
 * @description: TODO
 * @date : 2020/8/20 13:19
 */
@Slf4j
@Service
public class DomainStatusServiceImpl extends ServiceImpl<SysDomainDao, SysDomain> implements com.demofactory.syscontrol.api.DomainStatusService {
    @Resource
    private SysDomainDao sysDomainDao;

    @Resource
    private SysOrgDao sysOrgDao;

    @Resource
    private SysUserDao sysUserDao;

    @Override
    public String domainUpdate(SysDomain sysDomain) {
        UpdateWrapper<SysDomain> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", sysDomain.getId());
        sysDomainDao.update(sysDomain, updateWrapper);

        SysOrg sysOrg = new SysOrg();
        sysOrg.setStatus(sysDomain.getStatus());
        UpdateWrapper<SysOrg> updateWrapper1 = new UpdateWrapper<>();
        updateWrapper1.eq("domain_id", sysDomain.getId());
        sysOrgDao.update(sysOrg, updateWrapper1);

        SysUser sysUser = new SysUser();
        sysUser.setStatus(sysDomain.getStatus());
        UpdateWrapper<SysUser> updateWrapper2 = new UpdateWrapper<>();
        updateWrapper2.eq("domain_id", sysDomain.getId());
        sysUserDao.update(sysUser, updateWrapper2);
        log.info("result------修改成功");
        return (sysDomain.getStatus()==1)?
                "启用成功":((sysDomain.getStatus()==2)?"禁用成功":"删除成功");
    }
    @Override
    public List<SysDomain> selectSysDomain(SysDomain sysDomain){
        List<SysDomain> sysDomains = null;
        QueryWrapper<SysDomain> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!Objects.isNull(sysDomain.getId()),"id",sysDomain.getId());
        sysDomains =sysDomainDao.selectList(queryWrapper);
        return sysDomains;
    }
    @Override
    public Result insertSysDomain(SysDomain sysDomain){
        QueryWrapper<SysDomain> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",sysDomain.getName());
        if (sysDomainDao.selectCount(queryWrapper)>0){
            log.info("result------已存在该域");
            return Result.failure("已存在该域");
        }
        sysDomainDao.insert(sysDomain);
        log.info("result------插入成功");
        return Result.OK("插入成功");
    }
}
