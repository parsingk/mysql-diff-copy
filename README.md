# Mysql Data Diff & Copy

* table diff and copy **(copy for only tiny table)**
* procedures, functions diff   



## Guides


* Spring Bean Setting
``` 
    @Bean
    public DatabaseDiffer config() {    
        return new DiffCopyConfig().builder()
            .setSource(${SOURCE_DATABASE_NAME}, new NamedParameterJdbcTemplate(${YOUR_SOURCE_DATASOURCE}))
            .setDestination(${TARGET_DATABASE_NAME}, new NamedParameterJdbcTemplate(${YOUR_TARGET_DATASOURCE}))
            .build();
    }
``` 
    
    
* Mysql System Variable Setting
```   
 group_concat_max_len = 1048576
```   
 
* Mysql tally Table Create
``` 
    CREATE TABLE `tally` (
      `id` int unsigned NOT NULL,
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

    tally table insert id 1 to 10000.
``` 
* Use Granted User For information_schema(select) and   
your schema(create table, alter table, select, insert, update)


## Function References


* Get All Table List
```   
Tables getTableList() throws SQLException
```

* Get Source Table Data  
if count is 0, it will get all rows of table.
```  
SourceTableData getSourceTableData(String tableName, long last, int count);
```


* Get Destination Table Data   
* if count is 0, it will get all rows of table.
```
DestinationTableData getDestinationTableData(String tableName, long last, int count);
```

- Migration From source To destination.    
  - creating table if not exists    
  - adding columns if not exists   
  - updating columns if different
  - inserting data, updating data.   
  - the table must have primary key.   

- Response Code   
  - OK(1)   
  - ERROR_CREATE_TABLE(-99)  
  - ERROR_UPDATE_COLUMN(-100)  
  - ERROR_INSERT_COLUMN(-101)  
  - ERROR_UPSERT_DATA(-102)    
```
DataMigration upsert(String tableName);
```

* Get All Procedures and Functions From both of DataSources.
```
Routines getRoutines();
```    

* Diff Procedure's or Function's Text.
```
LinkedList<TextDiffUtil.Diff> getRoutinesDiffText(String routineName);
```
<br>

### Reference Wiki Documentation
**https://github.com/parsingk/mysql-diff-copy/wiki/Response-Object-References**

<br>

<span style="color:red">* **IMPORTANT !**</span>   
*The table must have primary key, when you call upsert function.*  
*If no primary key, it will set on first column.*

