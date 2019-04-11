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

    public Link add(String address, int site, int version) {

        ensureCompliance();

        address = address.trim();

        if (address.length() == 0) {
            return null;
        }

        try {
            address = URLDecoder.decode(address, StandardCharsets.UTF_8.name());

        } catch (UnsupportedEncodingException e) {
            // Actually should never happen. Really.
            e.printStackTrace();

            return null;
        }

        Link link = new Link();

        link.setUri(address);
        link.setUsed(false);
        link.setDate(Date.from(Instant.now()));
        link.setSite(site);
        link.setVersion(version);

        return linkRepository.save(link);
    }

    public List<Link> findAll() {
        return linkRepository.findAllByOrderByIdAsc();
    }

    public List<Link> findUnused() {
        return linkRepository.findAllByUsedOrderByIdAsc(false);
    }

    public long getCount() {
        return linkRepository.count();
    }

    public long getUnusedCount() {
        return linkRepository.countByUsed(false);
    }
}
