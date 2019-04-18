package io.github.gotonode.gem.repository;

import io.github.gotonode.gem.domain.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LinkRepository extends JpaRepository<Link, Long> {
    Link findByUri(String uri);
    Link findFirstByOrderByIdAsc();
    Link findFirstByUsedOrderByIdAsc(boolean used);
    List<Link> findAllByOrderByIdAsc();
    //Link findFirstByOrderByIdAscUsedFalse();

    @Query("SELECT l FROM Link l WHERE l.used = false ORDER BY id ASC")
    Link findFirstUnused();

    List<Link> findAllByUsedOrderByIdAsc(boolean used);

    long countByUsed(boolean used);
}
