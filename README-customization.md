# MySQL Connector/J 8.0.18 customization

Auto-commit turned off.

## Note about library jboss-common-jdbc-wrapper.jar

In build instruction <https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-installing-source.html>
jboss-common-jdbc-wrapper-3.2.3.jar or newer is mentioned. But in maven central there is only 3.2.3 version
without one or more compile-time required classes.

I found 3.2.7 version in <http://www.java2s.com/Code/Jar/j/Downloadjbosscommonjdbcwrapperjar.htm>.
This is sufficient to build library.

## Build

```
$ ant -version
Apache Ant(TM) version 1.9.14 compiled on March 12 2019

$ ant dist
```