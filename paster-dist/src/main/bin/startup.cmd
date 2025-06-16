@echo off

:: путь до jdk 17
set JAVA_HOME=C:\Java\jdk17

::  -DappName=pasterApp для идентификации процесса
::  -Xmx2g -макс. лимит памяти
::  -Djava.net.preferIPv4Stack=true использовать ipv4 вместо ipv6
::  -DappDebug=true включение отладочных сообщений

%JAVA_HOME%\bin\java -DappName=pasterApp -Xmx2g  -Djava.net.preferIPv4Stack=true -jar paster-run.jar
 

