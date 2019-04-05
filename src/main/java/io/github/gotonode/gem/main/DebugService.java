package io.github.gotonode.gem.main;

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

    public Link generate() {
        return generate(1).get(0);
    }

    public List<Link> generate(int count) {

        List<Link> links = new ArrayList<>();

        for (int i = 0; i < count; i++) {

            Link link = new Link();

            link.setUri("http://www." + UUID.randomUUID().toString() + ".com/");
            link.setDate(Date.from(Instant.now()));

            links.add(linkRepository.save(link));
        }

        return links;
    }

    public void delete(long id) {
        linkRepository.deleteById(id);
    }

    public void reset() {
        linkRepository.deleteAll();
    }

    public Link toggle(long id) {
        Link link = linkRepository.getOne(id);

        boolean used = link.isUsed();
        used = !used;

        link.setUsed(used);

        return linkRepository.save(link);
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
