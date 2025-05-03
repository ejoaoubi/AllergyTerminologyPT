package com.jesteves.terminology.Allergy.controller;

import com.jesteves.terminology.Allergy.entity.Allergy;
import com.jesteves.terminology.Allergy.service.CsvDataLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/allergies/")
public class AllergyController {

    private final CsvDataLoaderService allergyService;

    @Autowired
    public AllergyController(CsvDataLoaderService allergyService) {
        this.allergyService = allergyService;
    }

    @GetMapping("/all/")
    public ResponseEntity<List<Allergy>> getAllergies(){
        List<Allergy> allergies = allergyService.listAll();

        if (allergies.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(allergies);
    }

    @GetMapping("/search/")
    public ResponseEntity<List<Allergy>> searchAllergiesByTerm(@RequestParam(name = "term") String searchTerm) {
        List<Allergy> allergies = allergyService.searchAllergiesByTerm(searchTerm);
        if (allergies.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(allergies);
    }

}
