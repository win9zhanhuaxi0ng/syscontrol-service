package com.demofactory.syscontrol.service.demo;

import com.demofactory.syscontrol.api.DemoService;
import com.demofactory.syscontrol.dao.DemoDao;
import com.demofactory.syscontrol.domain.Demo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wy
 */
@Service
public class DemoServiceImpl implements DemoService {

    @Autowired
    private DemoDao demoDao;

    @Override
    public int insert(Demo demo) {

        return demoDao.insert(demo);
    }
}
