package com.erayoezer.acmeshop.controller;

import com.erayoezer.acmeshop.model.Topic;
import com.erayoezer.acmeshop.service.TopicService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
// TODO: refactor logic into services
@Controller
public class HomeController {

    private final TopicService topicService;

    public HomeController(
            TopicService topicService
    ) {
        this.topicService = topicService;
    }

    @GetMapping("/home")
    public String showHomePage(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("email", email);
        model.addAttribute("isAuthenticated", true);
        List<Topic> topicsByEmail = topicService.findAllByEmail(email);
        model.addAttribute("topics", topicsByEmail);
        return "home";
    }

    @GetMapping("/")
    public String toHomePage() {
//        throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "This is a try out error", false);
        return "redirect:/home";
    }

    @GetMapping("/contact")
    public String toContact(Model model) {
        model.addAttribute("isAuthenticated", true);
        return "contact";
    }
}
