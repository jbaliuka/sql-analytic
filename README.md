sql-analytic
============

SQL Parser for JAVA programming language. 
sql-analytic is derived from jsqlparser http://jsqlparser.sourceforge.net  to add SQL Analytic and Spreadsheet  functions, row level security

Row Level Security
============

New library supports basic AST transformations to copy tree. Row level security is also implemented as a AST transformation to add security policy, filters. Security rules use extended [PostgreSQL Policy] (http://www.postgresql.org/docs/9.5/static/sql-createpolicy.html) declaration syntax and also supports optional column list to restrict column level access. See [unit test] (./blob/master/src/test/java/com/github/sql/analytic/transform/policy/PolicySelectTransformTest.java) for sample code.  
     