package com.demofactory.syscontrol.service.demo;

import com.demofactory.syscontrol.api.DemoService;
import com.demofactory.syscontrol.domain.demo.Demo;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceImpl implements DemoService {


    @Override
    public int insert(Demo demo) {
        return 0;
    }
}
