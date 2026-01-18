package mee.prosject.webpage.controller;

import mee.prosject.webpage.model.Page;
import mee.prosject.webpage.service.PageContentService;
import mee.prosject.webpage.service.PageRegistry;
import mee.prosject.webpage.service.PageRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    private final PageRegistry registry;
    private final PageRepository repository;
    private final PageContentService pageContentService;

    public PageController(PageRegistry registry, PageRepository repository, PageContentService pageContentService) {
        this.registry = registry;
        this.repository = repository;
        this.pageContentService = pageContentService;
    }

    // Home page
    @GetMapping("/")
    public String viewHome(Model model) {
        Page page = repository.getPageBySlug("home");
        if (page == null) return "page-not-found";

        model.addAttribute("title", page.title());
        model.addAttribute("content", page.html());
        model.addAttribute("pages", registry.getAllPages());
        return "homePage";
    }

    // Show pages by slug
    @GetMapping("/{slug}")
    public String viewPage(@PathVariable String slug, Model model) {
        Page page = repository.getPageBySlug(slug);
        if (page == null) return "page-not-found";

        model.addAttribute("title", page.title());
        model.addAttribute("content", page.html());
        model.addAttribute("pages", registry.getAllPages());
        return "wikiPage";
    }
    // Show page for search
    @GetMapping("/search")
    public String viewSearch(@RequestParam("q") String searchTerm, Model model) {
        System.out.println("Trying to access " + searchTerm);
        model.addAttribute("title", "Search: " + searchTerm);
        model.addAttribute("results", pageContentService.searchFor(searchTerm));
        model.addAttribute("pages", registry.getAllPages());
        return "searchPage";
    }
}