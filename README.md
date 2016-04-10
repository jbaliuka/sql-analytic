sql-analytic
============

SQL Parser for JAVA programming language. 
sql-analytic is derived from [jsqlparser](http://jsqlparser.sourceforge.net)  to add SQL Analytic and Spreadsheet  functions, row level security and SQL dialect extensions.

Row Level Security
============

New library supports basic AST transformations to copy tree. Row level security is also implemented as a AST transformation to add security policy, filters. Security rules use extended [PostgreSQL Policy] (http://www.postgresql.org/docs/9.5/static/sql-createpolicy.html) declaration syntax and also supports optional column list to restrict column level access. See [unit test] (./sql-parser/src/test/java/com/github/sql/analytic/transform/policy/PolicySelectTransformTest.java) for sample code.  

<pre>
CREATE POLICY name ON table_name [( column_name [,...] )]
    [ FOR { ALL | SELECT | INSERT | UPDATE | DELETE } ]
    [ TO  role_name [, ...] ]
    [ USING ( using_expression ) ]
    [ WITH CHECK ( check_expression ) ]
</pre>

Currently it has some limitations because transformation has no table meta data but future versions might be improved to interpret JDBC meta data or CREATE TABLE statements.
Also it might be used to translate AST to specific SQL dialect using deParser extensions.
Final goal of this project is to implement RDMS agnostic SQL gateway for secure CRUD via REST using application security. 
Contributions are also welcome.  

   
 
     