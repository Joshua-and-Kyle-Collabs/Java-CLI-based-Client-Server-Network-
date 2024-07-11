import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static int clientIdCounter = 0;
    private static List<ClientHandler> clientHandlers = new ArrayList<>();

    public static void main(String[] args) {

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(2468);
            System.out.println("Server started on port " + serverSocket.getLocalPort() + ".");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected.");

                int clientId = ++clientIdCounter;
                ClientHandler clientHandler = new ClientHandler(clientId, socket);
                clientHandlers.add(clientHandler);

                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private int clientId;
        private Socket socket;
        private InputStreamReader inputStreamReader;
        private OutputStreamWriter outputStreamWriter;
        private BufferedReader bufferedReader;
        private BufferedWriter bufferedWriter;

        public ClientHandler(int clientId, Socket socket) {
            this.clientId = clientId;
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                inputStreamReader = new InputStreamReader(socket.getInputStream());
                outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

                bufferedReader = new BufferedReader(inputStreamReader);
                bufferedWriter = new BufferedWriter(outputStreamWriter);

                // Read the client ID
                String clientIdStr = bufferedReader.readLine();
                System.out.println("Client " + clientIdStr + " has entered the server.");

                // Read the client's device IP address
                String deviceIP = bufferedReader.readLine();
                System.out.println("Device IP of client " + clientIdStr + " is " + deviceIP);

                // Send a list of connected clients to the new client
                StringBuilder connectedClients = new StringBuilder("Connected clients:");
                for (ClientHandler clientHandler : clientHandlers) {
                    if (clientHandler.clientId != this.clientId) {
                        connectedClients.append("\n- Client ").append(clientHandler.clientId).append(" (Device IP: ").append(clientHandler.socket.getInetAddress().getHostAddress()).append(")");
                    }
                }
                bufferedWriter.write(connectedClients.toString());
                bufferedWriter.newLine();
                bufferedWriter.flush();

                // Notify other clients that a new client has connected
                for (ClientHandler clientHandler : clientHandlers) {
                    if (clientHandler.clientId != this.clientId) {
                        clientHandler.bufferedWriter.write("Client " + clientIdStr + " has entered the server. Their IP address is " + clientHandler.socket.getInetAddress().getHostAddress());
                        clientHandler.bufferedWriter.newLine();
                        clientHandler.bufferedWriter.flush();
                    }
                }
                while (true) {
                    String msgFromClient = bufferedReader.readLine();
                    if (msgFromClient == null) {
                        break;
                    }

                    System.out.println("Client " + clientIdStr + ": " + msgFromClient);

                    if (msgFromClient.startsWith("/dm ")) {
                        String[] tokens = msgFromClient.split(" ", 3);
                        if (tokens.length < 3) {
                            bufferedWriter.write("Invalid /dm command. Usage: /dm <recipient client ID> <message>");
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        } else {
                            try {
                                int recipientId = Integer.parseInt(tokens[1]);
                                String message = tokens[2];
                                boolean recipientFound = false;

                                for (ClientHandler clientHandler : clientHandlers) {
                                    if (clientHandler.clientId == recipientId) {
                                        clientHandler.bufferedWriter.write("(DM) Client " + clientIdStr + ": " + message);
                                        clientHandler.bufferedWriter.newLine();
                                        clientHandler.bufferedWriter.flush();

                                        recipientFound = true;
                                        break;
                                    }
                                }

                                if (!recipientFound) {
                                    bufferedWriter.write("Recipient client not found.");
                                    bufferedWriter.newLine();
                                    bufferedWriter.flush();
                                }
                            } catch (NumberFormatException e) {
                                bufferedWriter.write("Invalid client ID in /dm command.");
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                            }
                        }
                    } else {
                        for (ClientHandler clientHandler : clientHandlers) {
                            if (clientHandler.clientId != this.clientId) {
                                clientHandler.bufferedWriter.write("Client " + clientIdStr + ": " + msgFromClient);
                                clientHandler.bufferedWriter.newLine();
                                clientHandler.bufferedWriter.flush();
                            }
                        }
                    }

                    if (msgFromClient.equalsIgnoreCase("Bye")) {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    clientHandlers.remove(this);
                    inputStreamReader.close();
                    outputStreamWriter.close();
                    bufferedReader.close();
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    }