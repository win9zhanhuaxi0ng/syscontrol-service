package com.demofactory.syscontrol.service.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demofactory.syscontrol.api.SysUserService;
import com.demofactory.syscontrol.common.utils.RegexUtil;
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
    public String login(SysUser sysUser) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account",sysUser.getAccount());
        SysUser sysUser1= sysUserDao.selectOne(queryWrapper);
        if (sysUser1==null){
            return "账号不存在";
        }
        if (sysUser1.getPassword().equals(sysUser.getPassword())){
                if (sysUser1.getStatus()==1){
                    LocalDateTime localDateTime = sysUser1.getLastLoginTime();
                    sysUser1.setLastLoginTime(LocalDateTime.now());
                    sysUserDao.updateById(sysUser1);
                    if (localDateTime==null){
                        return "欢迎您第一次登录";
                    }
                    //重新设置登录时间
                    return "登录成功,您上次登录时间为："+localDateTime;
                }
                return "您的账号已被停用或删除";
        }
        return "两次密码输入不一致";
    }

    /**
     * 注册功能，根据传入的账号对数据库查找是否已存在，否，则判断两次密码是否一致，是，则插入
     * @param sysUser 用户信息包含账号、密码、提示语
     * @param secondaryPwd 二次密码校验
     * @return 0二次密码校验失败 1插入成功 2用户已存在
     */
    @Override
    public String register(SysUser sysUser,String secondaryPwd) {
        //正则表达式验证
        if (RegexUtil.checkRegex(RegexUtil.REGEX_EMAIL,sysUser.getAccount())) {
            sysUser.setUserEmail(sysUser.getAccount());
        } else if (RegexUtil.checkRegex(RegexUtil.REGEX_MOBILE,sysUser.getAccount())) {
            sysUser.setUserPhone(sysUser.getAccount());
        } else {
            return "账号格式不符合！";
        }
        if (!RegexUtil.checkRegex(RegexUtil.REGEX_PASSWORD,sysUser.getPassword())){
            return "密码格式不符合！";
        }
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account",sysUser.getAccount());
        SysUser sysUser1= sysUserDao.selectOne(queryWrapper);
        if(sysUser1==null){
            if (sysUser.getPassword().equals(secondaryPwd)){
                sysUserDao.insert(sysUser);
                return "注册成功";
            }
            return "两次密码输入不一致";
        }
        return "账号已存在";
    }

    /**
     * 更新登录时间
     * @param sysUser 用户信息
     */
    @Override
    public void updateLastLoginTime(SysUser sysUser) {

    }

    /**
     * 查询账号和提示语
     * @param sysUser 用户信息账号、提示语
     * @return "跳转为重设密码页面":"账号错误或提示语错误"
     */
    @Override
    public String selectAccountAndHint(SysUser sysUser) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account",sysUser.getAccount());
        queryWrapper.eq("pwd_hint",sysUser.getPwdHint());
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

    /**
     *修改密码功能
     * @param sysUser 用户账号、密码
     * @param secondaryPwd 二次校验密码
     * @return 1重设成功 -1二次密码输入不一致
     */
    @Override
    public String updatePassword(SysUser sysUser,String secondaryPwd) {
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("password",sysUser.getPassword());
        updateWrapper.eq("account",sysUser.getAccount());
        if (sysUser.getPassword().equals(secondaryPwd)){
            sysUserDao.update(sysUser,updateWrapper);
            return "重置密码成功";
        }
        return "两次密码输入不一致";
    }
}
