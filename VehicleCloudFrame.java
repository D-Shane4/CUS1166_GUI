import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


public class VehicleCloudFrame extends JFrame {

    // Javonda: VC Controller request review components
    private JTextArea controllerRequestArea;
    private JButton acceptButton;
    private JButton rejectButton;
    private Timer controllerRefreshTimer;

    //vc conttroller
    private final VCController vc = new VCController("VC-001");
    private JTextArea outputArea;

    // ── Shanti: frame-level layout reference
    private CardLayout cardLayout;
    private JPanel cards;

    // ── Hawa: radio buttons on home screen
    private JRadioButton ownerButton;
    private JRadioButton clientButton;
    private JRadioButton controllerButton;
    
  //--Hawa: Start Button on Home page
    private JButton startButton;

    // ── Hawa: Owner panel fields (declared at class level so listeners can read them)
    private JTextField ownerIDField;
    private JTextField vehicleIDField;
    private JTextField vehicleModelField;
    private JTextField vehicleMakeField;
    private JTextField vehicleYearField;
    private JTextField arrivalTimeField;
    private JTextField departureTimeField;

    // ── Hawa: Client panel fields
    private JTextField clientIDField;
    private JTextField jobDurationField;
    private JTextField jobDeadlineField;
    
  //-- Hawa: Controller Panel Components
    private JButton controllerHomeButton;
    private JButton computeCompletion;


    // ── Hawa: submit buttons (declared at class level so listeners can reference them)
    private JButton ownerSubmitButton;
    private JButton clientSubmitButton;
    private JButton ownerHomeButton;
    private JButton clientHomeButton;
    
 // ── Hawa: Clear buttons
    private JButton ownerClearButton;
    private JButton clientClearButton;

    public VehicleCloudFrame() {
        setupFrame();       // Shanti
        createComponents(); // Hawa
        attachListeners();  // Gianna
        startControllerRefresh(); // Javonda
        setVisible(true);
    }

