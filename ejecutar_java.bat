@echo off
cd "c:\tareas universitarios\TAREAS U\Tareas Univ\CICLO7\Curso Integrador I\ecovivashop"
mvn dependency:build-classpath -q -Dmdep.outputFile=classpath.txt
set /p CLASSPATH=<classpath.txt
java -cp "target/classes;%CLASSPATH%" %1
del classpath.txt