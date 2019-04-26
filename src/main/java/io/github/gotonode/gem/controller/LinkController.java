package io.github.gotonode.gem.controller;

import io.github.gotonode.gem.domain.Link;
import io.github.gotonode.gem.form.LinkData;
import io.github.gotonode.gem.service.LinkService;
import io.github.gotonode.gem.Main;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

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

        if (System.getenv().containsKey("GET_KEY")) {
            if (!System.getenv().get("GET_KEY").equals(key.trim())) {
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
    @ResponseBody
    public String add(@Valid @ModelAttribute LinkData linkData) {

        System.out.println("Got a POST request to add a link: " + linkData);

        String key = linkData.getKey();

        if ((key == null) || (!key.equals(System.getenv("POST_KEY")))) {

            System.out.println("Incorrect posting key.");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "The key was not correct.");
            return jsonObject.toString();

        } else {
            linkData.setKey(null); // For security, we erase this.
        }

        if (linkData.getAddress().trim().isEmpty()) {

            System.out.println("LinkData was not added because Address was empty.");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Address was empty or contained only whitespace characters.");
            return jsonObject.toString();
        }

        Link link = linkService.add(linkData);

        System.out.println("Added a new entry: " + link);

        return new JSONObject(link).toString();
    }

    @GetMapping("/done")
    public String done() {
        return "done";
    }

    @PostMapping("/toggle")
    public String toggle(@RequestParam Long id) {

        Link link = linkService.toggle(id);

        System.out.println("Toggled link: " + link);

        return "redirect:/";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {

        Link link = linkService.delete(id);

        System.out.println("Deleted link: " + link);

        return "redirect:/";
    }
}
