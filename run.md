```bash
mvn -pl javaagents exec:java -Dexec.mainClass="g6Agent.Main" -Dexec.args="javaagents/conf/MyTestAgent"
```


```bash
mvn -pl server exec:java -Dexec.args="-conf server/conf/SampleConfig.json --monitor"
```