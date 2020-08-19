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
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUser> implements SysUserService {

    @Resource
    private SysUserDao sysUserDao;


    @Override
    public SysUser loginByAccountAndPassword(String account, String password) {

        SysUser sysUser = sysUserDao.findByAccount(account);
        if (sysUser == null) {
            return null;
        }
        if (sysUser.getPassword().equals(password)) {

            return sysUser;
        }
        return null;
    }

    @Override
    public int registerSysUser(String account, String password, String secondaryPwd, String pwdHint) {
        //查询账号密码是否为空，为空返回0
        if (account.equals("") || password.equals("") || pwdHint.equals("")) {
            return 0;
        }
        //查询账号是否存在，存在则返回-1
        if (sysUserDao.findByAccount(account) != null) {
            return -1;
        }
        //判断两次输入密码是否一致，不一致则返回-2
        if (!password.equals(secondaryPwd)) {
            return -2;
        }
        //否则，注册成功返回1
        sysUserDao.insertNewSysUser(account, password, pwdHint);
        return 1;
    }

    //更新上一次登录时间
    @Override
    public void updateLastLoginTime(LocalDateTime lastLoginTime, String account) {
        sysUserDao.updateLastLoginTime(lastLoginTime, account);
    }

    @Override
    public LocalDateTime findLastLoginTimeByAccount(String account) {
        if (account.equals("")) {
            return null;
        }
        return sysUserDao.findLastLoginTimeByAccount(account);
    }

    @Override
    public String SelectAccountAndHint(SysUser sysUser) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", sysUser.getAccount());
        queryWrapper.eq("pwd_hint", sysUser.getPwdHint());
        List<SysUser> sysUsers = null;
        int status = 1;
        try {
            sysUsers = sysUserDao.selectList(queryWrapper);
        } catch (Exception ex) {
        }
        for (SysUser item : sysUsers
        ) {
            status = item.getStatus();
        }
        return (sysUsers != null && sysUsers.size() != 0) ? ((status == 1) ?
                "跳转为重设密码页面" : "账号已被停用或删除,请与管理员联系") : "账号错误或提示语错误";

    }
}
