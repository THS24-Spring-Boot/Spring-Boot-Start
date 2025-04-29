package com.example.spring_rest_intro;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PersonRepository {

    private List<Person> persoList = new ArrayList<>();

    public PersonRepository(List<Person> persoList) {
       initList();
    }

    private void initList(){
        persoList.add(new Person("Arne", "arnes@mail.com"));
        persoList.add(new Person("Bill", "bills@mail.com"));
        persoList.add(new Person("Cesar", "cesar@mail.com"));
        persoList.add(new Person("Didrik", "didrik@mail.com"));
        persoList.add(new Person("Eskil", "eskil@mail.com"));


    }

    public List<Person> getAllPersons(){
        return persoList;
    }

    public void addPerson(Person person){
        persoList.add(person);
    }

    public boolean deleteById(long id){
        return persoList.removeIf(p -> p.getId() == id);
    }
}
