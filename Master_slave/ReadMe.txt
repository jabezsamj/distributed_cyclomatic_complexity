
Master Slave Architecture

Performance is in the image "Master_slave_time_taken.png". The time taken is in minutes.


****************************Concept*************************************

- The Master will distribute the works to the clients and wait for the result. The Workers report with the results. The Master collates the results




****************************Implementation******************************

- Coded in Java Spring boot
- Git repository - Airbnb was used which has javascript code (1551 commits). The repository @ https://github.com/airbnb/javascript
- The Server makes a list of the commits in this respository.
- Splits the list into the number of workers
- Create a thread for each of the workers and simultaneously pushes the commits to the workers with a REST api call
- The wroker recives the requests each in a thread, 
- The clinet makes a copy of each of the commits and traverse through the folder to find Java script files, calculate the cyclomatic complexity.
- After finding the cumulative cyclomatic complexity the client sends the value to the server by calling a REST api of the server
- The server copies the result onto a text file
- The server records the time when it started sending the work (Text file)
- The server will not wait for the number of responses to be same as number of works assignments, but starts recording the time when there are 1000 responses
- The setup was executed on AWS instances

 (The result files aare in the "results" folder)



**************************** Observations *******************************

- The server could not send works simultaneously to the workers. Paralell streams were used to oversome this
- The Worker was slow in responding to the work requests when it was not threaded. The requests are handled in asynchronuos threads to overcome this
- The System was slow in terms of resource availability when small instances were used. Large systems were used to provide more resources (None of the executions exceeded more than 10 minutes)
- The server is not able to recieve more than 1120 requests. Various checkpoints were verified an bigger machines were used. The values are calculated in the workers and well handled by the master
  But the server is not able to recieve the requests (The server can handle much more when tried with different request setup). 
  There are high chances that the AWS traffic management is affecting the flow.



****************************Limitations**************************

Limitations for the demonstrations are below:

- It was not a single execution code all the steps were triggered once and all the calculations were made
  For each set of workers changes were declared and loaded to Instances. There is a scope of improvement in this.
- The server is not able to recieve more than 1120 responses. There is possibility of AWS stopping the traffic. The reason should be investigated by moving to a different platform
- The results can be handled using databases