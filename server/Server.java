import java.io.*;
import java.net.*;
import java.util.concurrent.*;

// Handles the server side of the application
public class Server {
	// The port the server is trying to host itself on
	private ServerSocket port = null;
	// Implements an executor server
	private ExecutorService service = null;

  // Constructor attempts to bind to a port and initialise the executor server
	public Server() {
		try {
			setPort(new ServerSocket(8888));
			setService();
		}
		catch (IOException e) {
			System.err.println("Could not listen on port 8888.");
			System.exit(1);
		}
	}

  // Returns the value for the port state variable
	public ServerSocket getPort() {
		return this.port;
	}

	// Returns the value for the service state variable
	public ExecutorService getService() {
		return this.service;
	}

	// Sets the value for the port state variable
	private void setPort(ServerSocket port) {
		this.port = port;
	}

	// Creates a fixed thread pool with 10 connections
	private void setService() {
		this.service = Executors.newFixedThreadPool(10);
	}

	// Executes the server
	public void run() throws IOException {
		while(true) {
			this.getService().submit(new ClientHandler(this.getPort().accept()));
		}
	}

	// Main method. Binds to a port and executes the server
	public static void main(String[] args) {
		Server server = new Server();

		try {
			server.run();
		}
		catch (IOException e) {
			System.err.println("An error was encountered running the server.");
			System.exit(1);
		}
	}
}
