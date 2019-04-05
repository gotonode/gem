package io.github.gotonode.gem.main;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LinkRepository extends JpaRepository<Link, Long> {
    Link findByUri(String uri);
    Link findFirstByOrderByIdAsc();
    Link findFirstByUsedOrderByIdAsc(boolean used);
    //Link findFirstByOrderByIdAscUsedFalse();

    @Query("SELECT l FROM Link l WHERE l.used = false ORDER BY id ASC")
    Link findFirstUnused();
}
