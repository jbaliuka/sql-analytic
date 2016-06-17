sql-analytic [![Build Status](https://travis-ci.org/jbaliuka/sql-analytic.svg?branch=master)](https://travis-ci.org/jbaliuka/sql-analytic)
============

SQL Parser for JAVA programming language. 
sql-analytic is derived from [jsqlparser](http://jsqlparser.sourceforge.net)  to add SQL Analytic and Spreadsheet  functions, row level security and SQL dialect extensions.

Row Level Security
============

New library supports basic AST transformations to copy tree. Row level security is also implemented as a AST transformation to add security policy, filters. Security rules use extended [PostgreSQL Policy] (http://www.postgresql.org/docs/9.5/static/sql-createpolicy.html) declaration syntax and also supports optional column list to restrict column level access. 
See [unit test] (./sql-parser/src/test/java/com/github/sql/analytic/transform/policy/PolicySelectTransformTest.java) 
and [h2 test] (./sql-dialect/sql-dialect-h2/src/test/java/com/github/sql/analytic/dialect/h2/H2SessionTest.java)
for sample code.  

<pre>
CREATE POLICY name ON table_name [( column_name [,...] )]
    [ FOR { ALL | SELECT | INSERT | UPDATE | DELETE } ]
    [ TO  role_name [, ...] ]
    [ USING ( using_expression ) ]
    [ WITH CHECK ( check_expression ) ]
</pre>


 
     