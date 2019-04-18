package io.github.gotonode.gem.service;

import io.github.gotonode.gem.domain.Link;
import io.github.gotonode.gem.repository.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class DebugService {

    @Autowired
    private LinkRepository linkRepository;

    private Random random = new Random();

    public List<Link> generate(int amount) {

        List<Link> links = new ArrayList<>();

        for (int i = 0; i < amount; i++) {

            Link link = new Link();

            link.setUri("http://www." + UUID.randomUUID().toString() + ".com/");
            link.setDate(Date.from(Instant.now()));

            links.add(linkRepository.save(link));
        }

        return links;
    }

    public void reset() {
        linkRepository.deleteAll();
    }

    public Link random() {

        List<Link> links = linkRepository.findAll();

        if (links.size() == 1) {
            return links.get(0);
        } else if (links.size() == 0) {
            return null;
        }

        long id = random.nextInt(links.size());

        return links.get((int) id);
    }

    /**
     * Fetches the next item but does NOT mark it as used.
     *
     * @return The next link from the database.
     */
    public Link fetchDebug() {
        return linkRepository.findFirstByUsedOrderByIdAsc(false);
    }
}
