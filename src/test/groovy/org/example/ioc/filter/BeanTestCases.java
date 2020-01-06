package org.example.ioc.filter;

import org.example.ioc.annotation.Component;
import org.example.ioc.annotation.PostConstruct;

@Component
class TestOne {

    private TestTwo testTwo;

    TestOne(TestTwo testTwo) {
        this.testTwo = testTwo;
    }

    @PostConstruct
    public void test() {
        System.out.println("test");
    }
}

@Component
class TestTwo {

}

class TestThree {

}
