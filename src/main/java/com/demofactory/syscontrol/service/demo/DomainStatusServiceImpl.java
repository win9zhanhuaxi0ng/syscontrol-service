package com.demofactory.syscontrol.service.demo;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demofactory.syscontrol.dao.DomainDao;
import com.demofactory.syscontrol.dao.OrgDao;
import com.demofactory.syscontrol.dao.SysUserDao;
import com.demofactory.syscontrol.domain.SysDomain;
import com.demofactory.syscontrol.domain.SysOrg;
import com.demofactory.syscontrol.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;

/**
 * @author : Hanamaru
 * @description: TODO
 * @date : 2020/8/20 13:19
 */
@Slf4j
@Service
public class DomainStatusServiceImpl extends ServiceImpl<DomainDao,SysDomain> implements com.demofactory.syscontrol.api.DomainStatusService {
    @Resource
    private DomainDao domainDao;

    @Resource
    private OrgDao orgDao;

    @Resource
    private SysUserDao sysUserDao;

    @Override
    public String domainUpdate(SysDomain sysDomain)
    {
        UpdateWrapper<SysDomain> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",sysDomain.getId());
        domainDao.update(sysDomain,updateWrapper);

        SysOrg sysOrg = new SysOrg();
        sysOrg.setStatus(sysDomain.getStatus());
        UpdateWrapper<SysOrg> updateWrapper1 = new UpdateWrapper<>();
        updateWrapper1.eq("domain_id",sysDomain.getId());
        orgDao.update(sysOrg,updateWrapper1);

        SysUser sysUser = new SysUser();
        sysUser.setStatus(sysDomain.getStatus());
        UpdateWrapper<SysUser> updateWrapper2 = new UpdateWrapper<>();
        updateWrapper2.eq("domain_id",sysDomain.getId());
        sysUserDao.update(sysUser,updateWrapper2);
        log.info("result------修改成功");
        return "修改成功";
    }
}
