package org.example.ioc.reflection;

import org.example.ioc.annotation.PostConstruct;

class PostConstructTestCases {
}

class PostConstructMethodTest {

    @PostConstruct
    void postConstructInit() {
        System.out.println("test");
    }
}

class NoPostConstructMethodTest {

}

class PostConstructInvalidReturnMethodTest {

    @PostConstruct
    int postConstructInit() {
        return 1;
    }
}

class MultiplePostConstructMethodTest {

    @PostConstruct
    void postConstructInit() {
        System.out.println("test");
    }

    @PostConstruct
    void postConstructInit2() {
        System.out.println("test2");
    }
}