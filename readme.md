## postgres 建数据库语句

```postgresql
create user mateadmin with password 'mateadmin';
create database mate with owner mateadmin;
GRANT ALL PRIVILEGES ON DATABASE mate to mateadmin;
```
