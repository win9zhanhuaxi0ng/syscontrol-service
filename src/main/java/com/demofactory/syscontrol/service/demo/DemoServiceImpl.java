package com.demofactory.syscontrol.service.demo;

import com.demofactory.syscontrol.api.DemoService;
import com.demofactory.syscontrol.dao.DemoDao;
import com.demofactory.syscontrol.domain.Demo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wy
 */
@Service
public class DemoServiceImpl implements DemoService {

    @Resource
    private DemoDao demoDao;

    @Override
    public int insert(Demo demo) {
        demoDao.insert(demo);
        return 1;
    }
}
