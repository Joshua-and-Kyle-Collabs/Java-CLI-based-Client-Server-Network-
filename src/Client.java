import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.net.InetAddress;

public class Client {

    public static void main(String[] args) {

        Socket socket = null;
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        Scanner scanner = new Scanner(System.in);

        boolean isValidInput = false;
        while (!isValidInput) {
            try {
                System.out.print("Enter the IP address of the server: ");
                String serverIp = scanner.nextLine();

                System.out.print("Enter the port number of the server: ");
                int serverPort = scanner.nextInt();

                String deviceIp = InetAddress.getLocalHost().getHostAddress();

                socket = new Socket(serverIp, serverPort);

                inputStreamReader = new InputStreamReader(socket.getInputStream());
                outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

                bufferedReader = new BufferedReader(inputStreamReader);
                bufferedWriter = new BufferedWriter(outputStreamWriter);

                isValidInput = true;
            } catch (IOException e) {
                System.out.println("Invalid input. Please enter the IP address and port number again.");
                scanner.nextLine();
            }
        }

        try {
            System.out.print("Enter your ID: ");
            String clientId = scanner.next();

            // Send the ID to the server
            bufferedWriter.write(clientId);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Thread responseThread = new Thread(new ResponseHandler(bufferedReader));
            responseThread.start();

            while (true) {

                String msgToSend = scanner.nextLine();

                bufferedWriter.write(msgToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                if (msgToSend.equalsIgnoreCase("BYE")) {
                    // Notify the server that the client is leaving
                    bufferedWriter.write("LEAVE");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null)
                    socket.close();
                if (inputStreamReader != null)
                    inputStreamReader.close();
                if (outputStreamWriter != null)
                    outputStreamWriter.close();
                if (bufferedReader != null)
                    bufferedReader.close();
                if (bufferedWriter != null)
                    bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ResponseHandler implements Runnable {
        private BufferedReader bufferedReader;

        public ResponseHandler(BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String msgFromServer = bufferedReader.readLine();
                    if (msgFromServer == null) {
                        break;
                    }
                    if (msgFromServer.equals("LEAVE")) {
                        // Notify the user that a client has left
                        System.out.println("A client has left the chat.");
                    } else {
                        System.out.println(msgFromServer);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}