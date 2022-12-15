package learn.petgallery.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import learn.petgallery.data.PetRepository;
import learn.petgallery.models.Pet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PetControllerTest {

    @MockBean
    PetRepository petRepository;

    @Autowired
    MockMvc mvc;

    @Test
    void shouldFindAllAndReturnHttp200() throws Exception {

        List<Pet> pets = List.of(
                new Pet(1, "Fluffy", "https://example.com/image-1.png"),
                new Pet(2, "Smoke", "https://example.com/image-2.jpg"),
                new Pet(3, "Magpie", "https://example.com/image-3.jpg")
        );

        when(petRepository.findAll()).thenReturn(pets);

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJson = objectMapper.writeValueAsString(pets);

        mvc.perform(get("/api/pet"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void shouldFindByIdAndReturnHttp200() throws Exception {

        Pet pet = new Pet(3, "Fluffy", "https://example.com/image-1.png");

        when(petRepository.findById(3)).thenReturn(Optional.of(pet));

        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJson = objectMapper.writeValueAsString(pet);

        mvc.perform(get("/api/pet/3"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void findByIdShouldReturn404ForMissingPet() throws Exception {
        mvc.perform(get("/api/pet/3"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAddValidPetAndReturnHttp201() throws Exception {
        Pet pet = new Pet(0, "Fritzy", "https://example.com/image-33.png");
        Pet createdPet = new Pet(4, "Fritzy", "https://example.com/image-33.png");

        when(petRepository.save(pet)).thenReturn(createdPet);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonIn = objectMapper.writeValueAsString(pet);
        String expectedJson = objectMapper.writeValueAsString(createdPet);

        var request = post("/api/pet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn);

        mvc.perform(request)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotAddInvalidPetAndReturnHttp400() throws Exception {
        Pet pet = new Pet(0, "", "https://example.com/image-33.png");
        List<String> errors = List.of("Pet name is required.");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonIn = objectMapper.writeValueAsString(pet);
        String expectedJson = objectMapper.writeValueAsString(errors);

        var request = post("/api/pet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn);

        mvc.perform(request)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateValidPetAndReturnHttp204() throws Exception {
        Pet pet = new Pet(4, "Fritzy", "https://example.com/image-33.png");

        when(petRepository.findById(4)).thenReturn(Optional.of(pet));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonIn = objectMapper.writeValueAsString(pet);

        var request = put("/api/pet/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn);

        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotUpdatePetIfIdsDoNotMatchAndReturnHttp405() throws Exception {
        Pet pet = new Pet(4, "Fritzy", "https://example.com/image-33.png");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonIn = objectMapper.writeValueAsString(pet);

        var request = put("/api/pet/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn);

        mvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    void shouldNotUpdateInvalidPetAndReturnHttp400() throws Exception {
        Pet pet = new Pet(4, "", "https://example.com/image-33.png");
        List<String> errors = List.of("Pet name is required.");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonIn = objectMapper.writeValueAsString(pet);
        String expectedJson = objectMapper.writeValueAsString(errors);

        var request = put("/api/pet/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn);

        mvc.perform(request)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotUpdateMissingPetAndReturnHttp404() throws Exception {
        Pet pet = new Pet(4, "Fritzy", "https://example.com/image-33.png");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonIn = objectMapper.writeValueAsString(pet);

        var request = put("/api/pet/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonIn);

        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeletePetAndReturnHttp204() throws Exception {
        when(petRepository.findById(4)).thenReturn(Optional.of(new Pet()));

        var request = delete("/api/pet/4");

        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotDeleteMissingPetAndReturnHttp404() throws Exception {
        var request = delete("/api/pet/4");

        mvc.perform(request)
                .andExpect(status().isNotFound());
    }
}