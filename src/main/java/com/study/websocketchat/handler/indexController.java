package com.study.websocketchat.handler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class indexController {

    @RequestMapping("/")
    public String index() {
        return "index"; // 타임리프는 .html 생략가능
    }
}
