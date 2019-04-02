package io.github.gotonode.gem.controller;

import io.github.gotonode.gem.domain.SampleItem;
import io.github.gotonode.gem.repository.SampleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class SampleController {

    @Autowired
    private SampleRepository sampleRepository;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("list", sampleRepository.findAll());
        return "index";
    }

    @PostMapping("/")
    public String add(@RequestParam String name) {

        SampleItem sampleItem = new SampleItem();
        sampleItem.setName(name);

        sampleRepository.save(sampleItem);

        return "redirect:/";
    }

    @GetMapping("/params")
    @ResponseBody
    public String params(@RequestParam Map<String, String> params) {
        return params.keySet().toString();
    }

    @GetMapping("/add")
    @ResponseBody
    public String add(@RequestParam int first, @RequestParam int second) {

        int sum = first + second;
        return String.valueOf(sum);
    }

    @GetMapping("/test")
    @ResponseBody
    public String test() {

        SampleItem sampleItem = new SampleItem(2, "Cannot see me.");

        sampleItem.setName("This is a test.");
        sampleItem.setId(1);

        return sampleItem.toString();
    }
}
