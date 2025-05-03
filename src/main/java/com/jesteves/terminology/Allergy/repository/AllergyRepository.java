package com.jesteves.terminology.Allergy.repository;

import com.jesteves.terminology.Allergy.entity.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, Long> {


    /**
     * Search the term (case-insensitive) in any of the fields : preferredTermEn,
     * allergenFsn, preferredTermPt, synonym1, synonym2, ou allergyCategoryFsn.
     *
     * @param searchTerm Term to search.
     * @return list of  Allergies that correspond to the term.
     */
    @Query("SELECT a FROM Allergy a WHERE " +
            "LOWER(a.preferredTermEn) LIKE LOWER(concat('%', :term, '%')) OR " +
            "LOWER(a.allergenFsn) LIKE LOWER(concat('%', :term, '%')) OR " +
            "LOWER(a.preferredTermPt) LIKE LOWER(concat('%', :term, '%')) OR " +
            "LOWER(a.synonym1) LIKE LOWER(concat('%', :term, '%')) OR " +
            "LOWER(a.synonym2) LIKE LOWER(concat('%', :term, '%')) OR " +
            "LOWER(a.allergyCategoryFsn) LIKE LOWER(concat('%', :term, '%'))")
    List<Allergy> searchByTerm(@Param("term") String searchTerm);

}
