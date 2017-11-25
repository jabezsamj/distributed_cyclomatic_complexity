package com.distributed.cyclomatic.service;
import com.distributed.cyclomatic.domain.Detail;
import java.util.List;

public interface DetailService {
    public Detail findById(Integer id);
    public void saveDetail(Detail author_1);
    public List<Detail> findAll();
    
}
