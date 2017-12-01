package com.distributed.cyclomatic.web.rest; 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
//Git import
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.gmetrics.*;
import org.gmetrics.metric.cyclomatic.CyclomaticComplexityMetric;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller("DetailRestController")
public class DetailRestController {

     
 
    public void getRepository()
    {

        final String REMOTE_URL = "https://github.com/rubik/argon.git";

            try
            {

            	File Path = new File("argon_git");
            	
            	if(Path.exists()) 
                {
            		FileUtils.deleteDirectory(Path);
                }
            	Path.mkdir();
                
                
                // then clone
                System.out.println("Cloning from " + REMOTE_URL + " to " + Path);
                try (Git result = Git.cloneRepository()
                        .setURI(REMOTE_URL)
                        .setDirectory(Path)
                        .call()) 
                {
        	        System.out.println("Having repository: " + result.getRepository().getDirectory());
                }
                catch(GitAPIException ge)
                {
                	ge.printStackTrace();
                }
 
                
            }
            catch (Exception e) 
            {
            	e.printStackTrace();
            }
                     
     
    }
    
    
    public <T> List<T> getCommits() 
    {
    	List commitList = new ArrayList();
    	try
    	{
        Repository repo = new FileRepository("argon_git/.git");
        Git git = new Git(repo);
        RevWalk walk = new RevWalk(repo);
        
        try
        {
        List<Ref> branches = git.branchList().call();

        for (Ref branch : branches) {
            String branchName = branch.getName();

            Iterable<RevCommit> commits = git.log().all().call();

            for (RevCommit commit : commits) {
                boolean foundInThisBranch = false;

                RevCommit targetCommit = walk.parseCommit(repo.resolve(
                        commit.getName()));
                for (Map.Entry<String, Ref> e : repo.getAllRefs().entrySet()) {
                    if (e.getKey().startsWith(Constants.R_HEADS)) {
                        if (walk.isMergedInto(targetCommit, walk.parseCommit(
                                e.getValue().getObjectId()))) {
                            String foundInBranch = e.getValue().getName();
                            if (branchName.equals(foundInBranch)) {
                                foundInThisBranch = true;
                                break;
                            }
                        }
                    }
                }

                if (foundInThisBranch) {
                	commitList.add(commit.getName());
                }
            }
         }
        
        }
          catch(GitAPIException ge)
          {
        	ge.printStackTrace();
          }
      }
        catch(IOException ie)
        {
    	ie.printStackTrace();
        }
        return commitList;
    }
    
    
    
    // send file to node
    public void sendfile()
    {
    	MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
    	parameters.add("file", new FileSystemResource("index.js"));

    	HttpHeaders headers = new HttpHeaders();
    	headers.set("Content-Type", "multipart/form-data");
    	headers.set("Accept", "text/plain");
        
    	RestTemplate restTemplate = new RestTemplate();
    	String result = restTemplate.postForObject(
    	    "http://localhost:8090/GetCylomatic", 
    	    new HttpEntity<MultiValueMap<String, Object>>(parameters, headers), 
    	    String.class);
    }

    
    
    //Master worker algorithm
    public void masterWorker()
    {
    	
    	getRepository();
    	List<String> commitList = getCommits();
    	System.out.println("Extracted commits");
    	for (String commit : commitList) 
    	{
    	    System.out.println(commit);    	
    	}
    	
    
    }
    
    
    
    @RequestMapping(value = "/Start", method = RequestMethod.GET)
    @ResponseBody
    public void StartProcess()  
    {
    	
    	System.out.println("Connected to Master");
    	System.out.println("Process started");
    	
    	//Get Git repository
    	masterWorker();
    
    	
    	//Call Master-worker method
    	//masterWorker();
    	
        
    }
    

    

}