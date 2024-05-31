package com.erayoezer.acmeshop.controller.frontend;

import com.erayoezer.acmeshop.model.Item;
import com.erayoezer.acmeshop.model.Topic;
import com.erayoezer.acmeshop.model.User;
import com.erayoezer.acmeshop.service.ItemService;
import com.erayoezer.acmeshop.service.TopicService;
import com.erayoezer.acmeshop.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TopicService topicService;
    private final ItemService itemService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserService userService,
            TopicService topicService,
            ItemService itemService
    ) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.topicService = topicService;
        this.itemService = itemService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("isAuthenticated", false);
        return "login";
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("isAuthenticated", false);
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@RequestParam String username, @RequestParam String email, @RequestParam String password) {
        userService.saveUser(username, email, password);
        return "redirect:/login";
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

    @GetMapping("/items/{id}")
    public String getTopicItems(@PathVariable Long id, Model model) {
        Optional<Topic> topic = topicService.findById(id);
        if (topic.isPresent()) {
            Topic retTopic = topic.get();
            model.addAttribute("topic", retTopic);
            List<Item> itemsByTopic = itemService.findByTopic(retTopic);
            model.addAttribute("items", itemsByTopic);
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("topicId", id);
            return "items";
        } else {
            logger.error("Topic was returned but couldn't be found now. This should NOT happen.");
            return "redirect:/home";
        }
    }

    @GetMapping("/item/edit/{id}") // TODO: make it put
    public String editItem(@PathVariable Long id, Model model) {
        Optional<Item> item = itemService.findById(id);
        if (item.isPresent()) {
            Item retItem = item.get();
            model.addAttribute("item", retItem);
            model.addAttribute("isAuthenticated", true);
            return "itemEdit";
        } else {
            logger.error("Item was returned but couldn't be found now. This should NOT happen.");
            return "redirect:/home";
        }
    }

    @PostMapping("/item/edit/{id}") // TODO: make it put
    public String saveEditedItem(@PathVariable Long id, Model model,  @RequestParam String text, @RequestParam String nextAt) {
        Optional<Item> returned = itemService.findById(id);
        if (returned.isEmpty()) {
            logger.error("Item could not be found for item editing. This should NOT happen.");
            return "redirect:/home";
        }
        Item item = returned.get();
        item.setText(text);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            item.setNextAt(formatter.parse(nextAt));
        } catch (Exception e) {
            logger.error("Date conversion error. String: {} Error: {}", nextAt, e.getMessage());
        }
        itemService.save(item);
        Optional<Topic> topic = topicService.findById(item.getTopic().getId());
        if (topic.isEmpty()) {
            logger.error("Topic could not be found for item editing. This should NOT happen.");
            return "redirect:/home";
        }
        Long topicId = topic.get().getId();
        return "redirect:/items/" + topicId;
    }

    @GetMapping("/")
    public String toHomePage() {
        return "redirect:/home";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username, @RequestParam String password, Model model) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/home";
        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/createTopic")
    public String createTopicGet(Model model) {
        model.addAttribute("isAuthenticated", true);
        return "createTopic";
    }

    @PostMapping("/topic/create")
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

    @GetMapping("/item/delete/{id}") // TODO: make it delete
    public String deleteItem(@PathVariable Long id, Model model) {
        Optional<Item> item = itemService.findById(id);
        if (item.isEmpty()) {
            logger.error("Item could not be found to delete. Id: {}", id);
            return "redirect:/home";
        }
        itemService.deleteById(id);
        model.addAttribute("isAuthenticated", true);
        Long topicId = item.get().getTopic().getId();
        return "redirect:/items/" + topicId;
    }

    @GetMapping("/topic/delete/{id}") // TODO: make it delete
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

    @PostMapping("/item/create/{id}")
    public String createItem(@PathVariable Long id, Model model, @RequestParam String text) {
        Optional<Topic> topic = topicService.findById(id);
        if (topic.isEmpty()) {
            logger.error("Topic could not be found. This should NOT happen.");
            return "redirect:/home";
        }
        Optional<Item> item = itemService.findLatestItemByTopicId(id);
        if (item.isEmpty()) {
            logger.error("Item could not be found. This should NOT happen.");
            return "redirect:/home";
        }
        Date dateOfLastItem = item.get().getNextAt();
        Instant newInstant = dateOfLastItem.toInstant().plus(Duration.ofDays(1));
        Timestamp newTimestamp = Timestamp.from(newInstant);
        Item newItem = new Item();
        newItem.setNextAt(newTimestamp);
        newItem.setText(text);
        newItem.setTopic(topic.get());
        itemService.save(newItem);
        model.addAttribute("isAuthenticated", true);
        return "redirect:/items/" + id;
    }
}
