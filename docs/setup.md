## Setup
### Check java version
```shell
java -version
```
Output should be something like:
```shell
openjdk version "11.0.16" 2022-07-19
OpenJDK Runtime Environment (build 11.0.16+8-post-Ubuntu-0ubuntu122.04)
OpenJDK 64-Bit Server VM (build 11.0.16+8-post-Ubuntu-0ubuntu122.04, mixed mode, sharing)
```
If not please install java on your machine

### Download and extract flink
```shell
wget https://dlcdn.apache.org/flink/flink-1.15.2/flink-1.15.2-bin-scala_2.12.tgz
tar -xvf flink-1.15.2-bin-scala_2.12.tgz
```

### Download connector jars 
```shell
cd flink-1.15.2-bin-scala_2.12/lib
wget https://repo1.maven.org/maven2/org/apache/flink/flink-connector-kafka/1.15.2/flink-connector-kafka-1.15.2.jar
wget https://repo1.maven.org/maven2/org/apache/kafka/kafka-clients/3.2.1/kafka-clients-3.2.1.jar
wget https://repo1.maven.org/maven2/org/apache/seatunnel/flink-sql-connector-jdbc/2.1.3/flink-sql-connector-jdbc-2.1.3.jar
```
### Add flink path to your `.bashrc` file
```shell
vim ~/.bashrc
```
At the end of the file, add this line
```shell
export FLINK_HOME="<path_to_flink>"
#optional - the below line makes flink commands available in any path
export PATH=$PATH:$FLINK_HOME/bin
```