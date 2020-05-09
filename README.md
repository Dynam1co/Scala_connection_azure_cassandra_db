# Developing a Scala app with Cassandra API using Azure Cosmos DB
Azure Cosmos DB is a globally distributed multi-model database. One of the supported APIs is the Cassandra API. This sample walks you through creation of keyspace, table, inserting and querying the data.

## Important: configure JAVA_HOME environ variable 
It is important to have created an environment variable called JAVA_HOME pointing to the java installation directory.

1. Access to system properties:
![Config](img/01.png)

2. Enter in system environ variables:
![Config](img/02.PNG)

3. In system's variables click in New:
![Config](img/03.PNG)

**NOTE:** Reboot the system for the changes to take effect

## Configure connection string
In src/main/resources/default.properties you have to configure connection data to your Cassandra DB.
```
cassandra_host=<FILLME>
cassandra_username=<FILLME>
cassandra_password=<FILLME>
ssl_keystore_file_path=<FILLME>
ssl_keystore_password=<FILLME>
```

If ssl_keystore_file_path is not given in default.properties, then by default <JAVA_HOME>/jre/lib/security/cacerts will be used If ssl_keystore_password is not given in config.properties, then the default password 'changeit' will be used
