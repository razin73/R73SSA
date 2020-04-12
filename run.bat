md classes
del /S/F/Q R73SSA.jar

C:\prog\jdk\bin\javac.exe -d classes sources\*.java
C:\prog\jdk\bin\jar.exe cvmf sources\manifest.txt R73SSA.jar -C classes .

rem rd /S/Q docs
rem md docs
rem C:\prog\jdk\bin\javadoc.exe -version -author -d docs -sourcepath sources\*.java

rd /S/Q classes

C:\prog\jdk\bin\java.exe -jar R73SSA.jar