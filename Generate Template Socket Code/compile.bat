set JAVA_COMPILER="C:\Progra~1\Java\jdk1.7.0_51\bin\javac"
set itext_LIBPATH="D:\IBM\Newgen Product\Lib\omnidocs_library\odwebpdf"
set CLASSPATH=.;lib\commons-discovery-0.2.jar;lib\commons-io-1.4.jar;lib\commons-logging-1.0.4.jar;lib\jcifs-1.3.17.jar;lib\log4j-1.2.14.jar;lib\xercesImpl-2.8.1.jar;lib\xml-apis.jar;lib\Populatepdf.jar;lib\webdesktop.jar;lib\ngejbcallbroker.jar;lib\wfdesktop.jar;lib\ISPack.jar;lib\nsms.jar;lib\jdts.jar;lib\NIPLJ.jar;;lib\aws-java-sdk-1.11.40.jar;lib\omnishared.jar;lib\ejbclient.jar;lib\ejb.jar;lib\azure-storage-7.0.0.jar;lib\dbStub.jar;lib\java-json.jar;lib\jboss-client.jar;lib\nglogger.jar;lib\stubswfscustom.jar;lib\stubwfs.jar;lib\wfsclient.jar;lib\wfsshared.jar;lib\wrapper.jar;lib\com.ibm.ws.ejb.thinclient_8.5.0.jar;lib\com.ibm.ws.orb_8.5.0.jar;lib\java-json.jar;lib\ngutility.jar;lib\stubs.jar;lib\ofwebshared.jar;lib\itextpdf-5.3.2.jar;lib\xmlworker-1.2.1.jar;lib\jsoup-1.8.1.jar;

%JAVA_COMPILER%   -cp %CLASSPATH%  com\newgen\generate\*.java
pause