    // ── Shanti: frame setup
    // FIX: was re-declaring 'frame' as a local variable, shadowing the class field
    private void setupFrame() {
        setSize(550, 520);
        setTitle("Vehicular Cloud Real Time System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 182, 193));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    // ── Hawa: panels, buttons, text fields
    // FIX: all JTextFields now stored as named instance variables so listeners can read them
    // FIX: submit/home buttons declared at class level instead of locally
    private void createComponents() {

        // Title
        JLabel titleLabel = new JLabel("Vehicular Cloud Console", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

    //Welcome Panel
    JPanel welcomePanel = new JPanel(new BorderLayout());
    welcomePanel.setBackground(new Color(255, 220, 230));

     // Title at the top
     JLabel welcomeTitle = new JLabel("Welcome to VCRTS", JLabel.CENTER);
     welcomeTitle.setFont(new Font("Arial", Font.BOLD, 24));
     welcomeTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // spacing
     welcomePanel.add(welcomeTitle, BorderLayout.NORTH);

   // Description text in the center
     JLabel description = new JLabel(
    "<html><div style='text-align: center; width: 350px;'>"
    + "<b>Vehicular Cloud Real-Time System (VCRTS)</b><br><br>"
    + "This system allows vehicle owners to share computing resources "
    + "and clients to submit computational jobs.<br><br>"
    + "The controller assigns jobs efficiently based on timing "
    + "and system availability."
    + "</div></html>",
    JLabel.CENTER
);
description.setFont(new Font("Arial", Font.PLAIN, 16));

// Wrap description in a GridBagLayout panel to center it vertically and horizontally
JPanel centerWrapper = new JPanel(new GridBagLayout());
centerWrapper.setBackground(new Color(255, 220, 230));
centerWrapper.add(description);
welcomePanel.add(centerWrapper, BorderLayout.CENTER);

// Start button at the bottom, centered
startButton = new JButton("Start");
JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
buttonWrapper.setBackground(new Color(255, 220, 230));
buttonWrapper.add(startButton);
welcomePanel.add(buttonWrapper, BorderLayout.SOUTH);

        
        
        // Home Panel 
        JPanel homePanel = new JPanel(new GridBagLayout());
        homePanel.setBackground(new Color(255, 220, 230));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel questionLabel = new JLabel("What type of user are you?");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        homePanel.add(questionLabel, gbc);

        ownerButton = new JRadioButton("Owner");
        ownerButton.setBackground(new Color(255, 220, 230));

        clientButton = new JRadioButton("Client");
        clientButton.setBackground(new Color(255, 220, 230));

        controllerButton = new JRadioButton("Controller");
        controllerButton.setBackground(new Color(255, 220, 230));

        ButtonGroup group = new ButtonGroup();
        group.add(ownerButton);
        group.add(clientButton);
        group.add(controllerButton);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridx = 0;
        homePanel.add(ownerButton, gbc);

        gbc.gridx = 1;
        homePanel.add(clientButton, gbc);

        gbc.gridx = 2;
        homePanel.add(controllerButton, gbc);
        
        // Javonda: Controller Panel EDITED 
        JPanel controllerPanel = new JPanel(new BorderLayout(10, 10));
        controllerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel controllerTitle = makeLabel("VC Controller Panel");
        controllerPanel.add(controllerTitle, BorderLayout.NORTH);

        // Javonda: center area showing one pending request at a time EDITED
        controllerRequestArea = new JTextArea(10, 30);
        controllerRequestArea.setEditable(false);
        controllerRequestArea.setLineWrap(true);
        controllerRequestArea.setWrapStyleWord(true);
        controllerRequestArea.setText("No pending request.");
        controllerPanel.add(new JScrollPane(controllerRequestArea), BorderLayout.CENTER);

        // Javonda: controller action buttons EDITED
        JPanel controllerButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));

        computeCompletion = new JButton("Compute Completion");
        acceptButton = new JButton("Accept");
        rejectButton = new JButton("Reject");
        controllerHomeButton = new JButton("Home");

        controllerButtons.add(computeCompletion);
        controllerButtons.add(acceptButton);
        controllerButtons.add(rejectButton);
        controllerButtons.add(controllerHomeButton);

        controllerPanel.add(controllerButtons, BorderLayout.SOUTH);

        //  Javonda: optional output area for completion report EDITED
        outputArea = new JTextArea(8, 30);
        outputArea.setEditable(false);

        // Owner Panel 
        JPanel ownerPanel = new JPanel(new GridLayout(9, 1, 0, 5));
        ownerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ownerPanel.add(makeLabel("Owner Registration"));

        ownerPanel.add(makeRow("Owner ID:",        ownerIDField       = new JTextField(15)));
        ownerPanel.add(makeRow("Vehicle ID:",       vehicleIDField     = new JTextField(15)));
        ownerPanel.add(makeRow("Vehicle Model:",    vehicleModelField  = new JTextField(15)));
        ownerPanel.add(makeRow("Vehicle Make:",     vehicleMakeField   = new JTextField(15)));
        ownerPanel.add(makeRow("Vehicle Year:",     vehicleYearField   = new JTextField(15)));
        ownerPanel.add(makeRow("Arrival Time(HH:MM):",     arrivalTimeField   = new JTextField(15)));
        ownerPanel.add(makeRow("Departure Time(HH:MM):",   departureTimeField = new JTextField(15)));

        JPanel ownerButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ownerSubmitButton = new JButton("Submit");
        ownerHomeButton   = new JButton("Home");
        ownerClearButton = new JButton("Clear");
        ownerButtons.add(ownerSubmitButton);
        ownerButtons.add(ownerClearButton);
        ownerButtons.add(ownerHomeButton);
        ownerPanel.add(ownerButtons);

        // Client Panel
        JPanel clientPanel = new JPanel(new GridLayout(5, 1, 0, 5));
        clientPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        clientPanel.add(makeLabel("Client Registration"));

        clientPanel.add(makeRow("Client ID:",             clientIDField   = new JTextField(15)));
        clientPanel.add(makeRow("Job Duration (min):",    jobDurationField = new JTextField(15)));
        clientPanel.add(makeRow("Job Deadline\n(yyyy-MM-ddTHH:mm):", jobDeadlineField = new JTextField(15)));

        JPanel clientButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientSubmitButton = new JButton("Submit");
        clientHomeButton   = new JButton("Home");
        clientClearButton = new JButton ("Clear");
        
        clientButtons.add(clientSubmitButton);
        clientButtons.add(clientClearButton);
        clientButtons.add(clientHomeButton);
        clientPanel.add(clientButtons);

        cards.add(homePanel,   "Home");
        cards.add(ownerPanel,  "Owner");
        cards.add(welcomePanel, "Welcome");
        cards.add(controllerPanel, "Controller");
        cards.add(clientPanel, "Client");

        add(cards, BorderLayout.CENTER);
        //make sure the welcome page shows first
        cardLayout.show(cards, "Welcome"); // FIX: was "welcome" (lowercase), CardLayout is case-sensitive
    }

    // Hawa: helper to build a labeled row
    private JPanel makeRow(String labelText, JTextField field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(180, 25));
        row.add(label);
        row.add(field);
        return row;
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }

