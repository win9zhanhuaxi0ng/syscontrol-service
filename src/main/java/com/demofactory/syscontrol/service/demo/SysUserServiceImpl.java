package com.demofactory.syscontrol.service.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demofactory.syscontrol.api.SysUserService;
import com.demofactory.syscontrol.dao.SysUserDao;
import com.demofactory.syscontrol.domain.SysUser;
import org.apache.dubbo.config.annotation.Service;


import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserDao,SysUser> implements SysUserService {

    @Resource
    private SysUserDao sysUserDao;

    /**
     * 登录功能根据传入的账号，查询数据库判断password和status
     * @param sysUser 用户信息包含账号、密码
     * @return null登录失败 sysUser登录成功
     */
    @Override
    public SysUser login(SysUser sysUser) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account",sysUser.getAccount());
        SysUser sysUser1= sysUserDao.selectOne(queryWrapper);
        if (sysUser1==null){
            return null;
        }
        if (sysUser1.getPassword().equals(sysUser.getPassword())){
                return sysUser1;
        }
        return null;
    }

    /**
     * 注册功能，根据传入的账号对数据库查找是否已存在，否，则判断两次密码是否一致，是，则插入
     * @param sysUser 用户信息包含账号、密码、提示语
     * @param secondaryPwd 二次密码校验
     * @return 0二次密码校验失败 1插入成功 2用户已存在
     */
    @Override
    public int register(SysUser sysUser,String secondaryPwd) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account",sysUser.getAccount());
        SysUser sysUser1= sysUserDao.selectOne(queryWrapper);
        if(sysUser1==null){
            if (sysUser.getPassword().equals(secondaryPwd)){
                sysUserDao.insert(sysUser);
                return 1;
            }
            return -2;
        }
        return -1;
    }

    @Override
    public void updateLastLoginTime(SysUser sysUser) {
        sysUser.setLastLoginTime(LocalDateTime.now());
        sysUserDao.updateById(sysUser);
    }

    @Override
    public LocalDateTime findLastLoginTime(String account) {
        return null;
    }

    @Override
    public String SelectAccountOrHint(SysUser sysUser)
    {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account",sysUser.getAccount());
        queryWrapper.eq("pwd_hint",sysUser.getPwdHint());
        List<SysUser> sysUsers = null;

        return (sysUserDao.selectCount(queryWrapper)>0)?"跳转为重设密码页面":"账号错误或提示语错误";

    }
}
