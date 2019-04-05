package io.github.gotonode.gem.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DebugController {

    @Autowired
    private DebugService debugService;

    @GetMapping("/generate")
    public ResponseEntity generate() {

        Link link = debugService.generate();

        Map<Long, String> map = new HashMap<>();
        map.put(link.getId(), link.getUri());

        ObjectMapper om = new ObjectMapper();
        String json = "";

        try {
            json = om.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON_UTF8).body(json);
    }

    @GetMapping("/generate/{num}")
    public ResponseEntity generateMany(@PathVariable int num) {

        List<Link> links = debugService.generate(num);

        Map<Long, String> map = new HashMap<>();

        for (Link link : links) {
            map.put(link.getId(), link.getUri());
        }

        ObjectMapper om = new ObjectMapper();
        String json = "";

        try {
            json = om.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON_UTF8).body(json);
    }

    @GetMapping("/toggle/{id}")
    public String toggle(@PathVariable long id) {

        debugService.toggle(id);

        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable long id) {

        debugService.delete(id);

        return "redirect:/";
    }

    @GetMapping("/reset")
    public String reset(@RequestParam(required = false) String key) {

        if (System.getenv().containsKey("HEROKU")) {
            if (!System.getenv().get("GEMKEY").equals(key.trim())) {
                System.out.println("Invalid key. Will not perform delete operation.");
                return "";
            }
        }

        debugService.reset();

        return "redirect:/";
    }

    @GetMapping("/random")
    public void random(HttpServletResponse response) {

        Link link = debugService.random();

        if (link == null) {
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader("Location", "/");
            response.setHeader("Connection", "close");
            return;
        }

        String uri = link.getUri();

        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", uri);
        response.setHeader("Connection", "close");
    }

    @GetMapping("/fetchDebug")
    public void fetchDebug(HttpServletResponse response) {

        Link link = debugService.fetchDebug();

        String uri = link.getUri();

        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", uri);
        response.setHeader("Connection", "close");
    }

}
