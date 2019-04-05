package io.github.gotonode.gem.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Controller
public class MainController {

    @Autowired
    private LinkService linkService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("links", linkService.findAll());
        return "index";
    }

    @GetMapping("/fetch")
    public void fetch(@RequestParam String key, HttpServletResponse response) {

//        if (System.getenv().get("GEMKEY") == null) {
//            System.out.println("Environment variable not found on system.");
//            return;
//        }
//
//        if (!System.getenv().get("GEMKEY").equals(key.trim())) {
//            System.out.println("Incorrect key used. Aborting operation.");
//            return;
//        }

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

        String output = linkService.add(uri);

        if (output == null) {
            System.out.println("Link was not added. Probably it was an empty string?");

            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
        }

        System.out.println("Added a new (formatted) link: " + output);

        return ResponseEntity.status(HttpStatus.OK).body(uri);
    }
}
