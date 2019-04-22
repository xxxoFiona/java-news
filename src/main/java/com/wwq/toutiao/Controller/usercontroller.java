package com.wwq.toutiao.Controller;

import com.wwq.toutiao.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


@Controller
public class usercontroller {

    @Autowired
    private UserService userService;


}
