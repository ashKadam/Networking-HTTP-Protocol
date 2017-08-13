# Networking-HTTP-Protocol
Implementation of HTTP Client and Server

Assignment requirements:
In this programming assignment, you will implement an HTTP client and server that run a simplified version of HTTP/1.1. Specifically, you will implement two HTTP commands: GET and PUT. This project can be completed only in C++ and Java.

HTTP Client
Your client should take the following command line arguments (in order): server name, port on which to contact the server, HTTP command (GET or PUT), and the path of the requested object on the server.
In other words, assuming that your executable is named “myclient”, you should be able to run your program from the command line as follows:


    myclient hostname port command filename


In response to a GET command, the client must:
1. connect to the server via a TCP connection
2. submit a valid HTTP/1.1 GET request to the server
3. read the server’s response and display it


In response to a PUT command, the client must:
1. Connect to the server via a TCP connection
2. submit a valid HTTP/1.1 PUT request to the server
3. send the file to the server
4. wait for the server’s reply
5. read the server’s response and display it


HTTP Server
Your server should take a command line argument that specifies the port number that the server will use to listen for incoming connection requests. In other words, assuming that your executable is named “myserver”, you should be able to run your server from the command line as follows:

    myserver port


Your server must:
1. Create a socket with the specified port number
2. Listen for incoming connections from clients
3. When a client connection is accepted, read the HTTP request
4. Construct a valid HTTP response:
  a. When the server receives a GET request, it should either construct a “200 OK” message followed by the requested object or a “404 Not Found” message.
  b. When the server receives a PUT request, it should save the file locally.
  c. If the received file is successfully saved, the server should construct a “200 OK File Created” response.
5. Send the HTTP response over the TCP connection with the client
6. Close the client connection
7. Continue to “loop” to listen for incoming connection


Your multithreaded server will have an infinite loop to listen for connections. To shut down your server, you will have to interrupt it with a termination signal. Upon receiving the termination signal, your server must shut down gracefully, closing all sockets before exiting. You can design your program that best matches your programming styles.
