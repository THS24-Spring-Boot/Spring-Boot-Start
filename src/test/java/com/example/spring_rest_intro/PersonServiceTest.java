package com.example.spring_rest_intro;

import org.hibernate.mapping.Any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PersonServiceTest {

    private PersonService service;

    private PersonRepository repo;

    private PersonMapper personMapper;


    @BeforeEach
    void setup(){
        repo = mock(PersonRepository.class);
        personMapper = mock(PersonMapper.class);

        service = new PersonService(repo, personMapper);
    }

    @Test
    void testGetAllPersonsShouldReturnListOfPersons(){
        //arrange
        List<Person> mockList = List.of(new Person(1L,"Arne", "arne@mnail.com"), new Person(2L,"Bill", "bill@mail.com"));
        List<PersonResponseDto> mockDtoList = List.of(
                new PersonResponseDto(1L, "Arne", "bio" ),
                new PersonResponseDto(2L, "Bill", "bio 2" ));
        when(repo.findAll()).thenReturn(mockList);
        when(personMapper.toDtoList(mockList)).thenReturn(mockDtoList);


        //act
        List<PersonResponseDto> result = service.getAllPersons();


        //assert
        assertEquals(2, result.size());
        assertEquals("Arne", result.get(0).getName());

        verify(repo).findAll();
        verify(personMapper).toDtoList(mockList);


    }

    @Test
    public void addPerson_success(){

        //arrange
        Person p = new Person(1L, "Bill", "bill@mail.com");
        PersonRequestDto personDto = new PersonRequestDto("Bill", "bill@mail.com","bio", 44,44);

        when(repo.save(p)).thenReturn(p);

        //Act
        Map<String, String> result = service.addPerson(personDto);

        //Assert
        assertEquals("success", result.get("status"));


    }

    @Test
    public void addPerson_error(){

        //arrange
        Person p = new Person(5L, "Bill", "bill@mail.com");
        PersonRequestDto personDto = new PersonRequestDto("Bill", "bill@mail.com","bio", 44,44);

        when(repo.save(p)).thenThrow(new RuntimeException("DB failed to write"));
        when(personMapper.toEntity(personDto)).thenReturn(p);


        //Act
        Map<String, String> result = service.addPerson(personDto);

        //Assert
        assertEquals("error", result.get("status"));


    }








}