package io.github.gotonode.gem.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Controller
public class LinkController {

    @Autowired
    private LinkService linkService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("links", linkService.findAll());
        return "index";
    }

    @GetMapping("/fetch")
    public void fetch(@RequestParam(required = false) String key, HttpServletResponse response) {

        if (System.getenv().containsKey("HEROKU")) {
            if (!System.getenv().get("GEMKEY").equals(key.trim())) {
                System.out.println("Incorrect key used. Aborting operation.");
                return;
            }
        }

        System.out.println("Fetching a new link (correct key)");

        Link link = linkService.fetch();

        String uri = link.getUri();

        System.out.println("Returning link: " + link);

        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", uri);
        response.setHeader("Connection", "close");
    }

    @PostMapping("/add")
    public ResponseEntity add(@RequestBody String uri) {

        System.out.println("Got a POST request to add a link: " + uri);

        String json = linkService.add(uri);

        System.out.println("Added a new link: " + json);

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON_UTF8).body(json);
    }
}
