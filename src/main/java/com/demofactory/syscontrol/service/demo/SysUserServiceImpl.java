package com.demofactory.syscontrol.service.demo;

import com.demofactory.syscontrol.api.SysUserService;
import com.demofactory.syscontrol.dao.SysUserDao;
import com.demofactory.syscontrol.domain.SysUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
    public int registerSysUser(String account,String password) {
        //查询账号密码是否为空，为空返回0
        if (account.equals("")||password.equals("")){
            return 0;
        }
        //查询账号是否存在，存在则返回-1
        if(sysUserDao.findByAccount(account) != null){
            return -1;
        }
        //否则,插入新用户，返回1
        sysUserDao.insertNewSysUser(account,password);
        return 1;
    }
}
