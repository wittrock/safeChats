all:
	javac *.java -classpath ../forms-1.3.0.jar:../log4j-1.2.17.jar:.

runserver:
	java -Djavax.net.ssl.keyStore=safeChats.keystore -Djavax.net.ssl.keyStorePassword=password -cp ../log4j-1.2.17.jar:. Server

runclient:
	java -Djavax.net.ssl.trustStore=truststore -Djavax.net.ssl.trustStorePassword=password -cp ../forms-1.3.0.jar:../log4j-1.2.17.jar:. Client $(host)



clean:
	rm *.class