package com.example.spring_rest_intro;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {


    private PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/hello")
    public String sayHello(){
        return "hello Spring!";
    }

    @PostMapping
    public ResponseEntity<Person> createUser(@RequestBody Person person){


        if(person == null){
            return ResponseEntity.badRequest().build();
        }
        personService.addPerson(person);
        return ResponseEntity.ok(person);

    }

    @GetMapping
    public ResponseEntity<List<Person>> getAll(){
        List<Person> persons = personService.getAllPersons();

        if(persons.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(persons);

    }
//    @GetMapping("/q")
//    public ResponseEntity<Person> getPersonById(@RequestParam long id){
//        Person person = personService.getPersonByid(id);
//
//        if(person == null){
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(person);
//
//    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable long id){
        Person person = personService.getPersonByid(id);

        if(person == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(person);

    }

    @DeleteMapping
    public ResponseEntity<Long> deleteById(@RequestParam long id){
        if(personService.deleteById(id)){
            return ResponseEntity.ok(id);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

}
