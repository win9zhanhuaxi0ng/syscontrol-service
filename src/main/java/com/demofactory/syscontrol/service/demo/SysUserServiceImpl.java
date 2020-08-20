package com.demofactory.syscontrol.service.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demofactory.syscontrol.api.SysUserService;
import com.demofactory.syscontrol.common.utils.RegexUtil;
import com.demofactory.syscontrol.dao.*;
import com.demofactory.syscontrol.domain.*;
import com.demofactory.syscontrol.domain.dto.SysUserDTO;
import org.apache.catalina.User;
import org.apache.dubbo.config.annotation.Service;


import javax.annotation.Resource;
import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUser> implements SysUserService {

    @Resource
    private SysUserDao sysUserDao;
    @Resource
    private SysDomainDao sysDomainDao;
    @Resource
    private SysOrgDao sysOrgDao;
    @Resource
    private UserBookDao userBookDao;
    @Resource
    private BookDao bookDao;

    /**
     * 登录功能根据传入的账号，查询数据库判断password和status
     *
     * @param sysUser 用户信息包含账号、密码
     * @return
     */
    @Override
    public String login(SysUser sysUser) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", sysUser.getAccount());
        SysUser sysUser1 = sysUserDao.selectOne(queryWrapper);
        if (sysUser1 == null) {
            return "账号或密码错误";
        }
        if (sysUser1.getPassword().equals(sysUser.getPassword())) {
            if (sysUser1.getStatus() == 1) {
                LocalDateTime localDateTime = sysUser1.getLastLoginTime();
                sysUser1.setLastLoginTime(LocalDateTime.now());
                sysUserDao.updateById(sysUser1);
                if (localDateTime == null) {
                    return "欢迎您第一次登录";
                }
                //重新设置登录时间
                return "登录成功,您上次登录时间为：" + localDateTime;
            }
            return "您的账号已被停用或删除";
        }
        return "账号或密码错误";
    }

    /**
     * 注册功能，根据传入的账号对数据库查找是否已存在，否，则判断两次密码是否一致，是，则插入
     *
     * @param sysUserDTO 用户DTO信息包含账号、密码、提示语、二次输入密码
     * @return 二次密码校验失败 插入成功 用户已存在
     */
    @Override
    public String register(SysUserDTO sysUserDTO) {
        SysUser sysUser = new SysUser();
        sysUser.setAccount(sysUserDTO.getAccount());
        sysUser.setPassword(sysUserDTO.getPassword());
        sysUser.setPwdHint(sysUserDTO.getPwdHint());
        //正则表达式验证
        if (RegexUtil.checkRegex(RegexUtil.REGEX_EMAIL, sysUser.getAccount())) {
            sysUser.setUserEmail(sysUser.getAccount());
        } else if (RegexUtil.checkRegex(RegexUtil.REGEX_MOBILE, sysUser.getAccount())) {
            sysUser.setUserPhone(sysUser.getAccount());
        } else {
            return "账号格式不符合！";
        }
        if (!RegexUtil.checkRegex(RegexUtil.REGEX_PASSWORD, sysUser.getPassword())) {
            return "密码格式不符合！";
        }
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", sysUser.getAccount());
        SysUser sysUser1 = sysUserDao.selectOne(queryWrapper);
        if (sysUser1 == null) {
            if (sysUser.getPassword().equals(sysUserDTO.getSecondaryPwd())) {
                sysUserDao.insert(sysUser);
                return "注册成功";
            }
            return "两次密码输入不一致";
        }
        return "账号已存在";
    }

    /**
     * 查询账号和提示语
     *
     * @param sysUser 用户信息账号、提示语
     * @return "跳转为重设密码页面":"账号错误或提示语错误"
     */
    @Override
    public String selectAccountAndHint(SysUser sysUser) {
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

    /**
     * 修改密码功能
     *
     * @param sysUserDTO 用户DTO信息包含账号、密码、二次输入密码
     * @return 重设成功 二次密码输入不一致
     */
    @Override
    public String updatePassword(SysUserDTO sysUserDTO) {
        SysUser sysUser = new SysUser();
        sysUser.setAccount(sysUserDTO.getAccount());
        sysUser.setPassword(sysUserDTO.getPassword());
        //密码正则表达式验证
        if (!RegexUtil.checkRegex(RegexUtil.REGEX_PASSWORD, sysUser.getPassword())) {
            return "重置密码格式不正确！";
        }
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("password", sysUser.getPassword());
        updateWrapper.eq("account", sysUser.getAccount());
        if (sysUser.getPassword().equals(sysUserDTO.getSecondaryPwd())) {
            sysUser.setPwdUpdateTime(LocalDateTime.now());
            sysUserDao.update(sysUser, updateWrapper);
            return "重置密码成功";
        }
        return "两次密码输入不一致";
    }

    /**
     * 查询用户所属域和组织
     *
     * @param sysUser 登入的用户信息
     * @return
     */
    @Override
    public String selectSysDomainAndSysOrg(SysUser sysUser) {

        SysUser sysUser1 = sysUserDao.selectById(sysUser.getId());
        if (!Objects.isNull(sysUser1.getDomainId()) && !Objects.isNull(sysUser1.getDomainId())) {
            SysDomain sysDomain = sysDomainDao.selectById(sysUser1.getDomainId());
            SysOrg sysOrg = sysOrgDao.selectById(sysUser1.getOrgId());
            return sysDomain.getName() + sysOrg.getOrgName();
        }
        return "用户未分配域和组织";
    }

    /**
     * 用户选择域和组织
     *
     * @param sysUser 用户信息+域id和组织id
     * @return
     */
    @Override
    public String insertSysDomainAndSysOrg(SysUser sysUser) {

        if (Objects.isNull(sysUser.getDomainId())) {
            return "未选择加入域";
        }
        if (Objects.isNull(sysUser.getOrgId())) {
            return "未选择加入组织";
        }
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", sysUser.getId());
        sysUserDao.update(sysUser, updateWrapper);
        return "修改成功";
    }

    /**
     * 用户所在域列出所有书
     *
     * @param sysUser
     * @return
     */
    @Override
    public List<Books> selectBooksByUserDomainId(SysUser sysUser) {

        QueryWrapper<Books> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("domain_id", sysUser.getDomainId());
        return bookDao.selectList(queryWrapper);
    }

    /**
     * 用户添加书
     *
     * @param userBook 书+用户ID
     * @return
     */
    @Override
    public String insertBooksToUser(UserBook userBook) {
        QueryWrapper<UserBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_id", userBook.getBookId());
        queryWrapper.eq("user_id", userBook.getUserId());
        if (Objects.isNull(userBookDao.selectOne(queryWrapper))) {
            userBookDao.insert(userBook);
            return "添加书成功";
        }
        return "您已添加此书";


    }

    /**
     * 显示用户下的书列表
     * @param sysUser
     * @return
     */
    @Override
    public List<Books> selectBooksByUserId(SysUser sysUser) {
        List<Long> bookIds = userBookDao.selectBookIdByUserId(sysUser.getId());
        return bookDao.selectBatchIds(bookIds);
    }

    /**
     * 用户删除书
     *
     * @param userBook 书ID和用户ID
     * @return
     */
    @Override
    public String deleteBooksByUser(UserBook userBook) {
        userBookDao.deleteById(userBook);
        return "删除成功";
    }


}
