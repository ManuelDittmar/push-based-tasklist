package org.camunda.consulting.push_based_tasklist.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping("/app/**")
    public String forward() {
        return "forward:/index.html";
    }
}
