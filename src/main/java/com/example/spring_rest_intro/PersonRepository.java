package com.example.spring_rest_intro;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PersonRepository {

    private List<Person> persoList = new ArrayList<>();

    private final JdbcTemplate jdbcTemplate;

    public PersonRepository(List<Person> persoList, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        //initList();
        setupTables();
    }

    private void setupTables(){

        try {
            String createTableSQL  = "CREATE TABLE IF NOT EXISTS persons (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT)";

            jdbcTemplate.execute(createTableSQL);
            System.out.println("table is created");
        } catch (Exception e) {
            System.out.println("failed to create table: " + e.getMessage());
        }


    }

    private void initList(){
        persoList.add(new Person("Arne", "arnes@mail.com"));
        persoList.add(new Person("Bill", "bills@mail.com"));
        persoList.add(new Person("Cesar", "cesar@mail.com"));
        persoList.add(new Person("Didrik", "didrik@mail.com"));
        persoList.add(new Person("Eskil", "eskil@mail.com"));


    }

    public Map<String,String> getStatus(){
        Map<String,String> response = new HashMap<>();

        try {
            String result = jdbcTemplate
                    .queryForObject("SELECT 'Connection is successful!' AS message",
                            (rs, rowNo) -> rs.getString("message"));

            response.put("status", "success");
            response.put("message", result);
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Database error: " + e.getMessage());
            return response;
        }

    }

    public List<Person> getAllPersons(){

        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM persons");

            persoList.clear();

            for(Map<String, Object> row: rows){
                int id = (int) row.get("id");
                String name = (String) row.get("name");
                String email = (String) row.get("email");

                persoList.add(new Person(id,name,email));
            }

            return persoList;
        } catch (Exception e) {
            System.out.println("error fetching persons: " + e.getMessage());
            return persoList;
        }
    }

    public Map<String, String> addPerson(Person person){

        Map<String, String> response = new HashMap<>();


        // sql för update = "UPDATE persons SET name = ?, email = ? WHERE id = ?"
        try {
            jdbcTemplate.update("INSERT INTO persons (name, email) VALUES (?,?)",
                    person.getName(),
                    person.getEmail());

            response.put("status", "success");
            response.put("message", "added new person");
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "failed to add person: " + e.getMessage());
            return response;
        }

        //persoList.add(person);
    }

    public boolean deleteById(int id){

        try {
            int rowsAffected = jdbcTemplate.update("DELETE FROM persons WHERE id = ?", id);

            if(rowsAffected > 0){
                return true;
            } else {
                System.out.println("person not found");
                return false;
            }

        } catch (Exception e) {
            System.out.println("error while deleting: " + e.getMessage());
            return false;
        }
        //return persoList.removeIf(p -> p.getId() == id);
    }
}
