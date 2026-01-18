package mee.prosject.webpage.controller;

import mee.prosject.webpage.model.Page;
import mee.prosject.webpage.service.PageRegistry;
import mee.prosject.webpage.service.PageRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    private final PageRegistry registry;
    private final PageRepository repository;

    public PageController(PageRegistry registry, PageRepository repository) {
        this.registry = registry;
        this.repository = repository;
    }

    // Home page
    @GetMapping("/")
    public String viewHome(Model model) {
        Page page = repository.getPageBySlug("home");
        if (page == null) return "page-not-found";

        model.addAttribute("title", page.title());
        model.addAttribute("content", page.html());
        model.addAttribute("pages", registry.getAllPages());
        return "pageView";
    }

    // Show pages by slug
    @GetMapping("/{slug}")
    public String viewPage(@PathVariable String slug, Model model) {
        Page page = repository.getPageBySlug(slug);
        if (page == null) return "page-not-found";

        model.addAttribute("title", page.title());
        model.addAttribute("content", page.html());
        model.addAttribute("pages", registry.getAllPages());
        return "pageView";
    }
}