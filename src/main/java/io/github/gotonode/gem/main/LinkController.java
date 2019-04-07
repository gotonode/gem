package io.github.gotonode.gem.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LinkController {

    @Autowired
    private LinkService linkService;

    @GetMapping("/")
    public String index(Model model) {
        List<Link> links = linkService.findAll();

        model.addAttribute("links",links);
        model.addAttribute("entries", links.size());

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

        System.out.println("Fetching a new entry (correct key)");

        Link link = linkService.fetch();

        if (link == null) {
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader("Location", "/done");
            response.setHeader("Connection", "close");

            return;
        }

        String uri = link.getUri();

        System.out.println("Returning entry: " + link);

        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", uri);
        response.setHeader("Connection", "close");
    }

    @PostMapping("/add")
    public ResponseEntity add(@RequestBody String uri) {

        System.out.println("Got a POST request to add a link with URL: " + uri);

        Link link = linkService.add(uri);

        if (link == null) {
            System.out.println("Link was not added because it was just an empty string.");

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body("{\"error\":\"The link was empty.\"}");
        }

        Map<Long, String> map = new HashMap<>();
        map.put(link.getId(), link.getUri());

        ObjectMapper om = new ObjectMapper();
        String json = "";

        try {
            json = om.writeValueAsString(map);

        } catch (JsonProcessingException e) {
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body("{\"error\":\"Constructing JSON has failed.\"}");
        }

        System.out.println("Added a new entry: " + link);

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(json);
    }

    @GetMapping("/done")
    public String done() {
        return "done";
    }
}
