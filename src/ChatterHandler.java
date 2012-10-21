import java.io.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ServerSocketFactory;
import javax.net.ssl.*;


public abstract class ChatterHandler implements Runnable {
    Socket sock;
    Server server;

    public ChatterHandler(Server server, Socket sock){
    	this.server = server;
    	this.sock = sock;
    }

    public abstract void run();
}