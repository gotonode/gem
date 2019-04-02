package io.github.gotonode.gem.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

import java.util.UUID;

@Controller
public class MainController {

    @Autowired
    private LinkRepository sampleRepository;

    @GetMapping("/")
    public String index(Model model) throws URISyntaxException {
        Link link = new Link();
        link.setUri("http://www." + UUID.randomUUID().toString() + ".com/");

        sampleRepository.save(link);
        return "index";
    }

    @GetMapping("/fetch")
    public String fetch(@RequestParam String key, Model model) {
        System.out.println(key);

        // TODO: Ineffective!
        Link link = sampleRepository.findAll().get(0);
        sampleRepository.delete(link);
        model.addAttribute("link", link.getUri());

        return "redirect: " + link.getUri();
    }

    @PostMapping("/add")
    public String add(@RequestParam String uri) throws URISyntaxException {

        Link link = new Link();
        link.setUri(uri);

        sampleRepository.save(link);

        return "redirect:/";
    }
}
