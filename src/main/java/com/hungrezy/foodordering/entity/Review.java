package com.hungrezy.foodordering.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "reviews")
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant;

    @Column(nullable = false)
    private Integer rating;

    private String comment;

    @JsonProperty("_id")
    public UUID idAlias() {
        return id;
    }

    @JsonProperty("userId")
    public User userIdAlias() {
        return user;
    }

    @JsonProperty("review")
    public String reviewAlias() {
        return comment;
    }

    @JsonProperty("helpfulVotes")
    public int helpfulVotesAlias() {
        return 0;
    }
}
