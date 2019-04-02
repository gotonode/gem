package io.github.gotonode.gem.repository;

import io.github.gotonode.gem.domain.SampleItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleRepository extends JpaRepository<SampleItem, Long> {
}
