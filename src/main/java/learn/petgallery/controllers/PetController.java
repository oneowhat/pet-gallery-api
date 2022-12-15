package learn.petgallery.controllers;

import learn.petgallery.domain.PetService;
import learn.petgallery.domain.Result;
import learn.petgallery.domain.ResultType;
import learn.petgallery.models.Pet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pet")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public List<Pet> findAll() {
        return petService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable int id) {
        Pet pet = petService.findById(id);
        if (pet != null) {
            return new ResponseEntity<>(pet, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Valid Pet pet, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return buildInvalidResponse(bindingResult);
        }
        Result<Pet> result = petService.add(pet);
        if (result.isSuccess()) {
            return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(result.getMessages(), HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable int id,
                                         @RequestBody @Valid Pet pet,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return buildInvalidResponse(bindingResult);
        }
        if (id != pet.getPetId()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Result<Void> result = petService.update(pet);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else if (result.getResultType() == ResultType.NOT_FOUND) {
            return new ResponseEntity<>(result.getMessages(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result.getMessages(), HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable int id) {
        Result<Void> result = petService.deleteById(id);
        if (result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result.getMessages(), HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<Object> buildInvalidResponse(BindingResult bindingResult) {
        return new ResponseEntity<>(bindingResult.getAllErrors().stream()
                .map(i -> i.getDefaultMessage())
                .collect(Collectors.toList()), HttpStatus.BAD_REQUEST);
    }
}
