package com.distributed.cyclomatic.service.impl;
import com.distributed.cyclomatic.persistence.DetailRepository;
import com.distributed.cyclomatic.domain.Detail;
import com.distributed.cyclomatic.service.DetailService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("DetailService")
@Transactional
public class DetailServiceImpl implements DetailService {

    @Autowired
    private DetailRepository authorRepository;
    public DetailServiceImpl() {
    }

    @Transactional
    public Detail findById(Integer id) {
        return authorRepository.findById(id);
    }

    @Transactional
    public List<Detail> findAll() {
        return authorRepository.findAll();
    }
     
    @Transactional
    public void saveDetail(Detail author) {
        Detail existingDetail = authorRepository.findById(author.getId());
        if (existingDetail != null) {
        if (existingDetail != author) {      
        existingDetail.setId(author.getId());
                existingDetail.setDetail(author.getDetail());
        }
        author = authorRepository.save(existingDetail);
    }else{
        author = authorRepository.save(author);
        }
        authorRepository.flush();
    }

    

}