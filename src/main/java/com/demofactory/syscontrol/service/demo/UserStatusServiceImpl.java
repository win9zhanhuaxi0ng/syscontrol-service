package com.demofactory.syscontrol.service.demo;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demofactory.syscontrol.dao.UserStatusDao;
import com.demofactory.syscontrol.domain.SysUser;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;

/**
 * @author : Hanamaru
 * @description: TODO
 * @date : 2020/8/19 17:21
 */
@Service
public class UserStatusServiceImpl extends ServiceImpl<UserStatusDao, SysUser> implements com.demofactory.syscontrol.api.UserStatusService {
    @Resource
    private UserStatusDao userStatusDao;

    @Override
    public String userStatusUpdate(SysUser sysUser)
    {
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("account",sysUser.getAccount());
        userStatusDao.update(sysUser,updateWrapper);
        return "修改成功";
    }
}