package com.mh.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/posts")
@Controller
public class PostController {

    @GetMapping
    public String posts(ModelMap map) {
        map.addAttribute("posts", List.of());
        return "posts/index";
    }
}
