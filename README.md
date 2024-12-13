# Client-Server Network Application

## Overview

This project is a simple client-server chat application built using Java that enables multiple clients to connect to a server, send and receive messages, and perform direct messaging (DM). The server manages multiple client connections, broadcasting messages to all clients and allowing clients to send private messages.

## Components

### 1. **Server.java**
The server listens for incoming client connections, assigns unique client IDs, and facilitates communication between connected clients. The server handles broadcasting messages to all clients, as well as private/direct messaging between clients.

#### Key Features:
- Starts a server socket on port `2468`.
- Accepts multiple client connections.
- Each client is assigned a unique client ID.
- Broadcasts messages from one client to all other connected clients.
- Handles private messages using the `/dm` command (e.g., `/dm 1 Hello` sends a message to client 1).
- Notifies all clients when a new client connects or disconnects.

### 2. **Client.java**
The client connects to the server, sends messages, and listens for incoming messages from the server. It also allows users to send direct messages to specific clients and notifies users when a client leaves the chat.

#### Key Features:
- Connects to the server using a specified IP address and port.
- Sends a unique client ID to the server upon connection.
- Allows users to type and send chat messages to the server.
- Listens for incoming messages from the server, including new client notifications and chat messages.
- Sends a `BYE` message to the server to disconnect from the chat.

### 3. **ResponseHandler.java** (Embedded in Client)
Handles receiving messages from the server on a separate thread so that the client can listen for messages while still sending messages.

## Requirements

- **Java version:** JDK 8 or higher
- **Networking:** A TCP/IP network connection
- **Port:** Default server port is `2468` (configurable)

## Usage

### Running the Server
1. Open a terminal/command prompt and navigate to the directory containing `Server.java`.
2. Compile the Java code using the command:
   ```
   javac Server.java
   ```
3. Run the server:
   ```
   java Server
   ```
   The server will start listening on port `2468`.

### Running the Client
1. Open a terminal/command prompt and navigate to the directory containing `Client.java`.
2. Compile the Java code using the command:
   ```
   javac Client.java
   ```
3. Run the client:
   ```
   java Client
   ```
4. Enter the server's IP address and port number when prompted to connect to the server.
5. Enter a unique client ID to join the chat.
6. Start chatting! You can send regular messages or use the `/dm <client ID> <message>` format to send a direct message to another client.

### Sending Messages
- **Broadcast message:** Simply type the message and press Enter. The message will be broadcast to all connected clients.
- **Direct message (DM):** To send a private message, use the `/dm <client ID> <message>` command, replacing `<client ID>` with the recipient's client ID.

### Leaving the Chat
- To leave the chat, type `BYE` and press Enter. This will disconnect the client from the server.

## Sample Commands

- **Send a broadcast message:** 
  ```
  Hello everyone!
  ```
  All connected clients will receive this message.

- **Send a direct message:** 
  ```
  /dm 1 Hey, how are you?
  ```
  This will send a private message to the client with ID 1.

- **Exit the chat:** 
  ```
  BYE
  ```

## Notes

- The server can handle multiple clients simultaneously, and each client can communicate with all others.
- Clients will be notified when other clients connect or disconnect.
- The server handles basic error checking, such as invalid direct message commands and non-existent client IDs for direct messaging.

## License

This project is open source. Feel free to modify and use it as you see fit. However, please provide appropriate credit if you use any part of the code in your own projects.
