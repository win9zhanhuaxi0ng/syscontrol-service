package com.demofactory.syscontrol.service.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demofactory.syscontrol.api.AssignUserService;
import com.demofactory.syscontrol.dao.AssignUserDao;
import com.demofactory.syscontrol.domain.SysUser;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : Hanamaru
 * @description: TODO
 * @date : 2020/8/19 13:50
 */
@Service
public class AssignUserServiceImpl extends ServiceImpl<AssignUserDao, SysUser> implements AssignUserService {
    @Resource
    private AssignUserDao assignUserDao;

    @Override
    public List<SysUser> selectAssignUser( SysUser sysUser)
    {
        List<SysUser> sysUsers = null;
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(sysUser.getDomainId()!=null,"domain_id",sysUser.getDomainId());
        queryWrapper.eq(sysUser.getOrgId()!=null,"org_id",sysUser.getOrgId());
        sysUsers = assignUserDao.selectList(queryWrapper);
        return sysUsers;
    }

    @Override
    public String updateAssignUser(SysUser sysUser)
    {
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("account",sysUser.getAccount());
        assignUserDao.update(sysUser,updateWrapper);
        return "修改成功";
    }

}
