package com.example.spring_rest_intro;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PersonService {

    PersonRepository repository;

    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    public List<Person> getAllPersons(){
        return repository.getAllPersons();
    }

    public Person getPersonByid(long id){
        List<Person> persons = repository.getAllPersons();

        return persons
                .stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Map<String, String> getStatus(){
        return repository.getStatus();
    }

    public Map<String, String> addPerson(Person person){
        return repository.addPerson(person);
    }

    public boolean deleteById(int id){
        return repository.deleteById(id);
    }
}
