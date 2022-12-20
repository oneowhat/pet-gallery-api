package learn.petgallery.data;

import learn.petgallery.models.Pet;

import java.util.List;

public interface PetRepository {

    List<Pet> findAll();
    Pet findById(int petId);

    Pet save(Pet pet, int appUserId);

    void deleteById(int petId, int appUserId);
}
