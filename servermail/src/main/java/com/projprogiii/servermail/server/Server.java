package com.projprogiii.servermail.server;

import com.projprogiii.servermail.ServerApp;
import com.projprogiii.servermail.model.log.LogManager;
import com.projprogiii.servermail.server.config.ConfigManager;
import com.projprogiii.servermail.server.session.Session;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server extends Thread {

    private final int threadsNumber;
    private final int timeout;
    private final int serverPort;
    private final LogManager logManager;
    private final ExecutorService serverThreads;
    private ServerSocket serverSocket;

    private Server(){
        ConfigManager configManager = ConfigManager.getInstance();
        logManager = ServerApp.model.getLogManager();

        logManager.printSystemLog("Loading initial configuration server.properties...");
        this.threadsNumber = Integer.parseInt(configManager.
                readProperty("server.threads_number"));
        this.timeout = Integer.parseInt(configManager.
                readProperty("server.timeout"));
        this.serverPort = Integer.parseInt(configManager.
                readProperty("server.server_port"));
        printLogInit();

        serverThreads = Executors.newFixedThreadPool(threadsNumber);
    }
    public static Server getInstance(){
        return new Server();
    }

    public int getServerPort() {
        return serverPort;
    }

    private void printLogInit(){
        logManager.printSystemLog("Configuration loaded.");
        logManager.printSystemLog("Max thread in thread pool: " +
                threadsNumber + ".");
        logManager.printSystemLog("Timeout to accept a new connection: " +
                timeout + " ms.");
        logManager.printSystemLog("Server port: " + serverPort + ".");
        logManager.printLog("");
        logManager.printSystemLog("Hello!");
    }
    public void start() {

        Socket currentSocket = null;
        try {
            serverSocket = new ServerSocket(serverPort);
            while (!Thread.interrupted()) {
                currentSocket = serverSocket.accept();
                currentSocket.setSoTimeout(timeout);
                serverThreads.execute(new Session(currentSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (currentSocket != null) {
                try {
                    currentSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void shutdown(){
        serverThreads.shutdown();
        try {
            serverSocket.close();
            System.out.println(serverThreads.awaitTermination((2L * threadsNumber) + 1,
                    TimeUnit.SECONDS) ?
                    "" : "Timeout elapsed before serverThreads thread pool termination.");
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
