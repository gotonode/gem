package io.github.gotonode.gem.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gotonode.gem.domain.Link;
import io.github.gotonode.gem.form.LinkForm;
import io.github.gotonode.gem.service.LinkService;
import io.github.gotonode.gem.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class LinkController {

    @Autowired
    private LinkService linkService;

    @Autowired
    private HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model, @RequestParam(required = false) Boolean showAll) {

        if (showAll == null) {
            if (httpSession.getAttribute("showAll") == null) {
                showAll = true;
                httpSession.setAttribute("showAll", showAll);
            } else {
                showAll = (Boolean) httpSession.getAttribute("showAll");
            }
        } else {
            httpSession.setAttribute("showAll", showAll);
        }

        List<Link> links;

        if (showAll) {
            links = linkService.findAll();
        } else {
            links = linkService.findUnused();
        }

        model.addAttribute("showAll", showAll);
        model.addAttribute("links", links);
        model.addAttribute("entries", linkService.getCount());
        model.addAttribute("unused", linkService.getUnusedCount());

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
            response.setHeader("Cache-Control", Main.CACHE_CONTROL);

            return;
        }

        String uri = link.getUri();

        System.out.println("Returning entry: " + link);

        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", uri);
        response.setHeader("Connection", "close");
        response.setHeader("Cache-Control", Main.CACHE_CONTROL);
    }

    @PostMapping("/add")
    public ResponseEntity add(@Valid @ModelAttribute LinkForm linkForm) {

        System.out.println("Got a POST request to add a link with URL: " + linkForm);

        Link link = linkService.add(linkForm);

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

    @PostMapping("/toggle")
    public String toggle(@RequestParam Long id) {

        Link link = linkService.toggle(id);

        System.out.println("Toggled the used-state of: " + link);

        return "redirect:/";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {

        linkService.delete(id);

        System.out.println("Deleted the link with ID " + id + ".");

        return "redirect:/";
    }
}
