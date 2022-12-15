package learn.petgallery.domain;

import learn.petgallery.data.PetRepository;
import learn.petgallery.models.Pet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class PetServiceTest {

    @MockBean
    PetRepository petRepository;

    @Autowired
    PetService petService;

    @Test
    void shouldFindAllPets() {
        List<Pet> pets = List.of(
                new Pet(1, "Fluffy", "https://example.com/image-1"),
                new Pet(2, "Smoke", "https://example.com/image-2"),
                new Pet(3, "Magpie", "https://example.com/image-3")
        );

        when(petRepository.findAll()).thenReturn(pets);

        List<Pet> actual = petService.findAll();

        assertEquals(pets, actual);
    }

    @Test
    void shouldFindFluffyById() {
        Pet fluffy = new Pet(1, "Fluffy", "https://example.com/image-1");

        when(petRepository.findById(1)).thenReturn(Optional.of(fluffy));

        Pet actual = petService.findById(1);

        assertEquals(fluffy, actual);
    }

    @Test
    void shouldAddValidPet() {
        Pet petToAdd = new Pet(0, "Atilla", "https://example.com/image-4");
        Pet petAdded = new Pet(4, "Atilla", "https://example.com/image-4");

        when(petRepository.save(petToAdd)).thenReturn(petAdded);

        Result<Pet> result = petService.add(petToAdd);

        assertTrue(result.isSuccess());
        assertEquals(petAdded, result.getPayload());
    }

    @Test
    void shouldNotAddPetWithBlankName() {
        Pet petToAdd = new Pet(0, " ", "https://example.com/image-4");

        Result<Pet> result = petService.add(petToAdd);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Pet name is required.", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddPetWithNullName() {
        Pet petToAdd = new Pet(0, null, "https://example.com/image-4");

        Result<Pet> result = petService.add(petToAdd);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Pet name is required.", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddPetWithBlankUrl() {
        Pet petToAdd = new Pet(0, "Atilla", " ");

        Result<Pet> result = petService.add(petToAdd);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Pet image URL must be a URL.", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddPetWithNullUrl() {
        Pet petToAdd = new Pet(0, "Atilla", null);

        Result<Pet> result = petService.add(petToAdd);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Pet image URL is required.", result.getMessages().get(0));
    }

    @Test
    void shouldNotAddPetWithMalformedUrl() {
        Pet petToAdd = new Pet(0, "Atilla", "this.isnot.a-valid url");

        Result<Pet> result = petService.add(petToAdd);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Pet image URL must be a URL.", result.getMessages().get(0));
    }

    @Test
    void shouldUpdateValidPet() {
        Pet petToUpdate = new Pet(4, "New name", "https://localhost:3000/image.png");

        when(petRepository.findById(4)).thenReturn(Optional.of(petToUpdate));

        Result<Void> result = petService.update(petToUpdate);

        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotUpdateNotExistingPet() {
        Pet petToUpdate = new Pet(4, "New name", "https://localhost:3000/image.png");

        when(petRepository.findById(4)).thenReturn(Optional.empty());

        Result<Void> result = petService.update(petToUpdate);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Pet id: '4' not found.", result.getMessages().get(0));
    }

    @Test
    void shouldDeleteExistingById() {

        Pet petToDelete = new Pet(4, "New name", "https://localhost:3000/image.png");

        when(petRepository.findById(4)).thenReturn(Optional.of(petToDelete));

        Result<Void> result = petService.deleteById(4);

        assertTrue(result.isSuccess());
    }

    @Test
    void shouldNotDeleteNotExistingPet() {
        when(petRepository.findById(4)).thenReturn(Optional.empty());

        Result<Void> result = petService.deleteById(4);

        assertFalse(result.isSuccess());
        assertEquals(1, result.getMessages().size());
        assertEquals("Pet id: '4' not found.", result.getMessages().get(0));
    }

}