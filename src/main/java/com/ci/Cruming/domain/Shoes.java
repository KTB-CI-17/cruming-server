package com.ci.Cruming.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shoes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shoes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String photo;

    @Column(nullable = false, length = 300)
    private String korean;

    @Column(nullable = false, length = 100)
    private String english;

    @Column(nullable = false, length = 500)
    private String link;

    @Column(length = 600)
    private String tip;
}
