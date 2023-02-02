## Download confluent kafka distribution
```shell
curl -O http://packages.confluent.io/archive/7.2/confluent-7.2.1.tar.gz
tar -xvf confluent-7.2.1.tar.gz
cd confluent-7.2.1
```
Set confluent home path in your `bashrc` file, append this line to your `bashrc` file
```shell
export CONFLUENT_HOME=<path_to_extracted_confluent>
export PATH=$PATH:$CONFLUENT_HOME/bin
```