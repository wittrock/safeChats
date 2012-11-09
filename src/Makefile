all:
	javac *.java -classpath ../forms-1.3.0.jar;.

runserver:
	java -Djavax.net.ssl.keyStore=safeChats.keystore -Djavax.net.ssl.keyStorePassword=password Server

runclient:
	java -Djavax.net.ssl.trustStore=truststore -Djavax.net.ssl.trustStorePassword=password -cp ../forms-1.3.0.jar:. Client $(host)



clean:
	rm *.class