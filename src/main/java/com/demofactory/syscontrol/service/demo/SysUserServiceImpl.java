package com.demofactory.syscontrol.service.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demofactory.syscontrol.api.SysUserService;
import com.demofactory.syscontrol.common.Result;
import com.demofactory.syscontrol.common.utils.RegexUtil;
import com.demofactory.syscontrol.dao.*;
import com.demofactory.syscontrol.domain.*;
import com.demofactory.syscontrol.domain.dto.SysUserDTO;
import org.apache.dubbo.config.annotation.Service;


import javax.annotation.Resource;
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
    public Result login(SysUser sysUser) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", sysUser.getAccount());
        SysUser sysUser1 = sysUserDao.selectOne(queryWrapper);
        Result result = new Result();
        if (sysUser1 == null) {
            result.setMessage("账号或密码错误");
            return result;
        }
        if (sysUser1.getPassword().equals(sysUser.getPassword())) {
            if (sysUser1.getStatus() == 1) {
                LocalDateTime localDateTime = sysUser1.getLastLoginTime();
                //重新设置登录时间
                sysUser1.setLastLoginTime(LocalDateTime.now());
                sysUserDao.updateById(sysUser1);
                if (localDateTime == null) {
                    result.setMessage("欢迎您第一次登录");
                    result.setSuccess(true);
                    return result;
                }
                result.setSuccess(true);
                result.setMessage("登录成功,您上次登录时间为：" + localDateTime);
                return result;
            }
            result.setMessage("您的账号已被停用或删除");
            return result;
        }
        result.setMessage("账号或密码错误");
        return result;
    }

    /**
     * 注册功能，根据传入的账号对数据库查找是否已存在，否，则判断两次密码是否一致，是，则插入
     *
     * @param sysUserDTO 用户DTO信息包含账号、密码、提示语、二次输入密码
     * @return 二次密码校验失败 插入成功 用户已存在
     */
    @Override
    public Result register(SysUserDTO sysUserDTO) {
        SysUser sysUser = new SysUser();
        sysUser.setAccount(sysUserDTO.getAccount());
        sysUser.setPassword(sysUserDTO.getPassword());
        sysUser.setPwdHint(sysUserDTO.getPwdHint());
        Result result = new Result();
        //正则表达式验证
        if (RegexUtil.checkRegex(RegexUtil.REGEX_EMAIL, sysUser.getAccount())) {
            sysUser.setUserEmail(sysUser.getAccount());
        } else if (RegexUtil.checkRegex(RegexUtil.REGEX_MOBILE, sysUser.getAccount())) {
            sysUser.setUserPhone(sysUser.getAccount());
        } else {
            result.setMessage("账号格式不符合");
            return result;
        }
        if (!RegexUtil.checkRegex(RegexUtil.REGEX_PASSWORD, sysUser.getPassword())) {
            result.setMessage("密码格式不符合");
            return result;
        }
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", sysUser.getAccount());
        SysUser sysUser1 = sysUserDao.selectOne(queryWrapper);
        if (sysUser1 == null) {
            if (sysUser.getPassword().equals(sysUserDTO.getSecondaryPwd())) {
                sysUserDao.insert(sysUser);
                result.setMessage("注册成功");
                result.setSuccess(true);
                return result;
            }
            result.setMessage("两次密码输入不一致");
            return result;
        }
        result.setMessage("账号已存在");
        return result;
    }

    /**
     * 查询账号和提示语
     *
     * @param sysUser 用户信息账号、提示语
     * @return "跳转为重设密码页面":"账号错误或提示语错误"
     */
    @Override
    public Result selectAccountAndHint(SysUser sysUser) {
        Result result = new Result();
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", sysUser.getAccount());
        SysUser sysUser1 = sysUserDao.selectOne(queryWrapper);
        if (Objects.isNull(sysUser1)){
            result.setMessage("账号不存在");
            return result;
        }
        if (sysUser1.getStatus()!=1){
            result.setMessage("账号已停用或删除");
            return result;
        }
        if (sysUser1.getPwdHint().equals(sysUser.getPwdHint())){
            result.setMessage("验证成功");
            result.setSuccess(true);
            return result;
        }
        result.setMessage("验证提示语失败");
        return result;
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
    public Result selectSysDomainAndSysOrg(SysUser sysUser) {
        Result result = new Result();
        SysUser sysUser1 = sysUserDao.selectById(sysUser.getId());
        if (!Objects.isNull(sysUser1.getDomainId()) && !Objects.isNull(sysUser1.getOrgId())) {
            SysDomain sysDomain = sysDomainDao.selectById(sysUser1.getDomainId());
            SysOrg sysOrg = sysOrgDao.selectById(sysUser1.getOrgId());
            result.setMessage("所属域："+sysDomain.getName()+"所属组织："+sysOrg.getOrgName());
            result.setSuccess(true);
            return result;
        }
        result.setMessage("用户未分配域和组织");
        return result;
    }

    /**
     * 用户选择域和组织
     *
     * @param sysUser 用户信息+域id和组织id
     * @return
     */
    @Override
    public Result insertSysDomainAndSysOrg(SysUser sysUser) {
        Result result = new Result();

        if (Objects.isNull(sysUser.getDomainId())) {
            result.setMessage("未选择加入域");
            return result;
        }
        if (Objects.isNull(sysUser.getOrgId())) {
            result.setMessage("未选择加入组织");
            return result;
        }
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", sysUser.getId());
        sysUserDao.update(sysUser, updateWrapper);
        result.setMessage("加入成功");
        result.setSuccess(true);
        return result;
    }

    /**
     * 当前域下，用户未添加的书
     *
     * @param
     * @return
     */
    @Override
    public Result selectBooksByUserDomainId(SysUser sysUser) {
        Result result = new Result();
        SysUser sysUser1 = sysUserDao.selectById(sysUser);
        if (Objects.isNull(sysUser1.getDomainId())){
            result.setMessage("请先加入域和组织");
            return result;
        }
        List<Long> list = bookDao.selectBookIdByDomainId(sysUser1.getDomainId());
        if (list.isEmpty()){
            result.setSuccess(true);
            return result;
        }
        List<Long> list1 = userBookDao.selectBookIdByUserId(sysUser1.getId());
        if (list1.isEmpty()){
            result.setData(bookDao.selectBatchIds(list));
            result.setSuccess(true);
            return result;
        }
        for (Long l:list1){
            list.remove(l);
        }
        if (list.isEmpty()){
            result.setSuccess(true);
            return result;
        }
        result.setData(bookDao.selectBatchIds(list));
        result.setSuccess(true);
        return result;
    }

    /**
     * 用户添加书
     *
     * @param userBook 书+用户ID
     * @return
     */
    @Override
    public Result insertBooksToUser(UserBook userBook) {
        Result result = new Result();
        QueryWrapper<UserBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_id", userBook.getBookId());
        queryWrapper.eq("user_id", userBook.getUserId());
        if (Objects.isNull(userBookDao.selectOne(queryWrapper))) {
            userBookDao.insert(userBook);
            result.setMessage("添加成功");
            result.setSuccess(true);
            return result;
        }
        result.setMessage("您已添加此书");
        return result;


    }

    /**
     * 用户已添加的书
     *
     * @param sysUser
     * @return
     */
    @Override
    public Result selectBooksByUserId(SysUser sysUser) {
        Result result = new Result();
        SysUser sysUser1 = sysUserDao.selectById(sysUser);
        if (Objects.isNull(sysUser1.getDomainId())){
            result.setMessage("请先加入域和组织");
            return result;
        }
        List<Long> bookIds = userBookDao.selectBookIdByUserId(sysUser.getId());
        if (bookIds.isEmpty()){
            result.setSuccess(true);
            return result;
        }
        result.setData(bookDao.selectBatchIds(bookIds));
        result.setSuccess(true);
        return result;
    }

    /**
     * 用户删除书
     *
     * @param
     * @return
     */
    @Override
    public Result deleteBooksByUser(UserBook userBook) {
        Result result = new Result();
        QueryWrapper<UserBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_id",userBook.getBookId());
        queryWrapper.eq("user_id",userBook.getUserId());
        if(userBookDao.delete(queryWrapper)!=0){
            result.setSuccess(true);
            result.setMessage("删除成功");
            return result;
        }
        result.setMessage("删除失败");
        return result;
    }


}
