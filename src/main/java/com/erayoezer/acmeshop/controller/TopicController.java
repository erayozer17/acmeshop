package com.erayoezer.acmeshop.controller;

import com.erayoezer.acmeshop.model.Topic;
import com.erayoezer.acmeshop.model.User;
import com.erayoezer.acmeshop.service.TopicService;
import com.erayoezer.acmeshop.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
// TODO: refactor logic into services
@Controller
public class TopicController {

    private static final Logger logger = LoggerFactory.getLogger(TopicController.class);

    private final UserService userService;
    private final TopicService topicService;

    public TopicController(
            UserService userService,
            TopicService topicService
    ) {
        this.userService = userService;
        this.topicService = topicService;
    }

    @GetMapping("/topics/create")
    public String createTopicGet(Model model) {
        model.addAttribute("isAuthenticated", true);
        return "createTopic";
    }

    @PostMapping("/topics/create")
    public String createTopicPost(Model model,  @RequestParam String name, @RequestParam String description) {
        Topic topic = new Topic();
        topic.setName(name);
        topic.setDescription(description);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()) {
            logger.error("User could not be found. This should NOT happen.");
            return "redirect:/home";
        }
        topic.setUser(user.get());
        Topic retTopic = topicService.save(topic);
        if (retTopic == null) {
            logger.error("Topic could not be created. Name: {} Description: {}", name, description);
            return "redirect:/home";
        }
        model.addAttribute("isAuthenticated", true);
        return "redirect:/home";
    }

    @GetMapping("/topics/delete/{id}") // TODO: make it delete
    public String deleteTopic(@PathVariable Long id, Model model) {
        Optional<Topic> topic = topicService.findById(id);
        if (topic.isEmpty()) {
            logger.error("Topic could not be found to delete. Id: {}", id);
            return "redirect:/home";
        }
        topicService.deleteById(id);
        model.addAttribute("isAuthenticated", true);
        return "redirect:/home";
    }
}
