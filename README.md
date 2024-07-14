# File Server
Server and client programs to host a file server for clients to download and upload files from and to. Uses an exectuor server with a fixed thread pool.

Compile the program by running the following commands from the root directory.
```
javac ./client/Client.java \
./server/*.java
```
Execute the server by navigating to the `server` directory and running `java Server`.

Execute the client by navigating to the `client` directory in a separate shell and running `java Client <command>` where command is a command from the list below.

- list: list all files on the server
- get \<filename\>: download file with specified filename from server
- put \<filename\>: upload file with specified filename on client
