package com.demofactory.syscontrol.service.demo;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demofactory.syscontrol.common.ObjResult;
import com.demofactory.syscontrol.common.Result;
import com.demofactory.syscontrol.common.Status;
import com.demofactory.syscontrol.dao.SysUserDao;
import com.demofactory.syscontrol.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;

/**
 * @author : Hanamaru
 * @description: TODO
 * @date : 2020/8/19 17:21
 */
@Slf4j
@Service
public class UserStatusServiceImpl extends ServiceImpl<SysUserDao, SysUser> implements com.demofactory.syscontrol.api.UserStatusService
{
    @Resource
    private SysUserDao sysUserDao;

    @Override
    public ObjResult<String> userStatusUpdate(SysUser sysUser)
    {
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", sysUser.getId());
        sysUserDao.update(sysUser, updateWrapper);
        log.info("result------修改成功");
        return ObjResult.success((sysUser.getStatus() == Status.ENABLE) ?
                "启用成功" : ((sysUser.getStatus() == Status.DISABLE) ? "禁用成功" : "删除成功"));
    }
}
