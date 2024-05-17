package com.erayoezer.acmeshop.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class List {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    @Version
    private Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        List list = (List) o;
        return Objects.equals(id, list.id) && Objects.equals(name, list.name) && Objects.equals(description, list.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }
}
