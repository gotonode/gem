package io.github.gotonode.gem.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;

@Controller
public class DebugController {

    @Autowired
    private DebugService debugService;

    @GetMapping("/generate")
    public ResponseEntity generate() {

        debugService.generate(1);

        long count = debugService.getCount();

        return ResponseEntity.status(HttpStatus.OK).body(count);
    }

    @GetMapping("/generate/{num}")
    public ResponseEntity generateMany(@PathVariable int num) {

        debugService.generate(num);

        long count = debugService.getCount();

        return ResponseEntity.status(HttpStatus.OK).body(count);
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

    @GetMapping("/random")
    public void random(HttpServletResponse response) {

        Link link = debugService.random();

        String uri = link.getUri();

        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", uri);
        response.setHeader("Connection", "close");
    }
}
