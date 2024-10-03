package ru.flynt3650.project.first_rest_app.controllers;

import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.flynt3650.project.first_rest_app.dto.PersonDto;
import ru.flynt3650.project.first_rest_app.models.Person;
import ru.flynt3650.project.first_rest_app.services.PeopleService;
import ru.flynt3650.project.first_rest_app.util.PersonErrorResponse;
import ru.flynt3650.project.first_rest_app.util.PersonNotCreatedException;
import ru.flynt3650.project.first_rest_app.util.PersonNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;
    private final ModelMapper modelMapper;

    @Autowired
    public PeopleController(PeopleService peopleService, ModelMapper modelMapper) {
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public List<PersonDto> getPeople() {
        return peopleService
                .findAll()
                .stream()
                .map(this::convertToPersonDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PersonDto getPerson(@PathVariable("id") int id) {
        return convertToPersonDto(peopleService.findOne(id));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid PersonDto personDto,
                                             @NotNull BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (var error : errors)
                errorMessage
                        .append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append(";");

            throw new PersonNotCreatedException(errorMessage.toString());
        }

        peopleService.save(convertToPerson(personDto));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private @NotNull ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e) {
        PersonErrorResponse response = new PersonErrorResponse(
                "Person with this id wasn't found.", System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private @NotNull ResponseEntity<PersonErrorResponse> handleException(@NotNull PersonNotCreatedException e) {
        PersonErrorResponse response = new PersonErrorResponse(
                e.getMessage(), System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private @NotNull Person convertToPerson(@NotNull PersonDto personDto) {
        return modelMapper.map(personDto, Person.class);
    }

    private PersonDto convertToPersonDto(Person person) {
        return modelMapper.map(person, PersonDto.class);
    }
}
