package com.demofactory.syscontrol.service.demo;

import com.demofactory.syscontrol.api.SysUserService;
import com.demofactory.syscontrol.dao.SysUserDao;
import com.demofactory.syscontrol.domain.SysUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Resource
    private SysUserDao sysUserDao;


    @Override
    public SysUser loginByAccountAndPassword(String account, String password) {

        SysUser sysUser = sysUserDao.findByAccount(account);
        if(sysUser == null){
            return null;
        }
        if (sysUser.getPassword().equals(password)){

            return sysUser;
        }
        return null;
    }

    @Override
    public int registerSysUser(String account,String password, String pwdHint) {
        //查询账号密码是否为空，为空返回0
        if (account.equals("")||password.equals("")||pwdHint.equals("")){
            return 0;
        }
        //查询账号是否存在，存在则返回-1
        if(sysUserDao.findByAccount(account) != null){
            return -1;
        }
        //否则,插入新用户，返回1
        sysUserDao.insertNewSysUser(account,password,pwdHint);
        return 1;
    }
    //更新上一次登录时间
    @Override
    public void updateLastLoginTime(LocalDateTime lastLoginTime, String account) {
        sysUserDao.updateLastLoginTime(lastLoginTime,account);
    }

    @Override
    public LocalDateTime findLastLoginTimeByAccount(String account) {
        if (account.equals("")){
            return null;
        }
        return  sysUserDao.findLastLoginTimeByAccount(account);
    }
}
