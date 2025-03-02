package com.ketan.web.front.home;

import com.ketan.web.front.home.helper.IndexRecommendHelper;
import com.ketan.web.front.home.vo.IndexVo;
import com.ketan.web.global.BaseViewController;
import org.springframework.beans.factory.annotation.Autowired;
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
public class IndexController extends BaseViewController {

    @Autowired
    IndexRecommendHelper indexRecommendHelper;

    @GetMapping(path = {"/", "", "/index", "/login"})
    public String index(Model model, HttpServletRequest request) {
        String activeTab = request.getParameter("category");
        IndexVo vo = indexRecommendHelper.buildIndexVo(activeTab);
        model.addAttribute("vo", vo);
        return "views/home/index";
    }
}
