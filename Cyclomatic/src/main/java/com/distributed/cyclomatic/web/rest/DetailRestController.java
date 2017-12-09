package com.distributed.cyclomatic.web.rest; 
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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

   
	public static LinkedList<File> workQueue = new LinkedList<File>();
    
	
	//Record File
	static String RECORD_FILE_NAME = "two_workers.xlsx";
    int COMMIT_ROW_COUNT = 0;
    int RESPONSE_COUNT = 1;
    int COMMITS_COUNT = 0;
    
    
	//Workers
	static String worker1 = "http://localhost:8090/GetCylomatic/";
	static String worker2 = "http://localhost:9000/GetCylomatic/";
	
	
	//Pull the repository
    public static void getRepository()
    {

        final String REMOTE_URL = "https://github.com/d3/d3.git";

            try
            {

            	File Path = new File("d3_git");
            	
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
    
    
    //Setup the Record file
    public static void setRecordFile()
    {
    	try {
    		
    	    XSSFWorkbook workbook = new XSSFWorkbook();
    	    XSSFSheet sheetTimeTaken = workbook.createSheet("Time_Taken");
    	    XSSFSheet sheetCyclomaticByCommits = workbook.createSheet("Cyclomatic_By_Commits");
            FileOutputStream outputStream = new FileOutputStream(RECORD_FILE_NAME);
            workbook.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    //Get the list of commits
    public <T> List<T> getCommits() 
    {
    	List commitList = new ArrayList();
    	try
    	{
        Repository repo = new FileRepository("d3_git/.git");
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
    
    
    
    // send file to nodes
    /*public void sendCommit(String commitId, String worker)
    {
    	MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
    	parameters.add("file", new FileSystemResource(fileLoc));
        
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.ALL);
        
    	RestTemplate restTemplate = new RestTemplate();
    	
    	
    	MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
    	map.add("email", "first.last@example.com");

    	HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

    	ResponseEntity<String> response = restTemplate.postForEntity( url, request , String.class );
    	
    	
    }*/
    
    
    
    public String sendCommit(String commitId, String worker)
    {
    	
    	URI append_url;
		try {
			append_url = new URI (worker + commitId);//restTemplate.put(append_url, String.class);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> Response  = restTemplate.getForEntity(append_url, String.class);
			return Response.toString();
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return "Error";
		}
		

    }
    
    
    

    
    // Clone each commit separately
    public void getEachCommit(String commitId)
    {
    	File gitDir = new File("d3_git");
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
    
    
    public void addFilesToQueue(String commitId)
    {
    	
    	File f = new File("d3_git");
    	String[] extensions = {"js"};
    	Collection<File> matchingFiles = FileUtils.listFiles(f, extensions, true); // Finding the Javascript files

    	File destDir = new File("Commits\\commit"+ commitId);
    	
    	for(File matchFile:matchingFiles)
    	{
	        try {
		            FileUtils.copyFileToDirectory(matchFile, destDir);
		        } 
				catch(Exception e) 
	            {e.printStackTrace();}
    	}
    	
    	Collection<File> newFiles = FileUtils.listFiles(destDir,TrueFileFilter.INSTANCE,TrueFileFilter.INSTANCE);
        for(File newFile:newFiles)
    	{
            workQueue.add(newFile);
    	}

    }
    
    
    
    //Assign Master-worker work
    public void assignWorkForTwo(List<String> commitList)
    {
    	try {
    		FileInputStream file = new FileInputStream(new File(RECORD_FILE_NAME));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheetTimeTaken = workbook.getSheetAt(0);
    		Row row1 = sheetTimeTaken.createRow(0);
     	    Cell cell1 = row1.createCell(0);
     	    Cell cell2 = row1.createCell(1);
     	    cell1.setCellValue("Start Time");
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
        }
        
    	System.out.println("Assignment Started");
    	//COMMITS_COUNT = commitList.size();
    	COMMITS_COUNT = 10+1;
        for (int i=0;i<10;i++) 
    	{
    		System.out.println("Assignment: " + i);
    		//if(i%2==0)
            String resp = sendCommit(commitList.get(i), worker1);
            System.out.println(resp);
    			//System.out.println("NUmber 1");
    		//else
    	    //sendCommit(commitList.get(i), worker2);
    			//System.out.println("NUmber 2");
    	}
           	
    }
    
    
    //Master worker algorithm
    public void masterWorker()
    {
    	List<String> commitList = getCommits();
    	System.out.println("Extracted Commits");
    	//int i =0;
    	/*for (String commitId : commitList) 
    	{
    		System.out.println(i);
    		getEachCommit(commitId);
    		i++;
    	}*/
    	
    	/*for (i=0;i<10;i++) 
    	{
    		System.out.println(i);
    		getEachCommit(commitList.get(i));
    	}*/
    	
    	//System.out.println("Work Queue Filled");
    	assignWorkForTwo(commitList);
    
    }
    
    
    
    
    public void saveOutput(String cycloValue)
    {
    	try {
    		FileInputStream file = new FileInputStream(new File(RECORD_FILE_NAME));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheetTimeTaken = workbook.getSheetAt(1);
    		Row row1 = sheetTimeTaken.createRow(COMMIT_ROW_COUNT++);
     	    Cell cell1 = row1.createCell(0);
     	    cell1.setCellValue(cycloValue);
     	    FileOutputStream outputStream = new FileOutputStream(new File(RECORD_FILE_NAME));
            workbook.write(outputStream);
            file.close();
            outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    // Connections
    
    @RequestMapping(value = "/RecieveCyclomatic/{value}", method = RequestMethod.PUT)
    @ResponseBody
    public void recieveCyclomatic(@PathVariable("value") String cycloValue, HttpServletResponse response) {
    	
    	//Send response
    	int code = HttpServletResponse.SC_OK;
    	java.io.PrintWriter wr;
		try {
			wr = response.getWriter();
			response.setStatus(code);
	        wr.print("RECIVED_WORK");
	        wr.flush();
	        wr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	System.out.println("RESPONSE_COUNT: " + RESPONSE_COUNT++);
    	saveOutput(cycloValue);
    	if(RESPONSE_COUNT == COMMITS_COUNT)
    	{
    		try {
        		FileInputStream file = new FileInputStream(new File(RECORD_FILE_NAME));
                XSSFWorkbook workbook = new XSSFWorkbook(file);
                XSSFSheet sheetTimeTaken = workbook.getSheetAt(0);
        		Row row1 = sheetTimeTaken.createRow(1);
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
            }
    		System.out.println(".......COMPLETED......");
    	}
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
		
    	
    }
    
    

    static{
    	getRepository();
    	System.out.println("Repository setup completed");
    	setRecordFile();
    	System.out.println("Recordbook created");
    }

    
    
}