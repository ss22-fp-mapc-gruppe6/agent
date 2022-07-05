#### how to run
The old way for running agents is still viable. 

For easier development the config files of both server and agent have been moved into the `src/main/resources` folder of this module.

You can hardcode what configuration gets executed into the `g6Agent.run.RunConfig` class and then run the `Agents`, `Server` or `Both` class from within your IDE.

If you would rather start it from outside the IDE you can use the maven exec plugin with the command
```shell
mvn exec:java -Dexec.mainClass="g6Agent.run.Server"
mvn exec:java -Dexec.mainClass="g6Agent.run.Agents"
mvn exec:java -Dexec.mainClass="g6Agent.run.Both"
```
from this folder or
```shell
mvn exec:java -Dexec.mainClass="g6Agent.run.Server" -pl javaagents
mvn exec:java -Dexec.mainClass="g6Agent.run.Agents" -pl javaagents
mvn exec:java -Dexec.mainClass="g6Agent.run.Both" -pl javaagents
```
from the root folder.


