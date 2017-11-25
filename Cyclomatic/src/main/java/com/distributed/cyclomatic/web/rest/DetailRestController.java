package com.distributed.cyclomatic.web.rest; 
import com.distributed.cyclomatic.domain.Detail;
import com.distributed.cyclomatic.persistence.DetailRepository;
import com.distributed.cyclomatic.service.DetailService;
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

@Controller("DetailRestController")
public class DetailRestController {

    @Autowired
    private DetailRepository detailRepository;

    @Autowired
    private DetailService detailService;

    @RequestMapping(value = "/Detail", method = RequestMethod.PUT)
    @ResponseBody
    public Detail saveDetail(@RequestBody Detail detail) {
    detailService.saveDetail(detail);
        return detailRepository.findById(detail.getId());
    }
     
    
    /*
    @RequestMapping(value = "/Detail", method = RequestMethod.POST)
    @ResponseBody
    public Detail newDetail(@RequestBody Detail detail) {
    detailService.saveDetail(detail);
        return detailRepository.findById(detail.getId());
    }*/
    
    @RequestMapping(value = "/Detail", method = RequestMethod.POST)
    @ResponseBody
    public void listDetails() {
        //return new java.util.ArrayList<Detail>(detailService.findAll());
    	System.out.println("I am able to connect");
    }
    
    /*
    @RequestMapping(value = "/Detail", method = RequestMethod.GET)
    @ResponseBody
    public void listDetails() {
        //return new java.util.ArrayList<Detail>(detailService.findAll());
    	System.out.println("I am able to connect");
    }*/

    @RequestMapping(value = "/Detail/{detail_id}", method = RequestMethod.GET)
    @ResponseBody
    public Detail loadDetail(@PathVariable Integer detail_id) {
        return detailService.findById(detail_id);
    }

    

}