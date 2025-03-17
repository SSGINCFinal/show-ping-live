package com.ssginc.showpinglive.repository;

import com.ssginc.showpinglive.dto.object.ForbiddenWord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForbiddenWordRepository extends MongoRepository<ForbiddenWord, String> {
}