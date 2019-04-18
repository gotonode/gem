package io.github.gotonode.gem.controller;

import io.github.gotonode.gem.domain.Link;
import io.github.gotonode.gem.service.DebugService;
import io.github.gotonode.gem.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class DebugController {

    @Autowired
    private DebugService debugService;

    @RequestMapping(value = "/debug/generateJson", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public List<Link> generateJson(@RequestParam Integer amount) {
        if (amount == null) {
            return debugService.generate(1);
        } else {
            return debugService.generate(amount);
        }
    }

    @PostMapping("/debug/generate")
    public String generate(@RequestParam Integer amount) {
        if (amount == null) {
            debugService.generate(1);
        } else {
            debugService.generate(amount);
        }

        return "redirect:/";
    }

    @PostMapping("/debug/reset")
    @ResponseBody
    public String reset() {

        // The following feature has been disabled since it's most likely
        // no longer needed. It will remain here for the foreseeable future.

        if (System.getenv("HEROKU") != null) {

            // debugService.reset();
            return "This feature has been disabled. Remove individual links as needed.";

        } else {

            // debugService.reset();
            return "This feature has been disabled. Remove individual links as needed or delete the database.";
        }
    }

    @GetMapping("/debug/random")
    public void random(HttpServletResponse response) {

        Link link = debugService.random();

        if (link == null) {
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader("Location", "/done");
            response.setHeader("Connection", "close");
            response.setHeader("Cache-Control", Main.CACHE_CONTROL);
            return;
        }

        String uri = link.getUri();

        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", uri);
        response.setHeader("Connection", "close");
        response.setHeader("Cache-Control", Main.CACHE_CONTROL);
    }

    @GetMapping("/debug/fetch")
    public void fetchDebug(HttpServletResponse response) {

        Link link = debugService.fetchDebug();

        if (link == null) {
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader("Location", "/done");
            response.setHeader("Connection", "close");
            response.setHeader("Cache-Control", Main.CACHE_CONTROL);

            return;
        }

        String uri = link.getUri();

        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", uri);
        response.setHeader("Connection", "close");
        response.setHeader("Cache-Control", Main.CACHE_CONTROL);
    }

}
