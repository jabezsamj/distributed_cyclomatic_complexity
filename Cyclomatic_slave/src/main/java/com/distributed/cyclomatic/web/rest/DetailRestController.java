package com.distributed.cyclomatic.web.rest; 

import java.util.List;
import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


@Controller("DetailRestController")
public class DetailRestController {


    @RequestMapping(value="/GetCylomatic", method=RequestMethod.POST,headers = "content-type=multipart/*" )
    @ResponseBody
    public void  getCyclomatic( @RequestParam("file") MultipartFile file){
    	
    	try {
			String content = new String(file.getBytes());
			System.out.println(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }



    
    @RequestMapping(value = "/Start", method = RequestMethod.GET)
    @ResponseBody
    public File startCalculation() {
    	

    	File file = new File("index.js");
    	File fileFetched = file.getParentFile();
    	

     	final String uri = "http://localhost:8080/Detail";  
    	RestTemplate restTemplate = new RestTemplate();                                  
    	String result = restTemplate.getForObject(uri, String.class);                    
                                                                                       
    	System.out.println(result); 
    	return fileFetched;

    }



    

}