import java.net.*;
import java.io.*;
import java.util.*;

// Handles one client's connection to the server as a thread
public class ClientHandler extends Thread {
	// The port the client is connected to on the server
	private Socket socket;
	// Data being read from the client
	private BufferedReader input;
	// Data being sent to the client
	private BufferedWriter output;
	// Data being written to the log file
	private BufferedWriter logWriter;
	// Data being read from a file
	private BufferedInputStream fileReader;
	// Data being written to a file
	private BufferedOutputStream fileWriter;

	// Constructor attempts to connect to the client, create input and output streams for the socket
	// and attempts to create a stream to write to the log file
	public ClientHandler(Socket socket) {
		super("ClientHandler");

		try {
			setSocket(socket);
			setInput(new BufferedReader(new InputStreamReader(this.getSocket().getInputStream())));
			setOutput(new BufferedWriter(new OutputStreamWriter(this.getSocket().getOutputStream())));
			setLogWriter(new BufferedWriter(new FileWriter("log.txt", true)));
			setFileReader(null);
			setFileWriter(null);
		}
		catch (IOException e) {
			System.err.println("Error with I/O");
			System.exit(1);
		}
	}

	// Returns the value for the socket state variable
	private Socket getSocket() {
		return this.socket;
	}

	// Returns the value for the input state variable
	private BufferedReader getInput() {
		return this.input;
	}

	// Returns the value for the output state variable
	private BufferedWriter getOutput() {
		return this.output;
	}

	// Returns the value for the socket state variable
	private BufferedWriter getLogWriter() {
		return this.logWriter;
	}

	// Returns the value for the fileReader state variable
	private BufferedInputStream getFileReader() {
		return this.fileReader;
	}

	// Returns the value for the fileWriter state variable
	private BufferedOutputStream getFileWriter() {
		return this.fileWriter;
	}

	// Sets the value for the socket state variable
	private void setSocket(Socket socket) {
		this.socket = socket;
	}

	// Sets the value for the input state variable
	private void setInput(BufferedReader input) {
		this.input = input;
	}

	// Sets the value for the output state variable
	private void setOutput(BufferedWriter output) {
		this.output = output;
	}

	// Sets the value for the logWriter state variable
	private void setLogWriter(BufferedWriter logWriter) {
		this.logWriter = logWriter;
	}

	// Sets the value for the fileReader state variable
	private void setFileReader(BufferedInputStream fileReader) {
		this.fileReader = fileReader;
	}

	// Sets the value for the fileWriter state variable
	private void setFileWriter(BufferedOutputStream fileWriter) {
		this.fileWriter = fileWriter;
	}

	// Logs the client's request to the log file
	private void log(String request) throws IOException {
		this.getLogWriter().write(new Date().toString());
		this.getLogWriter().write(":");
		this.getLogWriter().write(this.getSocket().getInetAddress().toString());
		this.getLogWriter().write(":");
		this.getLogWriter().write(request);
		this.getLogWriter().newLine();
		this.getLogWriter().close();
	}

	// Checks if the requested file exists on the server side
	public boolean fileExists(String filename) {
		File file = new File("serverFiles/" + filename);

		return file.isFile();
	}

	// Returns the filenames of the files on the server side
	public List<String> listFiles() {
		File directory = new File("serverFiles");
		File[] files = directory.listFiles();
		List<String> filenames = new LinkedList<>();
		String filename, filePath;

		for(File file: files) {
			filename = file.toString();
			filePath = filename.substring(filename.indexOf("/") + 1);
			filenames.add(filePath);
		}

		return filenames;
	}

	// Performs the requested action
	public void run() {
		try {
			String command = this.getInput().readLine();
			List<String> files;

			if(command.equals("list")) {
				this.log(command);

				files = this.listFiles();
				this.getOutput().write("The following files are in the serverFiles directory:\n");
				this.getOutput().newLine();
				this.getOutput().flush();

				for(String file: files) {
					this.getOutput().write(file);
					this.getOutput().newLine();
					this.getOutput().flush();
				}
			}
			else if(command.equals("get")) {
				String filename = this.getInput().readLine();
				this.log(command + " " + filename);
				boolean answer;

				if(answer = this.fileExists(filename)) {
					this.getOutput().write(filename);
					this.getOutput().newLine();
					this.getOutput().flush();

					this.setFileReader(new BufferedInputStream(new FileInputStream("serverFiles/" + filename)));
					int data;

					while((data = this.getFileReader().read()) != -1) {
						this.getOutput().write(data);
					}

					this.getOutput().flush();
					this.getFileReader().close();
				}
				else {
					this.getOutput().write("file error");
					this.getOutput().newLine();
					this.getOutput().flush();
				}
			}
			else if(command.equals("put")) {
				String filename = this.getInput().readLine();
				this.log(command + " " + filename);

				if(!filename.equals("file error")) {
					this.setFileWriter(new BufferedOutputStream(new FileOutputStream("serverFiles/" + filename)));
					int data;

					while((data = this.getInput().read()) != -1) {
						this.getFileWriter().write(data);
					}

					this.getFileWriter().close();
				}
			}

			this.getOutput().close();
			this.getInput().close();
			this.getSocket().close();
		}
		catch (IOException e) {
			System.err.println("Error with I/O");
			System.exit(1);
		}
	}
}
