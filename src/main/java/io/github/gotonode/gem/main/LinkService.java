package io.github.gotonode.gem.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class LinkService {

    @Autowired
    private LinkRepository linkRepository;

    public Link fetch() {

        Link link = linkRepository.findFirstByUsedOrderByIdAsc(false);

        if (link == null) {
            return null;
        }

        link.setUsed(true);
        linkRepository.save(link);

        return link;
    }

    public List<Link> findAll() {
        return linkRepository.findAllByOrderByIdAsc();
    }

    private void ensureCompliance() {

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

    public Link add(String uri) {

        ensureCompliance();

        uri = uri.replace("uri=", "");

        uri = uri.trim();

        if (uri.length() == 0) {
            return null;
        }

        try {
            uri = URLDecoder.decode(uri, StandardCharsets.UTF_8.name());

        } catch (UnsupportedEncodingException e) {
            // Actually should never happen. Really.
            e.printStackTrace();

            return null;
        }

        Link link = new Link();

        link.setUri(uri);
        link.setUsed(false);
        link.setDate(Date.from(Instant.now()));

        return linkRepository.save(link);
    }
}
