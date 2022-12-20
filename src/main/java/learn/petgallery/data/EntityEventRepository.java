package learn.petgallery.data;

import learn.petgallery.models.EntityEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityEventRepository extends JpaRepository<EntityEvent, Integer> {
}
