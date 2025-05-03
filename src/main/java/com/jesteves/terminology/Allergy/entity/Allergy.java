package com.jesteves.terminology.Allergy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "catalogo_alergias")
@Getter
@Setter
public class Allergy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "allergen_code")
    private Long allergen_code;

    @Column(name = "allergy_category_code")
    private Long allergyCategoryCode;

    @Column(name = "allergy_category_fsn", columnDefinition = "TEXT")
    private String allergyCategoryFsn;

    @Column(name = "allergen_fsn", columnDefinition = "TEXT")
    private String allergenFsn;

    @Column(name = "preferred_term_en", length = 255)
    private String preferredTermEn;

    @Column(name = "preferred_term_pt", length = 255)
    private String preferredTermPt;

    @Column(name = "synonym_1", length = 255)
    private String synonym1;

    @Column(name = "synonym_2", length = 255)
    private String synonym2;

    public Allergy() {
    }

}
