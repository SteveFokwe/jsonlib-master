package com.company.test;

import com.company.jsonlib.annotations.FieldName;
import com.company.jsonlib.annotations.Ignore;

import java.util.List;

public class StudentDTO {
    private int id;

    @FieldName(override = "prenom")
    private String firstName;

    @FieldName(override = "nom")
    private String lastName;

    private int age;
    private String gender;

    @Ignore
    private String internalNote;

    private List<CoursDTO> inscriptions;

    public StudentDTO() {
    }

    public StudentDTO(int id, String firstName, String lastName, int age, String gender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getInternalNote() {
        return internalNote;
    }

    public void setInternalNote(String internalNote) {
        this.internalNote = internalNote;
    }

    public List<CoursDTO> getInscriptions() {
        return inscriptions;
    }

    public void setInscriptions(List<CoursDTO> inscriptions) {
        this.inscriptions = inscriptions;
    }

    @Override
    public String toString() {
        return "StudentDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", inscriptions=" + inscriptions +
                '}';
    }
}