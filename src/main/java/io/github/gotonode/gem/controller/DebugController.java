package io.github.gotonode.gem.controller;

import io.github.gotonode.gem.domain.Link;
import io.github.gotonode.gem.service.DebugService;
import io.github.gotonode.gem.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Controller
public class DebugController {

    @Autowired
    private DebugService debugService;

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
        redirect(response, debugService.random());
    }

    @GetMapping("/debug/fetch")
    public void fetchDebug(HttpServletResponse response) {
        redirect(response, debugService.fetchDebug());
    }

    private void redirect(HttpServletResponse response, Link link) {

        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Connection", "close");
        response.setHeader("Cache-Control", Main.CACHE_CONTROL);

        if (link == null) {
            response.setHeader("Location", "/done");
        } else {
            response.setHeader("Location", link.getUri());
        }
    }

}
