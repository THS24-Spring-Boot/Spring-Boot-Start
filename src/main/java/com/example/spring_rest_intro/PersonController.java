package com.example.spring_rest_intro;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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


    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus(){
        Map<String, String> response = personService.getStatus();

        if (response.containsValue("success")){
            return ResponseEntity.ok(response);
        } else return ResponseEntity.status(500).body(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createUser(@RequestBody Person person){

        Map<String, String> response = personService.addPerson(person);

        if(response.containsValue("success")){
            return ResponseEntity.status(201).body(response);
        } else return ResponseEntity.status(500).body(response);

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
        Person person = personService.getPersonByid(id).orElse(null);

        if(person == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(person);

    }

    @DeleteMapping
    public ResponseEntity<Integer> deleteById(@RequestParam long id){
        personService.deleteById(id);
        return ResponseEntity.ok(1);

    }

}
