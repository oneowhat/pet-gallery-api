package learn.petgallery.domain;

import learn.petgallery.data.PetRepository;
import learn.petgallery.models.Pet;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final Validator validator;

    public PetService(PetRepository petRepository, Validator validator) {
        this.petRepository = petRepository;
        this.validator = validator;
    }

    public List<Pet> findAll() {
        return petRepository.findAll();
    }

    public Pet findById(int petId) {
        return petRepository.findById(petId).orElse(null);
    }

    public Result<Pet> add(Pet pet) {
        Result<Pet> result = validate(pet);
        if (!result.isSuccess()) {
            return result;
        }

        pet = petRepository.save(pet);
        result.setPayload(pet);
        return result;
    }

    public Result<Void> update(Pet pet) {
        Result<Void> result = validate(pet);
        if (!result.isSuccess()) {
            return result;
        }
        if (findById(pet.getPetId()) != null) {
            petRepository.save(pet);
            return result;
        }
        result.addMessage(String.format("Pet id: '%s' not found.", pet.getPetId()), ResultType.NOT_FOUND);
        return result;
    }

    public Result<Void> deleteById(int petId) {
        Result<Void> result = new Result<>();
        if (findById(petId) != null) {
            petRepository.deleteById(petId);
            return result;
        }
        result.addMessage(String.format("Pet id: '%s' not found.", petId), ResultType.NOT_FOUND);
        return result;
    }

    private <T> Result<T> validate(Pet pet) {
        Result<T> result = new Result<>();

        if (pet == null) {
            result.addMessage("Pet cannot be null.", ResultType.INVALID);
            return result;
        }

        var violations = validator.validate(pet);
        for (ConstraintViolation<Pet> violation : violations) {
            result.addMessage(violation.getMessage(), ResultType.INVALID);
        }

        return result;
    }
}
