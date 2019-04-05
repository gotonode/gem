package io.github.gotonode.gem.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class DebugService {

    @Autowired
    private LinkRepository linkRepository;

    public void generate(int count) {

        for (int i = 0; i < count; i++) {

            Link link = new Link();
            link.setUri("http://www." + UUID.randomUUID().toString() + ".com/");
            link.setDate(Date.from(Instant.now()));
            linkRepository.save(link);
        }
    }

    public long getCount() {
        return linkRepository.count();
    }

    public void delete(long id) {
        linkRepository.deleteById(id);
    }

    public void toggle(long id) {
        Link link = linkRepository.getOne(id);

        boolean used = link.isUsed();
        used = !used;

        link.setUsed(used);

        linkRepository.save(link);
    }

    public Link random() {

        List<Link> links = linkRepository.findAll();
        Random random = new Random();

        if (links.size() == 1) {
            return links.get(0);
        }

        long id = random.nextInt(links.size() - 1) + 1;

        return links.get((int) id);
    }
}
