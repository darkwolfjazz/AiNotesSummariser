package com.NotesSummary.repository;

import com.NotesSummary.entity.Notes;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends MongoRepository<Notes,Long> {

}
