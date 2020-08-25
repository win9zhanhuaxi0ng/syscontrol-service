package com.demofactory.syscontrol.service.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demofactory.syscontrol.common.ObjResult;
import com.demofactory.syscontrol.common.Result;
import com.demofactory.syscontrol.dao.BookDao;
import com.demofactory.syscontrol.domain.Books;
import com.demofactory.syscontrol.api.BookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author : Hanamaru
 * @description: TODO
 * @date : 2020/8/18 11:51
 */
@Slf4j
@Service
public class BookServiceImpl extends ServiceImpl<BookDao, Books> implements BookService
{
    @Resource
    private BookDao bookDao;

    @Override
    public ObjResult<String> insertBook(Books books)
    {
        QueryWrapper<Books> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_name", books.getBookName());
        queryWrapper.eq("domain_id", books.getDomainId());
        if (bookDao.selectCount(queryWrapper) > 0)
        {
            log.info("result------已存在该书");
            return ObjResult.failure("已存在该书");
        }
        bookDao.insert(books);
        log.info("result------插入成功");
        return ObjResult.success("插入成功");
    }

    @Override
    public ObjResult<List<Books>> selectBook(Books books)
    {
        QueryWrapper<Books> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!StringUtils.isBlank(books.getBookName()), "book_name", books.getBookName());
        queryWrapper.eq(!Objects.isNull(books.getDomainId()), "domain_id", books.getDomainId());
        List<Books> result = null;
        try
        {
            result = bookDao.selectList(queryWrapper);
        } catch (Exception ex)
        {
        }
        return ObjResult.success(result,"查询成功");
    }

    @Override
    public ObjResult<String> deleteBook(Long id)
    {
        QueryWrapper<Books> queryWrapper;
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        if (bookDao.selectCount(queryWrapper) > 0)
        {
            bookDao.delete(queryWrapper);
            log.info("result------删除成功");
            return ObjResult.success("删除成功");
        }
        log.info("result------不存在该书");
        return ObjResult.failure("不存在该书");
    }

}
