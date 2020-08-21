package com.demofactory.syscontrol.service.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demofactory.syscontrol.dao.BookDao;
import com.demofactory.syscontrol.domain.Books;
import com.demofactory.syscontrol.api.BookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : Hanamaru
 * @description: TODO
 * @date : 2020/8/18 11:51
 */
@Slf4j
@Service
public class BookServiceImpl extends ServiceImpl<BookDao, Books> implements BookService {
    @Resource
    private BookDao bookDao;

    @Override
    public String insertBook(Books books) {
        QueryWrapper<Books> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_name", books.getBookName());
        queryWrapper.eq("domain_id", books.getDomainId());
        if (bookDao.selectCount(queryWrapper) > 0) {
            log.info("result------已存在该书");
            return "已存在该书";
        }
        bookDao.insert(books);
        log.info("result------插入成功");
        return "插入成功";
    }

    @Override
    public List<Books> selectBook(Books books) {
        QueryWrapper<Books> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(books.getBookName() != null, "book_name", books.getBookName());
        queryWrapper.eq(books.getDomainId() != null, "domain_id", books.getDomainId());
        List<Books> result = null;
        try {
            result = bookDao.selectList(queryWrapper);
        } catch (Exception ex) {
        }
        return result;
    }

    @Override
    public String deleteBook(Long id) {
        QueryWrapper<Books> queryWrapper;
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        if (bookDao.selectCount(queryWrapper) > 0) {
            bookDao.delete(queryWrapper);
            log.info("result------删除成功");
            return "删除成功";
        }
        log.info("result------不存在该书");
        return "不存在该书";
    }

}
