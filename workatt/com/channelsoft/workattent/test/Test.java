package com.channelsoft.workattent.test;


import com.channelsoft.spring.RootConfig;
import com.channelsoft.workattent.controller.WorkattController;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = RootConfig.class)
public class Test {

    @Autowired
    WorkattController workattController;

    @org.junit.Test
    public void test() throws Exception {

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(workattController).build();
        String result = mockMvc.perform(get("/query").param("workerNo", "14015")).andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }

}
