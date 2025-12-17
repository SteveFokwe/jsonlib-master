package com.company.test;

import com.company.jsonlib.annotations.FieldName;

// DTO représentant un cours
public class CoursDTO {
    private String code;

    @FieldName(override = "titre")
    private String name;

    private String description;
    private int maxStudents;

    public CoursDTO() {
    }

    public CoursDTO(String code, String name, String description, int maxStudents) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.maxStudents = maxStudents;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(int maxStudents) {
        this.maxStudents = maxStudents;
    }

    @Override
    public String toString() {
        return "CoursDTO{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", maxStudents=" + maxStudents +
                '}';
    }
}