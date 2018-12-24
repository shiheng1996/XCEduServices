package com.xuecheng.manage_course.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

@Controller
@RequestMapping("/cms")
public class indexController {

    @RequestMapping("/index")
    public String index(HashMap<String,Object> map){
//        map.put("hello", "欢迎进入HTML页面");
        System.out.println("aaaa");
        return "/index";
    }
}
