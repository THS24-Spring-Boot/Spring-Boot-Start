package com.example.spring_rest_intro;

import org.springframework.stereotype.Service;

import java.util.List;

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

    public void addPerson(Person person){
        repository.addPerson(person);
    }

    public boolean deleteById(long id){
        return repository.deleteById(id);
    }
}
