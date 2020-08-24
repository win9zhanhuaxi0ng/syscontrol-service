package com.demofactory.syscontrol.service.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demofactory.syscontrol.api.OrgStatusService;
import com.demofactory.syscontrol.common.Result;
import com.demofactory.syscontrol.dao.SysDomainDao;
import com.demofactory.syscontrol.dao.SysOrgDao;
import com.demofactory.syscontrol.dao.SysUserDao;
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
 * @date : 2020/8/20 11:18
 */
@Slf4j
@Service
public class OrgStatusServiceImpl extends ServiceImpl<SysOrgDao, SysOrg> implements OrgStatusService {
    @Resource
    private SysDomainDao sysDomainDao;

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
        log.info("result------成功");
        return (sysOrg.getStatus()==1)?
                "启用成功":((sysOrg.getStatus()==2)?"禁用成功":"删除成功");
    }


    @Override
    public List<SysOrg> selectSysOrg(SysOrg sysOrg){
        List<SysOrg> sysOrgs = null;
        QueryWrapper<SysOrg> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!Objects.isNull(sysOrg.getId()),"id",sysOrg.getId());
        queryWrapper.eq(!Objects.isNull(sysOrg.getDomainId()),"domain_id",sysOrg.getDomainId());
        sysOrgs = sysOrgDao.selectList(queryWrapper);

        return sysOrgs;
    }

    @Override
    public Result insertSysOrg(SysOrg sysOrg)
    {
        QueryWrapper<SysOrg> queryWrapper =new QueryWrapper<>();
        queryWrapper.eq("domain_id",sysOrg.getDomainId());
        queryWrapper.eq("org_name",sysOrg.getOrgName());

        QueryWrapper<SysDomain> queryWrapper1 =new QueryWrapper<>();
        queryWrapper1.eq("id",sysOrg.getDomainId());

        if(sysDomainDao.selectCount(queryWrapper1)==0)
        {
            log.info("result-----不存在该域");
            return Result.failure("不存在该域");
        }

        if(sysOrgDao.selectCount(queryWrapper)>0)
        {
            log.info("result------已存在该机构");
            return Result.failure("已存在该机构");
        }
        sysOrgDao.insert(sysOrg);
        log.info("result-------插入成功");
        return Result.OK("插入成功");
    }
}
