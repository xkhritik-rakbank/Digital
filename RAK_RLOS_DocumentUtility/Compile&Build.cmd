@REM Copyright (c) 2004 NEWGEN All Rights Reserved.

@REM ************************************************************************************************
@REM Modify these variables to match your environment
	cls
	set JAVA_HOME="D:\Sajan\jdk1.8.0_92"
	set JTS_LIBPATH=lib
	set MYCLASSPATH=bin
	set LIBCLASSPATH=%JTS_LIBPATH%\DataEncryption.jar;%JTS_LIBPATH%\log4j-1.2.14.jar;%JTS_LIBPATH%\ngejbcallbroker.jar;%JTS_LIBPATH%\omnishared.jar;%JTS_LIBPATH%\SecurityAPI.jar;%JTS_LIBPATH%\wfdesktop.jar;%JTS_LIBPATH%\wfsclient.jar;%JTS_LIBPATH%\com.ibm.ws.ejb.thinclient_8.5.0.jar;%JTS_LIBPATH%\stubswfscustom.jar;%JTS_LIBPATH%\stubwfs.jar;%JTS_LIBPATH%\Amazon.jar;%JTS_LIBPATH%\aws-java-sdk-1.11.40.jar;%JTS_LIBPATH%\azure-storage-5.0.0.jar;%JTS_LIBPATH%\commons-io-2.0.1.jar;%JTS_LIBPATH%\ISPack.jar;%JTS_LIBPATH%\jdts.jar;%JTS_LIBPATH%\NIPLJ.jar;%JTS_LIBPATH%\nsms.jar;%JTS_LIBPATH%\commons-codec-1.7.jar;%JTS_LIBPATH%\ejb.jar;%JTS_LIBPATH%\ejbclient.jar;%JTS_LIBPATH%\stubs.jar
@REM ************************************************************************************************

@REM ************************************************************************************************
@REM Compile SockectClient

	%JAVA_HOME%\bin\javac -d %MYCLASSPATH% -classpath %LIBCLASSPATH%;%MYCLASSPATH% src\com\newgen\common\*.java
	%JAVA_HOME%\bin\javac -d %MYCLASSPATH% -classpath %LIBCLASSPATH%;%MYCLASSPATH% src\com\newgen\Falcon\Document\*.java
	%JAVA_HOME%\bin\javac -d %MYCLASSPATH% -classpath %LIBCLASSPATH%;%MYCLASSPATH% src\com\newgen\main\*.java

	pause
@REM ************************************************************************************************


@REM Copyright (c) 2004 NEWGEN All Rights Reserved.

@REM *********************JAR BUILDING***************************************************************************
@REM Modify these variables to match your environment
	cls
	set JAVA_HOME="D:\Sajan\jdk1.8.0_92"
	set MYCLASSPATH=bin
	set JARPATH=..
@REM ************************************************************************************************

 	cd %MYCLASSPATH%

@REM mqsocketserver jar
    %JAVA_HOME%\bin\jar -cvfm %JARPATH%\falcon_doc_attach.jar ..\MANIFEST.MF com\newgen\common\*.class
    %JAVA_HOME%\bin\jar -uvf %JARPATH%\falcon_doc_attach.jar com\newgen\main\*.class
    %JAVA_HOME%\bin\jar -uvf %JARPATH%\falcon_doc_attach.jar com\newgen\Falcon\Document\*.class
	%JAVA_HOME%\bin\jar -uvf %JARPATH%\falcon_doc_attach.jar ..\src\com\newgen\common\*.java
	%JAVA_HOME%\bin\jar -uvf %JARPATH%\falcon_doc_attach.jar ..\src\com\newgen\main\*.java
	%JAVA_HOME%\bin\jar -uvf %JARPATH%\falcon_doc_attach.jar ..\src\com\newgen\Falcon\Document\*.java
	pause
@REM ************************************************************************************************


