package com.erayoezer.acmeshop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@Access(AccessType.FIELD)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String text;

    @Column(name = "item_order")
    private Integer itemOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Topic topic;

    @NotNull
    private Boolean sent = false;

    @Column(name = "next_at")
    private Date nextAt;

    @Lob // This annotation indicates that the content should be stored as a large object
    @Column(columnDefinition = "TEXT")
    private String content;

    @Version
    private Long version;

    @Transient
    private String dateRepresentation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public @NotNull Boolean getSent() {
        return sent;
    }

    public void setSent(@NotNull Boolean sent) {
        this.sent = sent;
    }

    public Date getNextAt() {
        return nextAt;
    }

    public void setDateRepresentation(String date) {
        this.dateRepresentation = date;
    }

    public String getDateRepresentation() {
        return dateRepresentation;
    }

    public void setNextAt(Date nextAt) {
        this.nextAt = nextAt;
    }

    public Integer getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(Integer itemOrder) {
        this.itemOrder = itemOrder;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id) && Objects.equals(text, item.text) && Objects.equals(topic, item.topic) && Objects.equals(version, item.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, topic, version);
    }
}
