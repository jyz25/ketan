package com.ketan.web.home;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @auther Kindow
 * @date 2024/12/11
 * @project ketan
 * @description 主页控制器
 */


@Controller
public class IndexController {

    @GetMapping(path = {"/", "", "/index", "/login"})
    public String index(Model model, HttpServletRequest request) {
        return "views/home/index";
    }
}
