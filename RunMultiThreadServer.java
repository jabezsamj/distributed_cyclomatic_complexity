package servers;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;


public class RunMultiThreadServer 
{
	
    public static void main (String args[])
    {
        	
    	//MultiThreadedServer server = new MultiThreadedServer(9000);
    	//new Thread(server).start();
        
    	/*
    	final String host = "localhost";
		final int portNumber = 9000;
    	//System.out.println("Stopping Server");
    	//server.stop();
    	
    	for(int i=0;i<2;i++)
    	{
    		try
    		{
			Socket socket = new Socket(host, portNumber);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			System.out.println("server says:" + br.readLine());

            socket.close();
    		}
    		catch (IOException e) 
            {
                e.printStackTrace();
            }
    	}
    	
       */
    	
    	

      }
	
    public void CloneRemoteRepository () throws InvalidRemoteException, TransportException, GitAPIException
    {

        final String REMOTE_URL = "https://github.com/rubik/argon.git";
        
       

            // prepare a new folder for the cloned repository
            try
            {
            	File localPath = File.createTempFile("argon_repo", "");
                if(!localPath.delete()) 
                {
                    throw new IOException("Could not delete temporary file " + localPath);
                }
                
                // then clone
                System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);
                try (Git result = Git.cloneRepository()
                        .setURI(REMOTE_URL)
                        .setDirectory(localPath)
                        .call()) {
        	        // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
        	        System.out.println("Having repository: " + result.getRepository().getDirectory());
                }
            }
            catch (IOException e) 
            {
            	e.printStackTrace();
            }
            

            
     
    }

}
