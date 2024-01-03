package com.examin.backend.controller;
import com.examin.backend.model.PartsDTO;
import com.examin.backend.repository.PartsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173/") // Replace with the actual URL of your React app
public class PartsController {

    @Autowired
    private PartsRepository partsRepo;
    @Autowired
    private MongoTemplate mongoTemplate;
    @GetMapping("/parts")
    public ResponseEntity<?> getAllParts() {
        List<PartsDTO> parts = partsRepo.findAll();
        if (parts.size() > 0) {
            return new ResponseEntity<List<PartsDTO>>(parts, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No parts!", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/statistics/model-count")
    public ResponseEntity<?> getModelCount() {
        try {
            Map<String, Long> modelCount = partsRepo.findAll().stream()
                    .collect(Collectors.groupingBy(PartsDTO::getModel, Collectors.counting()));

            return new ResponseEntity<>(modelCount, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/statistics/manufacturer-count")
    public ResponseEntity<?> getManufacturerCount() {
        try {
            Map<String, Long> manufacturerCount = partsRepo.findAll().stream()
                    .collect(Collectors.groupingBy(PartsDTO::getFinishAndProducer, Collectors.counting()));

            return new ResponseEntity<>(manufacturerCount, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/statistics/price-ranges")
    public ResponseEntity<?> getPriceRanges() {
        try {
            List<PartsDTO> allParts = partsRepo.findAll();

            // Define price ranges
            int rangeSize = 100;
            int maxPrice = (int) allParts.stream().mapToDouble(part -> part.getPrice() != null ? part.getPrice() : 0).max().orElse(0);

            // Initialize the map with zero counts for each range
            Map<String, Long> priceRanges = new HashMap<>();
            for (int i = 0; i <= maxPrice; i += rangeSize) {
                int startRange = i;
                int endRange = i + rangeSize - 1;
                String rangeLabel = getRangeLabel(startRange);
                priceRanges.put(rangeLabel, 0L);
            }

            // Count parts in each price range
            for (PartsDTO part : allParts) {
                int partPrice = (int) (part.getPrice() != null ? part.getPrice() : 0);
                String rangeLabel = getRangeLabel(partPrice);
                priceRanges.put(rangeLabel, priceRanges.get(rangeLabel) + 1);
            }

            // Sort the map by count in descending order
            priceRanges = priceRanges.entrySet().stream()
                    .sorted((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

            return new ResponseEntity<>(priceRanges, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper method to get the range label for a given price
    private String getRangeLabel(int partPrice) {
        int startRange = (partPrice / 100) * 100;
        int endRange = startRange + 99;
        return startRange + "..." + endRange;
    }
    @GetMapping("/statistics/manufacturer-average-price")
    public ResponseEntity<?> getManufacturerAveragePrice() {
        try {
            Map<String, Double> manufacturerAveragePrice = partsRepo.findAll().stream()
                    .collect(Collectors.groupingBy(PartsDTO::getFinishAndProducer,
                            Collectors.averagingDouble(part -> part.getPrice() != null ? part.getPrice() : 0)));

            return new ResponseEntity<>(manufacturerAveragePrice, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/statistics/model-average-price")
    public ResponseEntity<?> getModelAveragePrice() {
        try {
            Map<String, Double> modelAveragePrice = partsRepo.findAll().stream()
                    .collect(Collectors.groupingBy(PartsDTO::getModel,
                            Collectors.averagingDouble(part -> part.getPrice() != null ? part.getPrice() : 0)));

            return new ResponseEntity<>(modelAveragePrice, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/statistics/total-count")
    public ResponseEntity<?> getTotalCount() {
        try {
            long totalCount = partsRepo.count();
            return new ResponseEntity<>(totalCount, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/statistics/average-price")
    public ResponseEntity<?> getAveragePrice() {
        try {
            List<PartsDTO> allParts = partsRepo.findAll();
            double averagePrice = allParts.stream()
                    .mapToDouble(part -> part.getPrice() != null ? part.getPrice() : 0)
                    .average()
                    .orElse(0);
            return new ResponseEntity<>(averagePrice, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/statistics/total-sum")
    public ResponseEntity<?> getTotalSum() {
        try {
            List<PartsDTO> allParts = partsRepo.findAll();
            double totalSum = allParts.stream()
                    .mapToDouble(part -> part.getPrice() != null ? part.getPrice() : 0)
                    .sum();
            return new ResponseEntity<>(totalSum, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/parts/{id}")
    public ResponseEntity<?> getPartById(@PathVariable("id") String id) {
        Optional<PartsDTO> partsOptional = partsRepo.findById(id);
        if (partsOptional.isPresent()) {
            return new ResponseEntity<>(partsOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Part not found by id!" + id, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/parts/{id}")
    public ResponseEntity<?> updatePartById(@PathVariable("id") String id, @RequestBody PartsDTO parts) {
        Optional<PartsDTO> partsOptional = partsRepo.findById(id);
        if (partsOptional.isPresent()) {
            PartsDTO partsToSave = partsOptional.get();
            partsToSave.setOldCode(parts.getOldCode() != null ? parts.getOldCode() : partsToSave.getOldCode());
            partsToSave.setP1P2P3(parts.getP1P2P3() != null ? parts.getP1P2P3() : partsToSave.getP1P2P3());
            partsToSave.setFinishAndProducer(parts.getFinishAndProducer() != null ? parts.getFinishAndProducer() : partsToSave.getFinishAndProducer());
            partsToSave.setModel(parts.getModel() != null ? parts.getModel() : partsToSave.getModel());
            partsToSave.setName(parts.getName() != null ? parts.getName() : partsToSave.getName());
            partsToSave.setPrice(parts.getPrice() != null ? parts.getPrice() : partsToSave.getPrice());
            partsToSave.setAvailability(parts.getAvailability() != null ? parts.getAvailability() : partsToSave.getAvailability());
            partsRepo.save(partsToSave);
            return new ResponseEntity<>(partsToSave, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Part not found by id!" + id, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/parts/{id}")
    public ResponseEntity<?> deletePartById(@PathVariable("id") String id) {
        try {
            partsRepo.deleteById(id);
            return new ResponseEntity<>("Successfully deleted" + id, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/parts")
    public ResponseEntity<?> createPart(@RequestBody PartsDTO newPart) {
        try {
            PartsDTO savedPart = partsRepo.save(newPart);
            return new ResponseEntity<>(savedPart, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/parts/availability/greater-than/{count}")
    public ResponseEntity<?> getPartsByAvailabilityGreaterThan(@PathVariable("count") String count) {
        return getPartsByAvailabilityComparison(count, "greater-than");
    }

    @GetMapping("/parts/availability/less-than/{count}")
    public ResponseEntity<?> getPartsByAvailabilityLessThan(@PathVariable("count") String count) {
        return getPartsByAvailabilityComparison(count, "less-than");
    }

    private ResponseEntity<?> getPartsByAvailabilityComparison(String count, String comparisonType) {
        try {
            int numericCount = extractNumericValue(count);

            // Fetch all documents
            List<PartsDTO> allParts = mongoTemplate.findAll(PartsDTO.class);

            // Filter documents based on the numeric comparison
            List<PartsDTO> filteredParts = allParts.stream()
                    .filter(part -> {
                        String availability = part.getAvailability();
                        if (availability != null && availability.matches("^<\\d+$")) {
                            int partNumericValue = extractNumericValue(availability);
                            return (comparisonType.equals("greater-than") && partNumericValue > numericCount) ||
                                    (comparisonType.equals("less-than") && partNumericValue < numericCount);
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

            return new ResponseEntity<>(filteredParts, HttpStatus.OK);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("Invalid numeric input", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper method to extract numeric value from a string
    // Helper method to extract numeric value from a string
    @GetMapping("/parts/availability/is-null-or-empty")
    public ResponseEntity<?> getPartsByAvailabilityNullOrEmpty() {
        try {
            // Fetch all documents
            List<PartsDTO> allParts = mongoTemplate.findAll(PartsDTO.class);

            // Filter documents where availability is null or an empty string
            List<PartsDTO> filteredParts = allParts.stream()
                    .filter(part -> {
                        String availability = part.getAvailability();
                        return availability == null || availability.isEmpty();
                    })
                    .collect(Collectors.toList());

            return new ResponseEntity<>(filteredParts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // Helper method to extract numeric value from a string
    private int extractNumericValue(String input) {
        String numericPart = input.replaceAll("\\D", "");
        return Integer.parseInt(numericPart);
    }}