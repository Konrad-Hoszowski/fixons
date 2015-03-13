# Introduction #

How to use jdbc-fixture.


# Details #

Step-by-step configuration
  * add jdbc-fixture-{version}.jar as the dependency
  * create wiki page

```
!3 Verify MySQL DB is setup with UTF8 encoding
|!-org.fos.fixture.JdbcFixture-!||
|INIT|jdbc:mysql://localhost:3306/moclets|com.mysql.jdbc.Driver|moclets|wbt_0199|
|SETUP_TEARDOWN|DROP TABLE animal|
|EXEC_UPDATE|CREATE TABLE animal( id  varchar(255),  PRIMARY KEY (id),  name   VARCHAR(300)      )|
|EXEC_UPDATE|DROP TABLE animal|
|FAIL_EXEC_UPDATE|CREATE TABLE animal( id  varchar(278),  PRIMARY KEY (id),  name   VARCHAR(300)      )|
|CLOSE||
```