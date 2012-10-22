all:
	javac *.java -cp ./forms-1.3.0.jar:./com/jgoodies/forms/layout/:.

runserver:
	java -Djavax.net.ssl.keyStore=safeChats.keystore -Djavax.net.ssl.keyStorePassword=password Server

runclient:
	java -Djavax.net.ssl.trustStore=truststore -Djavax.net.ssl.trustStorePassword=password Client



clean:
	rm *.class