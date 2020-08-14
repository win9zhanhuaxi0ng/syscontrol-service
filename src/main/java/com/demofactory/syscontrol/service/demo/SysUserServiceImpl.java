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
    public SysUser loginByAccount(String account) {

        return sysUserDao.findByAccount(account);
    }
}
