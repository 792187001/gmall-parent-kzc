package com.atguigu.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PassportController {

    @RequestMapping("login.html")
    public String login(HttpServletRequest request, String originUrl, Model model){
        //String requestURI = request.getRequestURI();
        //StringBuffer requestURL = request.getRequestURL();
        //String contextPath = request.getContextPath();
        String queryString = request.getQueryString();
        int i = queryString.indexOf("=");
        originUrl = queryString.substring(i + 1);
        model.addAttribute("originUrl",originUrl);
        return "login";
    }

}
