package com.example.spring_rest_intro;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class PersonIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository repo;

    @BeforeEach
    public void setup(){
        repo.deleteAll();
    }


    @Test
    public void createPerson_success() throws Exception{
        //arrange
        String json = """
                {
                    "name": "a guy",
                    "email": "min@mail.com",
                    "bio": "en beskrivning",
                    "age": 23,
                    "shoeSize": 40
                }
                """;

        //act
        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
         //assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value(containsString("a guy")));



    }


    @Test
    public void findPersonByID_success() throws Exception{
        //arrange
        Person p = new Person("Arne", "Arne@mil.com", new Profil() );
        repo.save(p);

        Long id = p.getId();

        //act
        mockMvc.perform(get("/person?id=" + id))
                .andExpect(status().isOk());


    }

}
