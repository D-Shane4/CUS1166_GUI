import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// creates the VC Controller GUI window
public class VCControllerFrame extends JFrame {

    // Text area to show incoming requests from clients
    private JTextArea requestArea;

    // radio button for accept/reject decision 
    private JButton acceptButton;
    private JButton rejectButton;

    // Timer to refresh the screen every second
    private Timer refreshTimer;

    // Javonda: controller computation components- object that processes jobs
    private final VCController vc = new VCController("VC-001");

    // Text area to show computation results
    private JTextArea outputArea;

    // Button to compute job completion results
    private JButton computeCompletion;

    // ✅ FIX: helper method OUTSIDE other methods (this was your main bug)
    private String formatRequest(String pending) {
        return pending.replace(" | ", "\n");
    }

    // Constructor → runs when the GUI is created
    public VCControllerFrame() {
        setTitle("VC Controller Panel");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 182, 193));
        setLocation(800, 200);
        setLayout(new BorderLayout());

        // Title label at the top
        JLabel title = new JLabel("VC Controller Panel", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Area showing incoming request
        requestArea = new JTextArea();
        requestArea.setEditable(false); // User cannot type here
        requestArea.setLineWrap(true); // Wrap long text
        requestArea.setWrapStyleWord(true);
        requestArea.setText("No pending request.");

        // Area showing computation output
        outputArea = new JTextArea(8, 30);
        outputArea.setEditable(false);

        // Split screen / top = requests, bottom = output
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(requestArea),
            new JScrollPane(outputArea)
        );
        splitPane.setResizeWeight(0.6);
        add(splitPane, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        computeCompletion = new JButton("Compute Completion");
        acceptButton = new JButton("Accept");
        rejectButton = new JButton("Reject");

        buttonPanel.add(computeCompletion);
        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // When accept is clicked / call handleDecision
        acceptButton.addActionListener(e -> handleDecision("ACCEPTED"));

        // When reject is clicked / call handleDecision
        rejectButton.addActionListener(e -> handleDecision("REJECTED"));

        // When compute button is clicked / read file + compute jobs
        computeCompletion.addActionListener(e -> {
            File file = new File("vehicular_cloud_log.txt");
            handleComputation(file);
        });

        startRefresh();

        setVisible(true);
    }

    // Refreshes request area every second to show latest pending request
    private void startRefresh() {
        refreshTimer = new Timer(1000, e -> {

            // Get pending request from server
            String pending = VCServer.getPendingRequest();

            if (pending != null) {

                // FIXED: show formatted request in panel (clean look)
                requestArea.setText(formatRequest(pending));

                // If it's a new request - show popup
                if (!pending.equals(VCServer.getLastDisplayedRequest())) {

                    // FIXED: use formatted version so popup is NOT huge
                    JTextArea messageArea = new JTextArea(
                        "New Request Received:\n\n" + formatRequest(pending)
                    );

                    messageArea.setLineWrap(true);
                    messageArea.setWrapStyleWord(true);
                    messageArea.setEditable(false);
                    messageArea.setColumns(35);
                    messageArea.setRows(8);

                    JOptionPane.showMessageDialog(
                        this,
                        new JScrollPane(messageArea),
                        "Message",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                    VCServer.setLastDisplayedRequest(pending);
                }

            } else {
                requestArea.setText("No pending request.");
            }

        });

        refreshTimer.start();
    }

    // Handles accept/reject button click
    private void handleDecision(String decision) {

        // If no request → show message
        if (VCServer.getPendingRequest() == null) {
            JOptionPane.showMessageDialog(this, "No pending request.");
            return;
        }

        // Send decision to server
        VCServer.setDecision(decision);
        JOptionPane.showMessageDialog(this, "Request " + decision + " by VC Controller.");
    }

    // Reads file and calculates job completion
    private void handleComputation(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            List<Job> jobs = new ArrayList<>();
            String line;

            // Read file line by line
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                if (!line.contains("Client ID:")) continue;

                // Split line into parts
                String[] parts = line.split("\\|");
                if (parts.length < 4) {
                    throw new IllegalArgumentException("Invalid client line: " + line);
                }

                // Variables to store extracted data
                String clientID = "";
                LocalDateTime arrival = null;
                int duration = 0;
                LocalDateTime deadline = null;

                // Loop through each part of the line and extract data
                for (String part : parts) {
                    String trimmed = part.trim();

                    if (trimmed.startsWith("Client ID:")) {
                        clientID = trimmed.split(":", 2)[1].trim();
                    } else if (trimmed.startsWith("Timestamp:")) {
                        arrival = LocalDateTime.parse(trimmed.split(":", 2)[1].trim());
                    } else if (trimmed.startsWith("Approx job duration (min):")) {
                        duration = Integer.parseInt(trimmed.split(":", 2)[1].trim());
                    } else if (trimmed.startsWith("Job deadline:")) {
                        deadline = LocalDateTime.parse(trimmed.split(":", 2)[1].trim());
                    }
                }

                // Create Job object if data is valid
                if (!clientID.isEmpty() && arrival != null && deadline != null) {
                    Job j = new Job(clientID, clientID, arrival, null, duration, deadline);
                    jobs.add(j);
                }
            }

            // If no jobs found 
            if (jobs.isEmpty()) {
                outputArea.setText("No client jobs found in the selected file");
                return;
            }

            // Sort jobs by arrival time (FIFO scheduling)
            jobs.sort(Comparator.comparing(Job::getArrivalTime));

            // Temporary VC controller for processing
            VCController tempVC = new VCController("VC-001");

            // Assign jobs to controller
            for (Job j : jobs) {
                tempVC.assignJob(j);
            }

            // Start time of computation
            LocalDateTime start = LocalDateTime.now();

            // Get completion report from controller
            String report = tempVC.completion();

            // Display result
            outputArea.setText("==== Starting at " + start + " ====\n" + report);

        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}