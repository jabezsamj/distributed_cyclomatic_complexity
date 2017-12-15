package com.distributed.cyclomatic.web.rest; 

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
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
	
	int REQUEST_COUNT = 0;
	int SUBMISSION_COUNT = 0;
	int sencommitCall =0;
	int calulateAvgCyclomaticCount =0;
	
	Queue<String> workQueue = new LinkedList<String>();
	String masterURL = "http://54.72.77.242:8080/";
	
	public int checkComplexity(File forFile) 
    {
        int complexity = 1;
        String fileName;
        String[] keywords = {"if", "else", "while", "case", "for", "switch", "do", "continue", "break", "&&",
            "||", "?", ":", "catch", "finally", "throw", "throws", "default", "return", "foreach", "elseif", "or", "and", "xor"};
        String words = "";
        String line = null;
       
        try {
            FileReader fr = new FileReader(forFile);
            BufferedReader br = new BufferedReader(fr);
            line = br.readLine();
            while (line != null) {
                StringTokenizer stTokenizer = new StringTokenizer(line);
                while (stTokenizer.hasMoreTokens()) {
                    words = stTokenizer.nextToken();
                    for (int i = 0; i < keywords.length; i++) {
                        if (keywords[i].equals("\\")) {
                            break;
                        } else {
                            if (keywords[i].equals(words)) {
                                complexity++;
                            }
                        }
                    }
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return (complexity);
    }
	

	public File getCommit(String commitId)
    {
		final String REMOTE_URL = "https://github.com/airbnb/javascript.git";
		File gitDir = new File("airbnb_git"+commitId);
        try
        {
        	
        	if(gitDir.exists()) 
            {
        		FileUtils.deleteDirectory(gitDir);
            }
        	gitDir.mkdir();
            
            try (Git cloneRepo = Git.cloneRepository()
                    .setURI(REMOTE_URL)
                    .setDirectory(gitDir)
                    .call()) 
            {}
            catch(GitAPIException ge)
            {
            	ge.printStackTrace();
            }
            
            
            
            Git gitByCommit = Git.open(gitDir);
            try {
            	gitByCommit.checkout().setName(commitId).call();
			} catch (RefAlreadyExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RefNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidRefNameException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CheckoutConflictException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            
        }
        catch (Exception e) 
        {
        	e.printStackTrace();
        }
    
        return(gitDir);
    	
    }
	
	
	public int getCycloAvgOfEachFile(File commitDir)
	{
		List<Integer> cycloVals = new ArrayList<Integer>();
		String[] extensions = {"js"};
    	Collection<File> matchingFiles = FileUtils.listFiles(commitDir, extensions, true);
    	int i = 0;
    	int cumulative_cyclo_val = 0;
    	for(File matchFile:matchingFiles)
    	{
    		i++;
    		cumulative_cyclo_val += checkComplexity(matchFile);
    	}
    	
    	int avg_Cyclo_val = cumulative_cyclo_val/i;
    	return avg_Cyclo_val;
	}
	
	
	public int calulateAvgCyclomatic(String commitId)
	{
		//System.out.println("Calculate Avg Cyclomatic count: "+ ++calulateAvgCyclomaticCount);
		File commitDir = getCommit(commitId);
		return  getCycloAvgOfEachFile(commitDir);
	}
	
	
    public void sendCommit(String value)
    {
    	//System.out.println("Commit Call: "+ ++sencommitCall);
    	try {
			URI master_url = new URI (masterURL + "RecieveCyclomatic/" + value);
	    	RestTemplate restTemplate = new RestTemplate();
	    	restTemplate.postForLocation(master_url, String.class);
	        System.out.println("SUBMISSION_COUNT: " + ++SUBMISSION_COUNT);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
   	
    }
    
    
    
    public void work(String commitId)
    {
    	String cyclomatic = String.valueOf(calulateAvgCyclomatic(commitId)); // Get the cyclomatic value and convert it into string
		String returnVal = commitId + "CYCLOMATIC_VALUE_" + cyclomatic;
		sendCommit(returnVal);
		requestWork();
    }
    
    
    public void requestWork()
    {
    	System.out.println("REQUEST_WORK_COUNT : "+ ++REQUEST_COUNT);
    	try {
			URI master_url = new URI (masterURL + "RequestWork");
	    	RestTemplate restTemplate = new RestTemplate();
	    	ResponseEntity<String> Response = restTemplate.getForEntity(master_url, String.class);
	    	if(Response.getBody().equals("WORK_COMPLETE"))
	    	{
	    		System.out.println("WORK_COMPLETE");
	    	}
	    	else
	    	{
	    		work(Response.getBody());
	    		//System.out.println(Response.getBody());
	    	}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
    }
	

    /*public class GetCyclomaticRunnable implements Runnable {
        
    	String commitId;
	    public GetCyclomaticRunnable(String commitId) {
			 this.commitId = commitId; 
		}

		public void run(){
			System.out.println(workQueue.size());
			startWork();
		}
	}

	@RequestMapping(value = "/GetCylomatic/{commitId}", method = RequestMethod.POST)
    @ResponseBody
    public void getCyclomatic (@PathVariable("commitId") String commitId) 
	{
		 System.out.println("REQUEST_COUNT: " + REQUEST_COUNT++ );
		 GetCyclomaticRunnable runnable = new GetCyclomaticRunnable(commitId);
		 CompletableFuture<String> future = new CompletableFuture<>();
		 future.runAsync(runnable);
    }*/
	
	@RequestMapping(value = "/trigger", method = RequestMethod.POST)
    @ResponseBody
    public void getCyclomatic () 
	{
		 System.out.println("Triggered");
		 requestWork();
    }

}



