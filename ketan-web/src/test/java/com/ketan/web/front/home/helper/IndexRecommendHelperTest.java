package com.ketan.web.front.home.helper;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class IndexRecommendHelperTest {

    @Autowired
    IndexRecommendHelper helper;

    @Test
    public void testBuildIndexVo(){
        helper.buildIndexVo("后端");
    }
}
