package com.example.rakesh;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.Date;

//Cross Cutting Concerns, common things in a same Aspect

// terms

//advice : what to call

@Component
@Aspect
@EnableAspectJAutoProxy // we need this to enable aop
public class BussinessLogic {
    // we will understand Aop with this class.
    // write at one place and we can use it anywhere
    // examples : logging, security, transaction, any frequently used code

    final static Logger logger = LoggerFactory.getLogger(BussinessLogic.class);

    @After("execution(String secondIndex(*)) && args(name)")
    public void helloName(String name) {
        logger.info("Nice to see you " + name);
    }

    //@Before("execution(String index())")
    public void  log() {
        logger.info("log aspect called");
    }

    @Before("execution(String index())")
    public void hello() {
        logger.info("hello");
    }

    @After("execution(String index())")
    public void bye() {
        logger.info("bye");
    }
}
