package mee.prosject.webpage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Spring Boot testside");
        model.addAttribute("message", "Applikasjonen kjører");
        return "home";
    }
}
