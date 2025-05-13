package com.example.spring_rest_intro;


import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PersonService {

    PersonRepository repository;

    PersonMapper personMapper;

    public PersonService(PersonRepository repository, PersonMapper personMapper) {
        this.personMapper = personMapper;
        this.repository = repository;
    }

    public List<PersonResponseDto> getAllPersons(){
        //List<Person> personList = repository.findAll();
        return personMapper.toDtoList(repository.findAll());
    }

    public PersonResponseDto getPersonByid(Long id){
        Optional<Person> opt = repository.findById(id);

        if (opt.isPresent()){
            return personMapper.toDto(opt.get());
        }


         return null ;


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

    public Map<String, String> addPerson(PersonRequestDto personDto){
        Map<String,String> response = new HashMap<>();

        Person person = personMapper.toEntity(personDto);

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
