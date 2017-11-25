package com.distributed.cyclomatic.web.rest; 
import com.distributed.cyclomatic.domain.Author;
import com.distributed.cyclomatic.persistence.AuthorRepository;
import com.distributed.cyclomatic.service.AuthorService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("AuthorRestController")
public class AuthorRestController {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorService authorService;

    @RequestMapping(value = "/Author", method = RequestMethod.PUT)
    @ResponseBody
    public Author saveAuthor(@RequestBody Author author) {
    authorService.saveAuthor(author);
        return authorRepository.findById(author.getId());
    }

    @RequestMapping(value = "/Author", method = RequestMethod.POST)
    @ResponseBody
    public Author newAuthor(@RequestBody Author author) {
    authorService.saveAuthor(author);
        return authorRepository.findById(author.getId());
    }

    @RequestMapping(value = "/Author", method = RequestMethod.GET)
    @ResponseBody
    public List<Author> listAuthors() {
        return new java.util.ArrayList<Author>(authorService.findAll());
    }

    @RequestMapping(value = "/Author/{author_id}", method = RequestMethod.GET)
    @ResponseBody
    public Author loadAuthor(@PathVariable Integer author_id) {
        return authorService.findById(author_id);
    }

    

}