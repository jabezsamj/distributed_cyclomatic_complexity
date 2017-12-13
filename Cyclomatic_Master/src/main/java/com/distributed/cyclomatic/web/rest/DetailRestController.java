package com.distributed.cyclomatic.web.rest; 
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//Excel sheet
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


//Git import
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.gmetrics.*;
import org.gmetrics.metric.cyclomatic.CyclomaticComplexityMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;



@Controller("DetailRestController")
public class DetailRestController {

   
	
    //logs
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	//Record File
	static String TIME_RECORD_FILE_NAME = "three_workers_time.txt";
	static String COMMITS_RECORD_FILE_NAME = "three_workers_commits.txt";
	File time_file = new File(TIME_RECORD_FILE_NAME);
	File commit_file = new File(COMMITS_RECORD_FILE_NAME);
	final static String newLine = System.getProperty("line.separator");
    int RESPONSE_COUNT = 0;
    int COMMITS_COUNT = 0;

	//Workers
    String workers[] = {"http://52.214.237.212:8090/GetCylomatic/",
    		            "http://34.250.62.132:8090/GetCylomatic/",
    		            "http://34.242.50.62:8090/GetCylomatic/"};
    
    int WORKER = 0;

	//Pull the repository
    public static void getRepository()
    {

        final String REMOTE_URL = "https://github.com/airbnb/javascript.git";

            try
            {

            	File Path = new File("airbnb_git");
            	
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


    //Get the list of commits
    public <T> List<T> getCommits() 
    {
    	List commitList = new ArrayList();
    	try
    	{
        Repository repo = new FileRepository("airbnb_git/.git");
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
        
        repo.close();
      }
        catch(IOException ie)
        {
    	ie.printStackTrace();
        }
        return commitList;
    }
 // Clone each commit separately
    public void getEachCommit(String commitId)
    {
    	File gitDir = new File("airbnb_git");
        Git git;
		try {
			git = Git.open(gitDir);
			try {
				git.checkout().setName(commitId).call();
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
 
    
    public void writeStart()
    {
    	try {
    		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
     	    Date date = new Date();
			FileUtils.writeStringToFile(time_file, newLine+"START_TIME : " + dateFormat.format(date), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    //Assign Master-worker work
    public void assignWork(List<String> commitList, int wrokerIndex)
    //public void assignWorkForTwo(String commitId, int wrokerIndex)
    {
    	
    	/*RestTemplate restTemplate = new RestTemplate();
    	try {
			restTemplate.postForLocation(new URI (workers[wrokerIndex] + commitId), String.class);
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	commitList.parallelStream()
        .forEach(commitId -> {
        	    
				RestTemplate restTemplate = new RestTemplate();
	            try {
					restTemplate.postForLocation(new URI (workers[wrokerIndex] + commitId), String.class);
				} catch (RestClientException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        });
           	
    }
    
    
    public class AssignWorkRunnable implements Runnable {
        
    	List<String> commitList;
    	int wrokerIndex;
	    public AssignWorkRunnable(List<String> commitList, int wrokerIndex) {
			 this.commitList = commitList;
			 this.wrokerIndex = wrokerIndex;
		}

		public void run(){
			assignWork(commitList, wrokerIndex);
	    }
	  }

    //Master worker algorithm
    public void masterWorker()
    {
    	List<String> commitList = getCommits();
    	System.out.println("Extracted Commits");
    	COMMITS_COUNT = (commitList.size())- 400;
    	System.out.println("COMMITS_COUNT : " + COMMITS_COUNT);
    	int partitionSize = (commitList.size()/3)+1;
    	List<List<String>> partitions = new LinkedList<List<String>>();
    	for (int i = 0; i < commitList.size(); i += partitionSize) {
    		partitions.add(commitList.subList(i,
    	            Math.min(i + partitionSize, commitList.size())));
    	}
    	writeStart();
    	System.out.println("Assignment Started");

    	
    	CompletableFuture<String> future = new CompletableFuture<>();
    	AssignWorkRunnable runnable1 = new AssignWorkRunnable(partitions.get(0), 0);
    	AssignWorkRunnable runnable2 = new AssignWorkRunnable(partitions.get(1), 1);
    	AssignWorkRunnable runnable3 = new AssignWorkRunnable(partitions.get(2), 2);
		future.runAsync(runnable1);
		future.runAsync(runnable2);
		future.runAsync(runnable3);
    }

    public void saveOutput(String cycloValue, int responseCount)
    {
    	
    	try {
			FileUtils.writeStringToFile(commit_file, newLine+responseCount+"  : "+cycloValue, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }
    
    
    public void writeEnd()
    {
    	/*try {
    		FileInputStream file = new FileInputStream(new File(RECORD_FILE_NAME));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheetTimeTaken = workbook.getSheetAt(0);
    		Row row1 = sheetTimeTaken.createRow(TIME_ROW_COUNT++);
     	    Cell cell1 = row1.createCell(0);
     	    Cell cell2 = row1.createCell(1);
     	    cell1.setCellValue("End Time");
     	    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
     	    Date date = new Date();
     	    cell2.setCellValue(dateFormat.format(date));
     	    FileOutputStream outputStream = new FileOutputStream(new File(RECORD_FILE_NAME));
            workbook.write(outputStream);
            file.close();
            outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    	
    	try {
    		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
     	    Date date = new Date();
			FileUtils.writeStringToFile(time_file, newLine+"FINISH_TIME : " + dateFormat.format(date), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(".......COMPLETED......");
    }
    		
    public class saveOutputRunnable implements Runnable
    {
    	String cycloValue;
    	public saveOutputRunnable(String cycloValue) {
    		this.cycloValue = cycloValue;
    	}
    	
    	
    	public void run(){
    	System.out.println("RESPONSE_COUNT: " + ++RESPONSE_COUNT);
	    saveOutput(cycloValue, RESPONSE_COUNT);
	    if(RESPONSE_COUNT == COMMITS_COUNT){		
	    	writeEnd();
	    	COMMITS_COUNT = COMMITS_COUNT + 5;
	        }
    	}
    }
          

	// Connections
    @RequestMapping(value = "/RecieveCyclomatic/{value}", method = RequestMethod.POST)
    @ResponseBody
    public void recieveCyclomatic(@PathVariable("value") String cycloValue) {
        
    	saveOutputRunnable runnable = new saveOutputRunnable(cycloValue);
		CompletableFuture<String> future = new CompletableFuture<>();
		future.runAsync(runnable);
    }
    
    @RequestMapping(value = "/Start", method = RequestMethod.GET)
    @ResponseBody
    public void StartProcess(HttpServletResponse response)  
    {
    	int code = HttpServletResponse.SC_OK;
    	java.io.PrintWriter wr;
		try {
			wr = response.getWriter();
			response.setStatus(code);
	        wr.print("PROCESS_STARTED");
	        wr.flush();
	        wr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		System.out.println("Process started");
		masterWorker();
		//assignWorkForTwo("3792c0f2afe3522d6b3efe038e32350c9ee69834", 1);
	
    }

    static{
    	getRepository();
    	System.out.println("Repository setup completed");
    }


}