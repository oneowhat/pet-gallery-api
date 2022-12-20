package learn.petgallery.data;

import learn.petgallery.models.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaPetRepository extends JpaRepository<Pet, Integer> {
    Pet save(Pet pet, int appUserId);

    void deleteById(int petId, int appUserId);
}
