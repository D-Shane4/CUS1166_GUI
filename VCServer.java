import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

public class VCServer {
    private static DatabaseConnection db = new DatabaseConnection();

    private static final int PORT = 5050;

    // Javonda: shared controller state for pending request / controller decision
    private static String pendingRequest = null;
    private static String decision = null;
    private static String lastDisplayedRequest = null;


    public static synchronized void setPendingRequest(String request) {
        pendingRequest = request;
        decision = null;
    }

    public static synchronized String getPendingRequest() {
        return pendingRequest;
    }

    public static synchronized void setDecision(String newDecision) {
        decision = newDecision;
    }

    public static synchronized String getDecision() {
        return decision;
    }

    public static synchronized void clearRequest() {
        pendingRequest = null;
        decision = null;
    }

    public static synchronized String getLastDisplayedRequest() {
        return lastDisplayedRequest;
    }

    public static synchronized void setLastDisplayedRequest(String request) {
        lastDisplayedRequest = request;
    }

    public static void main(String[] args) {
        System.out.println("VC Controller Server starting on port " + PORT + " ...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening. Waiting for connections...");

            // Keep accepting new connections indefinitely
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection from: " + clientSocket.getInetAddress());

                // Spin up a new thread for each connection (Note II - multi-threaded)
                Thread handler = new Thread(new RequestHandler(clientSocket));
                handler.setDaemon(true); // dies when main thread exits
                handler.start();
            }

        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------------------
    // Inner class: handles one client/owner connection
    // -----------------------------------------------------------------------
    static class RequestHandler implements Runnable {

        private final Socket socket;

        RequestHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
        try (DataInputStream  dis = new DataInputStream(socket.getInputStream());
         DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

        // 1. Read the incoming data string sent by Owner or Client
        //gianna edit - parse data to initalize later
        String data = dis.readUTF();
        String [] parts = data.split("|");
        System.out.println("\n--- Incoming Request ---");
        System.out.println(data);

        // 2. Send acknowledgment immediately (assignment: "acknowledgment should
        //    be sent upon receiving any request")
        dos.writeUTF("Request received by VC Controller");
        dos.flush();

        // 3. Decide: ACCEPT or REJECT
        //    Policy: accept if the data is non-empty and well-formed.
        //    You can replace this logic with any business rule you need.

        // Javonda (EDITED): send the request to the VC Controller GUI
        // so the controller can choose Accept or Reject instead of
        // the server deciding automatically
        VCServer.setPendingRequest(data);

        // Javonda (EDITED): wait until the controller makes a decision
        while (VCServer.getDecision() == null) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        String decision = VCServer.getDecision();
// gianna initalize variable from parsed data to add to database 
          
     String requestId = parts[0];
     String userId = parts[1];
     String userType = parts[2];

     String vehicleID = parts[3];
     String vehicleMake = parts[4];
     String vehicleModel = parts[5];

     int vehicleYear = Integer.parseInt(parts[6]);

     String arrivalTime = parts[7];
     String departureTime = parts[8];

     Integer jobDuration = Integer.parseInt(parts[9]);

     LocalDateTime jobDeadline = LocalDateTime.parse(parts[10]);

     LocalDateTime timestamp = LocalDateTime.now();
       // 4. If accepted, persist to file (server owns the file — clients no longer
        //    call FileManager directly)
         
     if ("ACCEPTED".equals(decision)) {
         FileManager.saveRaw(data);   // see FileManager addition below
         // gianna - if accepted send to database & file for completion time
         db.insertUser(userId,userType);
         db.insertRequest(
         requestId,
         userId,
         timestamp,
         vehicleID,
         vehicleMake,
         vehicleModel,
         vehicleYear,
         arrivalTime,
         departureTime,
         jobDuration,
         jobDeadline);

         System.out.println("Decision: ACCEPTED — data saved to log & database.");
         // SHANTI - Client inserts
         db.clientInsert(
         requestId,
         userId,
         timestamp,
         jobDuration,   
         jobDeadline);

      System.out.println("Decision: ACCEPTED — data saved");
     
     } else {
         System.out.println("Decision: REJECTED — data NOT saved.");
     }

        // 5. Send the decision back to the caller
        dos.writeUTF(decision);
        dos.flush();

        // Javonda (EDITED): clear request after the controller finishes
        VCServer.clearRequest();

        } catch (Exception e) {
        System.err.println("Handler error: " + e.getMessage());
        e.printStackTrace();
     } finally {
        try { socket.close(); } catch (Exception ignored) {}
    }
}
        private String evaluate(String data) {
        // Javonda (EDITED): kept this method so existing code structure stays intact.
        // The VC Controller GUI now makes the final Accept/Reject decision
            if (data == null || data.isBlank()) {
                return "REJECTED";
            }
            // Owner entries start with "Owner ID:", client entries with "Request ID:"
            if (data.contains("Owner ID:") || data.contains("Request ID:")) {
                return "ACCEPTED";
            }
            return "REJECTED";
        }
    }
}
