package io.github.gotonode.gem.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LinkService {

    @Autowired
    private LinkRepository linkRepository;

    public Link fetch() {

        Link link = linkRepository.findFirstByOrderByIdAsc();

        if (link == null) {
            return null;
        }

        link.setUsed(true);
        linkRepository.save(link);

        return link;
    }

    public List<Link> findAll() {
        return linkRepository.findAll();
    }

    private void checkForSpace() {

        long count = linkRepository.count();

        if (count < Main.MAX_DATABASE_ENTRIES) {

            // There's still room left in the database.
            return;
        }

        System.out.println("Database is full (" + count + " entries). Removing oldest entries.");

        while (count >= Main.MAX_DATABASE_ENTRIES) {

            Link link = linkRepository.findFirstByOrderByIdAsc();

            System.out.println("Removing link: " + link);

            linkRepository.delete(link);

            count = linkRepository.count();
        }

        System.out.println("Database is in compliance again (" + count + " entries), ready to accept new links.");
    }

    public String add(String uri) {

        checkForSpace();

        uri = uri.replace("uri=", "");

        uri = uri.trim();

        if (uri.length() == 0) {
            System.out.println("Link was not added because it was just an empty string.");
            return "{\"error\":\"The link was empty.\"}";
        }

        Link link = new Link();
        link.setUri(uri);
        link.setUsed(false);
        link.setDate(Date.from(Instant.now()));

        Link savedLink = linkRepository.save(link);

        Map<Long, String> map = new HashMap<>();
        map.put(savedLink.getId(), savedLink.getUri());

        ObjectMapper om = new ObjectMapper();
        String json = "";

        try {
            json = om.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }
}
