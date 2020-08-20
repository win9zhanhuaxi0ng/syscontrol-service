package com.demofactory.syscontrol.service.demo;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demofactory.syscontrol.api.UserBookService;
import com.demofactory.syscontrol.dao.UserBookDao;
import com.demofactory.syscontrol.domain.UserBook;
import org.springframework.stereotype.Service;

@Service
public class UserBookServiceImpl extends ServiceImpl<UserBookDao, UserBook> implements UserBookService {
}
