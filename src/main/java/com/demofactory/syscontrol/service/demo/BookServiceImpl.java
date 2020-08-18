package com.demofactory.syscontrol.service.demo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demofactory.syscontrol.dao.BookDao;
import com.demofactory.syscontrol.domain.Books;
import com.demofactory.syscontrol.api.BookService;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : Hanamaru
 * @description: TODO
 * @date : 2020/8/18 11:51
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookDao, Books> implements BookService {
    @Resource
    private BookDao bookDao;

    @Override
    public String insertBook(Books books)
    {
        QueryWrapper<Books> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_name",books.getBookName());
        if(bookDao.selectCount(queryWrapper)>0){
            System.out.println("result------已存在该书");
            return "已存在该书";
        }
        bookDao.insert(books);
        return "插入成功";
    }

    @Override
    public List<Books> selectBook(Books books)
    {
        QueryWrapper<Books> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_name",books.getBookName());
        List<Books> result = null;
        try{
            result = bookDao.selectList(queryWrapper);
        }catch (Exception ex){
        }
        return result;
    }

    @Override
    public String deleteBook(Books books) {
        QueryWrapper<Books> queryWrapper;
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("book_name",books.getBookName());
        if(bookDao.selectCount(queryWrapper)>0)
        {
            bookDao.delete(queryWrapper);
            return "删除成功";
        }
        return "不存在该书";
    }


}
