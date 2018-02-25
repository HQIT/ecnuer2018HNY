package com.cloume.ecnu.ecnuer2018.controller;

import com.cloume.ecnu.ecnuer2018.model.MyUser;

import javax.servlet.http.HttpServletRequest;

class CommonController {
    MyUser getCurrentUser(HttpServletRequest request) {
        MyUser u = (MyUser) request.getSession().getAttribute("_MY_USER");
        return u;
    }
}
