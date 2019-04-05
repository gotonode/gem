package io.github.gotonode.gem.main;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkRepository extends JpaRepository<Link, Long> {
    Link findByUri(String uri);
    Link findFirstByOrderByIdAsc();
}
