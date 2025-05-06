package com.example.spring_rest_intro;


import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PersonService {

    PersonRepository repository;

    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    public List<Person> getAllPersons(){
        return repository.findAll();
    }

    public Optional<Person> getPersonByid(Long id){
         return repository.findById(id);


//        return persons
//                .stream()
//                .filter(p -> p.getId() == id)
//                .findFirst()
//                .orElse(null);
    }

    public Map<String, String> getStatus(){
        Map<String,String> response = new HashMap<>();

        try {
            repository.count();
            response.put("status", "success");
            response.put("message", "table exists");
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Database error: " + e.getMessage());
            return response;
        }

    }

    public Map<String, String> addPerson(Person person){
        Map<String,String> response = new HashMap<>();

        try {
            repository.save(person);
            response.put("status", "success");
            response.put("message", "new person added:" + person);
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Database error: " + e.getMessage());
            return response;
        }

    }

    public void deleteById(Long id){

        repository.deleteById(id);
    }
}
