package com.example.spring_rest_intro;

public class PersonRequestDto {

    private String name;
    private String email;
    private String bio;
    private int shoeSize;
    private int age;

    public PersonRequestDto() {
    }

    public PersonRequestDto(String name, String email, String bio, int shoeSize, int age) {
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.shoeSize = shoeSize;
        this.age = age;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getShoeSize() {
        return shoeSize;
    }

    public void setShoeSize(int shoeSize) {
        this.shoeSize = shoeSize;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
