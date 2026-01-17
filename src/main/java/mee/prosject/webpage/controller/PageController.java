package mee.prosject.webpage.controller;

import mee.prosject.webpage.model.Page;
import mee.prosject.webpage.service.PageRegistry;
import mee.prosject.webpage.service.PageRenderer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    private final PageRegistry registry;
    private final PageRenderer renderer;

    public PageController(PageRegistry registry, PageRenderer renderer) {
        this.registry = registry;
        this.renderer = renderer;
    }

    // Home page
    @GetMapping("/")
    public String viewHome(Model model) {
        Page page = registry.getBySlug("home");
        if (page == null) return "page-not-found";

        model.addAttribute("title", page.title());
        model.addAttribute("content", renderer.render(page.content()));
        model.addAttribute("pages", registry.getAllPages());
        return "pageView";
    }

    // Show pages by slug
    @GetMapping("/{slug}")
    public String viewPage(@PathVariable String slug, Model model) {
        Page page = registry.getBySlug(slug);
        if (page == null) return "page-not-found";

        model.addAttribute("title", page.title());
        model.addAttribute("content", renderer.render(page.content()));
        model.addAttribute("pages", registry.getAllPages());
        return "pageView";
    }
}