package io.github.gotonode.gem.controller;

import io.github.gotonode.gem.domain.Link;
import io.github.gotonode.gem.form.LinkData;
import io.github.gotonode.gem.service.LinkService;
import io.github.gotonode.gem.Main;
import org.hibernate.validator.internal.constraintvalidators.hv.URLValidator;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.URL;
import java.time.Instant;
import java.util.Date;
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

    @GetMapping(value = "/stats", produces = "application/json")
    @ResponseBody
    public String stats() {

        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObjectMessage = new JSONObject();
        jsonObjectMessage.put("new", linkService.getUnusedCount());
        jsonObjectMessage.put("all", linkService.getCount());
        jsonObject.put("stats", jsonObjectMessage);

        System.out.println(jsonObject);

        return jsonObject.toString();
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

    @PostMapping(value = "/add", produces = "application/json")
    @ResponseBody
    public String add(@Valid @ModelAttribute LinkData linkData) {

        final String key = linkData.getKey();

        linkData.setKey("[HIDDEN]"); // For security, we hide this.

        System.out.println("Got a POST request to add a link: " + linkData);

        if ((key == null) || (!key.equals(System.getenv("POST_KEY")))) {

            String msg = "The key was not correct.";

            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObjectMessage = new JSONObject();
            jsonObjectMessage.put("code", Main.CODE_ERROR_KEY_INCORRECT);
            jsonObjectMessage.put("message", msg);
            jsonObject.put("error", jsonObjectMessage);

            System.out.println(jsonObject);

            return jsonObject.toString();
        }

        try {
            // TODO: Validate URL by regex?
            new URL(linkData.getAddress()).toURI();
        }
        catch (Exception e) {
            String msg = "URL is not valid.";

            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObjectMessage = new JSONObject();
            jsonObjectMessage.put("code", Main.CODE_MESSAGE_ALREADY_RECEIVED);
            jsonObjectMessage.put("message", msg);
            jsonObject.put("message", jsonObjectMessage);

            System.out.println(jsonObject);

            return jsonObject.toString();
        }

        Link latestLink = linkService.findLatestByAddress(linkData.getAddress());

        if (latestLink != null) {

            final long millisecondsInFortnight = 1209600000;

            Date dateNow = Date.from(Instant.now());
            Long millisNow = dateNow.getTime();

            Date dateLatestLink = latestLink.getDate();
            dateLatestLink.setTime(dateLatestLink.getTime() + millisecondsInFortnight);
            Long millisLink = dateLatestLink.getTime();

            if (millisNow.compareTo(millisLink) < 0) {

                String msg = "Already received less than 14 days ago.";

                JSONObject jsonObject = new JSONObject();
                JSONObject jsonObjectMessage = new JSONObject();
                jsonObjectMessage.put("code", Main.CODE_MESSAGE_ALREADY_RECEIVED);
                jsonObjectMessage.put("message", msg);
                jsonObject.put("message", jsonObjectMessage);

                System.out.println(jsonObject);

                return jsonObject.toString();
            }
        }

        Link link = linkService.add(linkData);

        System.out.println(link);

        return new JSONObject(link).toString();
    }

    @GetMapping("/done")
    public String done() {
        return "done";
    }

    @PostMapping("/id/{id}/toggle")
    public String toggle(@PathVariable String id) {

        Long idLong = Long.parseLong(id);

        Link link = linkService.toggle(idLong);

        System.out.println("Toggled link: " + link);

        return "redirect:/";
    }

    @PostMapping("/id/{id}/delete")
    public String delete(@PathVariable String id) {

        Long idLong = Long.parseLong(id);

        Link link = linkService.delete(idLong);

        System.out.println("Deleted link: " + link);

        return "redirect:/";
    }
}
