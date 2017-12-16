
Work Stealing Architecture

Performance is in the image "Work_stealing_time_taken.png". The time taken is in minutes


****************************Concept*************************************

- The Master will wait for the workers to request for work. The Master assigns a task and waits for the worker to return the with the result. 
  The masters gives the next work once the worker returns.




****************************Implementation******************************

- Coded in Java Spring boot
- Git repository - Airbnb was used which has javascript code (1551 commits). The repository @ https://github.com/airbnb/javascript
- The Master makes a list of the commits in this respository.
- The master intimates all the workers to start requesting for work
- The Wrokers requests the master for work and gets a commit in return
- The wroker makes a copy of each of the commits and traverse through the folder to find Java script files, calculate the cyclomatic complexity.
- After finding the cumulative cyclomatic complexity the client sends the value to the server by calling a REST api of the server
- On submission of the result, the worker is internally triggered to request for work
- Again the loop continues
- The loop stops when the server responds with a no work left message when a worker request for work
- The setup was executed on AWS instances

 (The result files are in the "results" folder)



**************************** Observations *******************************

- The process was slower compared to other archiitectures as there are sequential two way communication required for each work
- Paralell streams were not required as the the work assignment is per request basis
- The System was slow in terms of resource availability when small instances were used. Large systems were used to provide more resources (None of the executions exceeded more than 15 minutes)
- The server is not able to recieve more than 1120 requests. Various checkpoints were verified an bigger machines were used. The values are calculated in the workers and well handled by the master
  But the server is not able to recieve the requests (The server can handle much more when tried with different request setup). 
  There are high chances that the AWS traffic management is affecting the flow.



****************************Limitations**************************

Limitations for the demonstrations are below:

- The performance could have been improved if the requests were handled with threads for each requets, intead of two connections getting establised for each work. This can be a future improvement.
- The server is not able to recieve more than 1120 responses. There is possibility of AWS stopping the traffic. The reason should be investigated by moving to a different platform
- The results can be handled using databases