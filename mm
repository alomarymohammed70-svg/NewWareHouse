ant -f D:\\warehouseappgui jfxsa-run
init:
Deleting: D:\warehouseappgui\build\built-jar.properties
deps-jar:
Updating property file: D:\warehouseappgui\build\built-jar.properties
Compiling 1 source file to D:\warehouseappgui\build\classes
compile:
Deleting directory D:\warehouseappgui\dist\lib
Copying 1 file to D:\warehouseappgui\dist\lib
Detected JavaFX Ant API version 1.3
Launching <fx:jar> task from C:\Program Files\Java\jdk1.8.0_131\jre\..\lib\ant-javafx.jar
Warning: From JDK7u25 the Codebase manifest attribute should be used to restrict JAR repurposing.
         Please set manifest.custom.codebase property to override the current default non-secure value '*'.
Launching <fx:deploy> task from C:\Program Files\Java\jdk1.8.0_131\jre\..\lib\ant-javafx.jar
No base JDK. Package will use system JRE.
No base JDK. Package will use system JRE.
jfx-deployment-script:
jfx-deployment:
jar:
Copying 13 files to D:\warehouseappgui\dist\run936712956
jfx-project-run:
Executing D:\warehouseappgui\dist\run936712956\warehouseappgui.jar using platform C:\Program Files\Java\jdk1.8.0_131\jre/bin/java
Exception in Application start method
java.lang.reflect.InvocationTargetException
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at com.sun.javafx.application.LauncherImpl.launchApplicationWithArgs(LauncherImpl.java:389)
	at com.sun.javafx.application.LauncherImpl.launchApplication(LauncherImpl.java:328)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at sun.launcher.LauncherHelper$FXHelper.main(LauncherHelper.java:767)
Caused by: java.lang.RuntimeException: Exception in Application start method
	at com.sun.javafx.application.LauncherImpl.launchApplication1(LauncherImpl.java:917)
	at com.sun.javafx.application.LauncherImpl.lambda$launchApplication$155(LauncherImpl.java:182)
	at java.lang.Thread.run(Thread.java:748)
Caused by: javafx.fxml.LoadException: 
file:/D:/warehouseappgui/dist/run936712956/warehouseappgui.jar!/warehouseappgui/FXMLDocument.fxml

	at javafx.fxml.FXMLLoader.constructLoadException(FXMLLoader.java:2601)
	at javafx.fxml.FXMLLoader.loadImpl(FXMLLoader.java:2543)
	at javafx.fxml.FXMLLoader.loadImpl(FXMLLoader.java:2441)
	at javafx.fxml.FXMLLoader.loadImpl(FXMLLoader.java:3214)
	at javafx.fxml.FXMLLoader.loadImpl(FXMLLoader.java:3175)
	at javafx.fxml.FXMLLoader.loadImpl(FXMLLoader.java:3148)
	at javafx.fxml.FXMLLoader.loadImpl(FXMLLoader.java:3124)
	at javafx.fxml.FXMLLoader.loadImpl(FXMLLoader.java:3104)
	at javafx.fxml.FXMLLoader.load(FXMLLoader.java:3097)
	at warehouseappgui.Warehouseappgui.start(Warehouseappgui.java:22)
	at com.sun.javafx.application.LauncherImpl.lambda$launchApplication1$162(LauncherImpl.java:863)
	at com.sun.javafx.application.PlatformImpl.lambda$runAndWait$175(PlatformImpl.java:326)
	at com.sun.javafx.application.PlatformImpl.lambda$null$173(PlatformImpl.java:295)
	at java.security.AccessController.doPrivileged(Native Method)
	at com.sun.javafx.application.PlatformImpl.lambda$runLater$174(PlatformImpl.java:294)
	at com.sun.glass.ui.InvokeLaterDispatcher$Future.run(InvokeLaterDispatcher.java:95)
	at com.sun.glass.ui.win.WinApplication._runLoop(Native Method)
	at com.sun.glass.ui.win.WinApplication.lambda$null$148(WinApplication.java:191)
	... 1 more
Caused by: javax.xml.stream.XMLStreamException: ParseError at [row,col]:[15,1]
Message: Premature end of file.
	at com.sun.org.apache.xerces.internal.impl.XMLStreamReaderImpl.next(XMLStreamReaderImpl.java:604)
	at javax.xml.stream.util.StreamReaderDelegate.next(StreamReaderDelegate.java:88)
	at javafx.fxml.FXMLLoader.loadImpl(FXMLLoader.java:2513)
	... 17 more
Exception running application warehouseappgui.Warehouseappgui
Java Result: 1
Deleting directory D:\warehouseappgui\dist\run936712956
jfxsa-run:
BUILD SUCCESSFUL (total time: 2 seconds)
