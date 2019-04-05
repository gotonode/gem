package io.github.gotonode.gem.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class LinkService {

    @Autowired
    private LinkRepository linkRepository;

    public Link fetch() {

        // TODO: Ineffective!
        Link link = linkRepository.findAll().get(0);
        link.setUsed(true);
        linkRepository.save(link);

        return link;
    }

    public List<Link> findAll() {
        return linkRepository.findAll();
    }

    public String add(String uri) {

        uri = uri.replace("uri=", "");

        uri = uri.trim();

        Link link = new Link();
        link.setUri(uri);
        link.setUsed(false);
        link.setDate(Date.from(Instant.now()));

        linkRepository.save(link);

        return uri;
    }
}
