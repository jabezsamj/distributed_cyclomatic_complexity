package com.distributed.cyclomatic.service.impl;
import com.distributed.cyclomatic.persistence.AuthorRepository;
import com.distributed.cyclomatic.domain.Author;
import com.distributed.cyclomatic.service.AuthorService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("AuthorService")
@Transactional
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private AuthorRepository authorRepository;
    public AuthorServiceImpl() {
    }

    @Transactional
    public Author findById(Integer id) {
        return authorRepository.findById(id);
    }

    @Transactional
    public List<Author> findAll() {
        return authorRepository.findAll();
    }
     
    @Transactional
    public void saveAuthor(Author author) {
        Author existingAuthor = authorRepository.findById(author.getId());
        if (existingAuthor != null) {
        if (existingAuthor != author) {      
        existingAuthor.setId(author.getId());
                existingAuthor.setAuthor(author.getAuthor());
        }
        author = authorRepository.save(existingAuthor);
    }else{
        author = authorRepository.save(author);
        }
        authorRepository.flush();
    }

    

}