    // Gianna: action listeners
    // FIX: cardLayout and all buttons now properly in scope as instance variables
    private void attachListeners() {
        ownerButton.addActionListener(e -> cardLayout.show(cards, "Owner"));
        clientButton.addActionListener(e -> cardLayout.show(cards, "Client"));
        controllerButton.addActionListener(e -> cardLayout.show(cards, "Controller"));
        startButton.addActionListener(e -> cardLayout.show(cards, "Home"));
        //home buttons
        controllerHomeButton.addActionListener(e-> goHome());
        ownerHomeButton.addActionListener(e -> goHome());
        clientHomeButton.addActionListener(e -> goHome());
       //submit buttons
        ownerSubmitButton.addActionListener(e -> handleOwnerSubmit());
        clientSubmitButton.addActionListener(e -> handleClientSubmit());
        //clear buttons
        ownerClearButton.addActionListener(e -> handleClear());
        ownerClearButton.addActionListener(e -> handleClear());
        clientClearButton.addActionListener(e -> handleClear());

        // Javonda: controller decision buttons
        acceptButton.addActionListener(e -> handleControllerDecision("ACCEPTED"));
        rejectButton.addActionListener(e -> handleControllerDecision("REJECTED"));

        computeCompletion.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select the vehicular cloud log file");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                handleComputation(chooser.getSelectedFile());
            }
        });
    }

    // Gianna: clear all fields
    private void handleClear() {
        ownerIDField.setText("");
        vehicleIDField.setText("");
        vehicleModelField.setText("");
        vehicleMakeField.setText("");
        vehicleYearField.setText("");
        arrivalTimeField.setText("");
        departureTimeField.setText("");
        clientIDField.setText("");
        jobDurationField.setText("");
        jobDeadlineField.setText("");
    }

    // Gianna: owner submit handler (EDITED & PATCHED BY MEHMET)
     private void handleOwnerSubmit() {
        try {
            String ownerID       = ownerIDField.getText().trim();
            String vehicleID     = vehicleIDField.getText().trim();
            String vehicleModel  = vehicleModelField.getText().trim();
            String vehicleMake   = vehicleMakeField.getText().trim();
            int    vehicleYear   = Integer.parseInt(vehicleYearField.getText().trim());
            String arrivalTime   = arrivalTimeField.getText().trim();
            String departureTime = departureTimeField.getText().trim();
 
            if (ownerID.isEmpty() || vehicleID.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Owner ID and Vehicle ID are required.");
                return;
            }
 
            Owner owner = new Owner(ownerID, vehicleID, vehicleModel, vehicleMake,
                                    vehicleYear, arrivalTime, departureTime);
 
            // Send to VC Controller server and receive decision  ← SOCKET PATH
            // (mirrors handleClientSubmit — server now owns the file write)
           // Javonda (EDITED): run owner request in background so GUI does not freeze
new Thread(() -> {
    String result = owner.sendVehicleInfo("localhost", 5050);

    SwingUtilities.invokeLater(() -> {
        if ("ACCEPTED".equals(result)) {
            JOptionPane.showMessageDialog(this,
                "Vehicle Registered Successfully!\nOwner ID: " + ownerID +
                "\nData saved to vehicular_cloud_log.txt");
        } else if ("REJECTED".equals(result)) {
            JOptionPane.showMessageDialog(this,
                "Registration Rejected by VC Controller.\nData was NOT saved.");
        } else {
            JOptionPane.showMessageDialog(this, "Server Communication Error.");
        }
    });
}).start();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vehicle year must be a valid number.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check all fields.");
        }
     }
     
    // Gianna: client submit handler
    private void handleClientSubmit() {
        try {
            String clientID          = clientIDField.getText().trim();
            int    jobDurationMinutes = Integer.parseInt(jobDurationField.getText().trim());
            LocalDateTime jobDeadline = LocalDateTime.parse(
                jobDeadlineField.getText().trim(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
            );

            if (clientID.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Client ID is required.");
                return;
            }

            Client client = new Client(clientID, jobDurationMinutes, jobDeadline);

            // milestone 5 - redirected data to socket in Client class by calling jobrequest, returns message based on response from server 
          // Javonda (EDITED): run client request in background so GUI does not freeze
new Thread(() -> {
    String result = client.jobRequest("localhost", 5050);

    SwingUtilities.invokeLater(() -> {
        if ("ACCEPTED".equals(result)) {
            JOptionPane.showMessageDialog(this, "Job Accepted by Server");
        } else if ("REJECTED".equals(result)) {
            JOptionPane.showMessageDialog(this, "Job Rejected by Server");
        } else {
            JOptionPane.showMessageDialog(this, "Server Communication Error");
        }
    });
}).start();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Job duration must be a valid number.");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Deadline must be in format: yyyy-MM-ddTHH:mm");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check all fields.");
        }
    }

    private void handleComputation(File file){
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            List<Job> jobs= new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                if (!line.startsWith("Client ID:")) continue;

                String[] parts = line.split("\\|");
                if (parts.length < 4) {
                    throw new IllegalArgumentException("Invalid client line: " + line);
                }
                 
                String clientID = parts[0].split(":",2)[1].trim();
                LocalDateTime arrival  = LocalDateTime.parse(parts[1].split(":", 2)[1].trim());
                int duration           = Integer.parseInt(parts[2].split(":", 2)[1].trim());
                LocalDateTime deadline = LocalDateTime.parse(parts[3].split(":", 2)[1].trim());

                Job j = new Job(clientID, clientID, arrival, null, duration, deadline);
                jobs.add(j);
            }

            if (jobs.isEmpty()) {
                outputArea.setText("No client jobs found in the selected file");
                return;
            }

            jobs.sort(Comparator.comparing(Job::getArrivalTime));

            for (Job j : jobs){
                vc.assignJob(j);
            } 
            LocalDateTime start = LocalDateTime.now();
            String report = vc.completion();

            outputArea.setText("==== Starting at" + start + "====\n" + report);
           
        } catch (Exception e){
            outputArea.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Gianna: go back home
    private void goHome() {
        handleClear();
        cardLayout.show(cards, "Home");
    }
    // Javonda: refresh controller screen so it shows one pending request at a time
private void startControllerRefresh() {
    controllerRefreshTimer = new Timer(1000, e -> {
        String pending = VCServer.getPendingRequest();

        if (pending != null) {
            controllerRequestArea.setText(pending);

            // show popup only once per request
            if (!pending.equals(VCServer.getLastDisplayedRequest())) {
                JOptionPane.showMessageDialog(this,
                    "New Request Received:\n\n" + pending);
                VCServer.setLastDisplayedRequest(pending);
            }
        } else {
            controllerRequestArea.setText("No pending request.");
        }
    });

    controllerRefreshTimer.start();
}

// Javonda: accept or reject the current request
    private void handleControllerDecision(String decision) {
        String pending = VCServer.getPendingRequest();

        if (pending == null) {
            JOptionPane.showMessageDialog(this, "There is no pending request to review.");
            return;
        }

        VCServer.setDecision(decision);

        JOptionPane.showMessageDialog(this,
            "Request " + decision + " by VC Controller.");

        controllerRequestArea.setText("No pending request.");
    }
}
