package com.distributed.cyclomatic.persistence;  
import com.distributed.cyclomatic.domain.Detail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailRepository extends JpaRepository<Detail, Integer> {	 
    Detail findById(Integer id);
    List<Detail> findAll(); 
}