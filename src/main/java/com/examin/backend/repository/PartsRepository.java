package com.examin.backend.repository;

import com.examin.backend.model.PartsDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartsRepository extends MongoRepository<PartsDTO, String> {

    @Query("{'Availability': {$regex: ?0, $options: 'i'}}")
    List<PartsDTO> findPartsByAvailabilityRegex(String regex);
}