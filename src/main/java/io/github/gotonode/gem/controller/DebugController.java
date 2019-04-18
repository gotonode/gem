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
    public String reset() {

        debugService.reset();

        return "redirect:/";
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
