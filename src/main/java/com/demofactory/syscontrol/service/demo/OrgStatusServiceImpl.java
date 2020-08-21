package com.demofactory.syscontrol.service.demo;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demofactory.syscontrol.api.OrgStatusService;
import com.demofactory.syscontrol.dao.SysOrgDao;
import com.demofactory.syscontrol.dao.SysUserDao;
import com.demofactory.syscontrol.domain.SysOrg;
import com.demofactory.syscontrol.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;

/**
 * @author : Hanamaru
 * @description: TODO
 * @date : 2020/8/20 11:18
 */
@Slf4j
@Service
public class OrgStatusServiceImpl extends ServiceImpl<SysOrgDao, SysOrg> implements OrgStatusService {
    @Resource
    private SysOrgDao sysOrgDao;

    @Resource
    private SysUserDao sysUserDao;

    @Override
    public String orgStatusUpdate(SysOrg sysOrg) {
        UpdateWrapper<SysOrg> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", sysOrg.getId());
        sysOrgDao.update(sysOrg, updateWrapper);
        SysUser sysUser = new SysUser();
        sysUser.setStatus(sysOrg.getStatus());
        UpdateWrapper<SysUser> updateWrapper1 = new UpdateWrapper<>();
        updateWrapper1.eq("org_id", sysOrg.getId());
        sysUserDao.update(sysUser, updateWrapper1);
        log.info("result------修改成功");
        return "修改成功";
    }
}
