package com.example.infra.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping("/hello-world")
    public String hello(){
        return "hello, world";
    }
}

// 이미지를 만들어야 하는데 일반적으로 도커 파일 만들어서 빌드해서 사용하는데
// jib는 gitOps -> 이미지 만들어서 푸시 (이미지 생성시 이 앱 말고 다른 작업을 안할 거면 용이하다)
// https://github.com/GoogleContainerTools/jib