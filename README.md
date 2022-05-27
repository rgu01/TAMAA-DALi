# edu.collaboration.tamaa.Information.java
Program entry: Information.java. Once users click "start", a server is started, where an instance of the class "UPlanner" is run.

# What TAMAA-DALi does?
Before we explain what a *server of MMT* means, let's have a look at what TAMAA-DALi does. TAMAA-DALi works as a *middleware of MALTA*, which does the following steps of work:

1. obtaining the information of the environment and agents from MMT, 
2. calculating paths that deal with the special areas, such as forbidden areas, and visit each of the milestones,
3. generating agent model for synthesizing a mission plan that includes the paths and task schedule,
4. sending the agent model to the server of MALTA, which runs the model checker UPPAAL/UPPAAL STRATEGO to synthesize a mission plan,
5. receiving the result of synthesis (an xml file), that is, the resulting mission plan from the server of MALTA,
6. parsing the xml file of the resulting mission plan, 
7. sending the parsed mission plan to MMT.

By looking at step 1 and step 7, one can tell that an important work of TAMAA-DALi is to communicate with MMT. Since MMT and TAMAA-DALi are developed seperately in different languages (MMT in C# and TAMAA-DALi in Java), we use Thrift for the cross-language communication.

Specifically, TAMAA-DALi runs as a server that waits for a client, that is, MMT, to connect to it and send the information of environment and agents. Here, we need to distinguish the *server of MALTA* and the *server of MMT*. The former is running in Linux, which calls UPPAAL and generates mission plans. The latter (step 1 and step 7 of TAMAA-DALi) is running in Windows. If we stand in MALTA's point of view, the entire TAMAA-DALi (all 7 steps) is the middleware. For the brievity of description, we also call TAMAA-DALi the *server of MMT*, which in fact only refers to step 1 and step 7.

# edu.collaboration.tamaa.UPlanner.java
Now, let's look closely at UPlanner.java. This class extends Thread, because we want the server of MMT runs as a thread so that the GUI of TAMAA-DALi keeps responding to the users' operations. When the server of MMT is implemented by PlannerServiceHandler.java.

# edu.collaboration.tamaa.PlannerServiceHandler.java
In this class, we override two functions that are exposed to MMT, that is, computePlan and ping. The C# code of MMT callback these two functions written in Java just like calling two C# funtions that are written in its own program. This is the trick played by Thrift.
