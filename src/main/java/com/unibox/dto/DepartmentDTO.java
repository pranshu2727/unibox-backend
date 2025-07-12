package com.unibox.dto;
public class DepartmentDTO {
    private int id;
    private String name;

    public DepartmentDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters & Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
}
