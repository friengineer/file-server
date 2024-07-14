import java.io.*;
import java.net.*;

// Handles the client side of the application
public class Client {
	// The server the client is trying to connect to
	private Socket connection;
	// Data being read from the server
	private BufferedReader input;
	// Data being sent to the server
	private BufferedWriter output;
	// Data being read from a file
	private BufferedInputStream fileReader;
	// Data being written to a file
	private BufferedOutputStream fileWriter;

	// Constructor attempts to connect to the server and create input and output streams for the socket
	public Client() {
		try {
			setConnection(new Socket("localhost", 8888));
			setInput(new BufferedReader(new InputStreamReader(this.getConnection().getInputStream())));
			setOutput(new BufferedWriter(new OutputStreamWriter(this.getConnection().getOutputStream())));
			setFileReader(null);
			setFileWriter(null);
		}
		catch (UnknownHostException e) {
			System.err.println("Host does not exist.");
			System.exit(1);
		}
		catch (IOException e) {
			System.err.println("Host is unreachable.");
			System.exit(1);
		}
	}

	// Returns the value for the connection state variable
	private Socket getConnection() {
		return this.connection;
	}

	// Returns the value for the input state variable
	private BufferedReader getInput() {
		return this.input;
	}

	// Returns the value for the output state variable
	private BufferedWriter getOutput() {
		return this.output;
	}

	// Returns the value for the fileReader state variable
	private BufferedInputStream getFileReader() {
		return this.fileReader;
	}

	// Returns the value for the fileWriter state variable
	private BufferedOutputStream getFileWriter() {
		return this.fileWriter;
	}

	// Sets the value for the connection state variable
	private void setConnection(Socket port) {
		this.connection = port;
	}

	// Sets the value for the input state variable
	private void setInput(BufferedReader input) {
		this.input = input;
	}

	// Sets the value for the output state variable
	private void setOutput(BufferedWriter output) {
		this.output = output;
	}

	// Sets the value for the fileReader state variable
	private void setFileReader(BufferedInputStream fileReader) {
		this.fileReader = fileReader;
	}

	// Sets the value for the fileWriter state variable
	private void setFileWriter(BufferedOutputStream fileWriter) {
		this.fileWriter = fileWriter;
	}

	// Checks if the requested file exists on the client side
	private boolean fileExists(String filename) {
		File file = new File("clientFiles/" + filename);

		return file.isFile();
	}

	// Interprets the user input and performs the request
	public void request(String[] arguments) {
		try {
			String response;

			if(arguments[0].equals("list")) {
				this.getOutput().write("list");
				this.getOutput().newLine();
				this.getOutput().flush();

				while((response = this.getInput().readLine()) != null) {
					System.out.println(response);
				}
			}
			else if(arguments[0].equals("get")) {
				if(arguments.length < 2) {
					System.err.println("Please provide a filename");
				}
				else {
					this.getOutput().write("get");
					this.getOutput().newLine();
					this.getOutput().flush();
					this.getOutput().write(arguments[1]);
					this.getOutput().newLine();
					this.getOutput().flush();
					response = this.getInput().readLine();

					if(response.equals("file error")) {
						System.err.println("File does not exist");
					}
					else {
						System.out.println("Downloading " + arguments[1] + " from server");

						this.setFileWriter(new BufferedOutputStream(new FileOutputStream("clientFiles/" + arguments[1])));
						int data;

						while((data = this.getInput().read()) != -1) {
							this.getFileWriter().write(data);
						}

						this.getFileWriter().close();
						System.out.println("File successfully downloaded");
					}
				}
			}
			else if(arguments[0].equals("put")) {
				if(arguments.length < 2) {
					System.err.println("Please provide a filename");
				}
				else {
					if(this.fileExists(arguments[1])) {
						System.out.println("Uploading " + arguments[1] + " to server");
						this.getOutput().write("put");
						this.getOutput().newLine();
						this.getOutput().flush();
						this.getOutput().write(arguments[1]);
						this.getOutput().newLine();
						this.getOutput().flush();

						this.setFileReader(new BufferedInputStream(new FileInputStream("clientFiles/" + arguments[1])));
						int data;

						while((data = this.getFileReader().read()) != -1) {
							this.getOutput().write(data);
						}

						this.getOutput().flush();
						this.getFileReader().close();
						System.out.println("File successfully uploaded");
					}
					else {
						this.getOutput().write("file error");
						this.getOutput().newLine();
						this.getOutput().flush();
						System.err.println("File does not exist");
					}
				}
			}
			else {
				System.err.println("Inavalid argument given: " + arguments[0]);
			}

			this.getInput().close();
			this.getOutput().close();
			this.getConnection().close();
		}
		catch (IOException e) {
			System.err.println("I/O exception");
			System.exit(1);
		}
	}

	// Main method. Connects to the server and creates the request
	public static void main(String[] args) {
		Client client = new Client();

		if(args.length == 0) {
			System.err.println("Please enter a command line argument.");
			System.exit(1);
		}

		StringBuilder command = new StringBuilder();

		// Creates the request
		for(String argument: args) {
			if(command.length() != 0) {
				command.append(" ");
			}

			command.append(argument);
		}

		client.request(command.toString().split(" "));
	}
}
