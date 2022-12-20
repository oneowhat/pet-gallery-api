package learn.petgallery.data;

import learn.petgallery.models.EntityEvent;
import learn.petgallery.models.EntityEventType;
import learn.petgallery.models.Pet;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuditingPetRepository implements PetRepository{

    private final JpaPetRepository jpaPetRepository;
    private final EntityEventRepository entityEventRepository;

    public AuditingPetRepository(JpaPetRepository jpaPetRepository,
                                 EntityEventRepository entityEventRepository) {
        this.jpaPetRepository = jpaPetRepository;
        this.entityEventRepository = entityEventRepository;
    }

    @Override
    public List<Pet> findAll() {
        return jpaPetRepository.findAll();
    }

    @Override
    public Pet findById(int petId) {
        return jpaPetRepository.findById(petId).orElse(null);
    }

    @Override
    public Pet save(Pet pet, int appUserId) {
        Pet savedPet = jpaPetRepository.save(pet);
        EntityEvent entityEvent = EntityEvent.createSaveEvent(savedPet, EntityEventType.SAVE, appUserId);
        entityEventRepository.save(entityEvent);
        return savedPet;
    }

    @Override
    public void deleteById(int petId, int appUserId) {
        EntityEvent entityEvent = EntityEvent.createDeleteEvent(petId, Pet.class.getName(), appUserId);
        jpaPetRepository.deleteById(petId);
        entityEventRepository.save(entityEvent);
    }
}
