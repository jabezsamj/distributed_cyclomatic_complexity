package com.distributed.cyclomatic.service;
import com.distributed.cyclomatic.domain.Author;
import java.util.List;

public interface AuthorService {
    public Author findById(Integer id);
    public void saveAuthor(Author author_1);
    public List<Author> findAll();
    
}
