package com.example.spring_rest_intro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PersonMapper {

    @Autowired
    PostRepository postRepository;

    public PersonMapper(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PersonResponseDto toDto(Person person){

        PersonResponseDto dto = new PersonResponseDto();
        dto.setId(person.getId());
        dto.setName(person.getName());

        dto.setBio(person.getProfile().getBio());

        List<Post> posts = postRepository.findByPerson_id(person.getId());
        dto.setPosts(posts);




        return dto;

    }

    public Person toEntity(PersonRequestDto dto){
        Person person = new Person();
        person.setName(dto.getName());
        person.setEmail(dto.getEmail());

        Profil profile = new Profil();
        profile.setBio(dto.getBio());
        profile.setAge(dto.getAge());
        profile.setShoeSize(dto.getShoeSize());

        person.setProfile(profile);

        return person;
    }

    public List<PersonResponseDto> toDtoList(List<Person> personList){

        List<PersonResponseDto> dtoList = new ArrayList<>();
        for (Person p : personList){
            dtoList.add(toDto(p));
        }
         return dtoList;

//        return personList
//                .stream()
//                .map(this::toDto)
//                .toList();

    }
}
