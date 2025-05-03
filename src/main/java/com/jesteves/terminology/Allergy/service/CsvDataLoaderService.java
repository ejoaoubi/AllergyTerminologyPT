package com.jesteves.terminology.Allergy.service;

import com.jesteves.terminology.Allergy.entity.Allergy;
import com.jesteves.terminology.Allergy.repository.AllergyRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvDataLoaderService {

    private static final Logger log = LoggerFactory.getLogger(CsvDataLoaderService.class);

    private final AllergyRepository repository;
    private final ResourceLoader resourceLoader;


    @Value("${app.csv.file.path:classpath:db/catalogo_alergias.csv}")
    private String csvFilePath;

    @Autowired
    public CsvDataLoaderService(AllergyRepository repository, ResourceLoader resourceLoader) {
        this.repository = repository;
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    @Transactional
    public void loadCsvDataOnStartup() {

        if (repository.count() > 0) {
            log.info("Data CSV already loaded. Skipping.");
            return;
        }

        log.info("Loading CSV: {}", csvFilePath);
        Resource resource = resourceLoader.getResource(csvFilePath);
        List<Allergy> dataToSave = new ArrayList<>();

        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream());
             CSVReader csvReader = new CSVReader(reader)) {

            String[] headers = csvReader.readNext();
            if (headers == null) {
                log.warn("CSV is empty.");
                return;
            }
            log.info("Header CSV: {}", (Object) headers);

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                try {

                    Allergy csvData = new Allergy();
                    csvData.setAllergen_code(Long.parseLong(line[0].trim()));
                    csvData.setAllergyCategoryCode(Long.parseLong(line[1].trim()));
                    csvData.setAllergenFsn(line[3].trim());
                    csvData.setAllergyCategoryFsn(line[4].trim());
                    csvData.setPreferredTermEn(line[5].trim());
                    csvData.setPreferredTermPt(line[6].trim());
                    csvData.setSynonym1(line[7].trim());
                    csvData.setSynonym2(line[8].trim());

                    dataToSave.add(csvData);

                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    log.error("Fail in CSV Line: {} - {}", line, e.getMessage());
                }
            }

            if (!dataToSave.isEmpty()) {
                log.info("Saving {} lines in db", dataToSave.size());
                repository.saveAll(dataToSave);
                log.info("CSV Data loaded with success!");
            } else {
                log.info("No valid data found in CSV.");
            }


        } catch (IOException | CsvValidationException e) {
            log.error("Fail loading CSV data: {}", e.getMessage(), e);

        }
    }

    public List<Allergy> listAll(){
        return repository.findAll();
    }

    public List<Allergy> searchAllergiesByTerm(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return repository.searchByTerm(searchTerm.trim());
    }
}