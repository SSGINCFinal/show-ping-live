package com.ssginc.showpinglive.repository;

import com.ssginc.showpinglive.dto.object.ForbiddenWord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForbiddenWordRepository extends MongoRepository<ForbiddenWord, String> {
    // 금칙어 검색을 위한 메서드 예시
    boolean existsBySlang(String slang);
}