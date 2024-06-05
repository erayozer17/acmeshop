package com.erayoezer.acmeshop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty(message = "Name cannot be empty")
    @Size(max = 100, message = "Name should not be greater than 100 characters")
    private String name;

    @NotEmpty(message = "Description cannot be empty")
    @Size(max = 500, message = "Description should not be greater than 500 characters")
    private String description;

    @NotNull
    private String everydayAt;

    @NotNull
    private int everyNthDay;

    @NotNull
    private Boolean generated = false;

    @Version
    private Long version;

    @JsonManagedReference
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        Topic topic = (Topic) o;
        return Objects.equals(id, topic.id) && Objects.equals(name, topic.name) && Objects.equals(description, topic.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items.clear();
        if (items != null) {
            for (Item item : items) {
                item.setTopic(this);
                this.addItem(item);
            }
        }
    }

    public @NotNull Boolean getGenerated() {
        return generated;
    }

    public void setGenerated(@NotNull Boolean generated) {
        this.generated = generated;
    }

    public void addItem(Item item) {
        items.add(item);
        item.setTopic(this);
    }

    public void removeItem(Item item) {
        items.remove(item);
        item.setTopic(null);
    }

    public @NotNull String getEverydayAt() {
        return everydayAt;
    }

    public void setEverydayAt(@NotNull String everydayAt) {
        this.everydayAt = everydayAt;
    }

    @NotNull
    public int getEveryNthDay() {
        return everyNthDay;
    }

    public void setEveryNthDay(@NotNull int everyNthDay) {
        this.everyNthDay = everyNthDay;
    }
}
