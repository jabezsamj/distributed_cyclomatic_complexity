package com.distributed.cyclomatic.persistence;  
import com.distributed.cyclomatic.domain.Author;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {	 
    Author findById(Integer id);
    List<Author> findAll(); 
}