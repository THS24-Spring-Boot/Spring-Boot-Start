package com.example.spring_rest_intro;

public class Person {

    private long id;

    private String name;

    private String email;

    private static long maxId = 0;

    public Person( String name, String email) {
        this.id = maxId +1;
        this.name = name;
        this.email = email;
        maxId ++;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
