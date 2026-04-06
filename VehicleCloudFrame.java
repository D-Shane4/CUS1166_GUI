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

        // ── NEW: Welcome Panel
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(255, 220, 230));

        JLabel welcomeTitle = new JLabel("Welcome to VCRTS", JLabel.CENTER);
        welcomeTitle.setFont(new Font("Arial", Font.BOLD, 20));

        JTextArea description = new JTextArea(
            "Vehicular Cloud Real-Time System (VCRTS)\n\n" +
            "This system allows vehicle owners to share computing resources\n" +
            "and clients to submit computational jobs.\n\n" +
            "The controller assigns jobs efficiently based on timing\n" +
            "and system availability."
        );
        description.setEditable(false);
        description.setBackground(new Color(255, 220, 230));

        startButton = new JButton("Start");

        welcomePanel.add(welcomeTitle, BorderLayout.NORTH);
        welcomePanel.add(description, BorderLayout.CENTER);
        welcomePanel.add(startButton, BorderLayout.SOUTH);

        
        
        // Home Panel 
        JPanel homePanel = new JPanel(null);
        homePanel.setBackground(new Color(255, 220, 230));

        JLabel questionLabel = new JLabel("What type of user are you?");
        questionLabel.setBounds(180, 60, 250, 100);
        homePanel.add(questionLabel);

        ownerButton = new JRadioButton("Owner");
        ownerButton.setBounds(100, 110, 100, 180);
        ownerButton.setBackground(new Color(255, 220, 230));

        clientButton = new JRadioButton("Client");
        clientButton.setBounds(220, 110, 100, 180);
        clientButton.setBackground(new Color(255, 220, 230));
        
        controllerButton = new JRadioButton("Controller");
        controllerButton.setBounds(320, 110, 100, 180);
        controllerButton.setBackground(new Color(255,220,230));

        ButtonGroup group = new ButtonGroup();
        group.add(ownerButton);
        group.add(clientButton);
        group.add(controllerButton);

        homePanel.add(ownerButton);
        homePanel.add(clientButton);
        homePanel.add(controllerButton);
        
        // Controller Panel
        JPanel controllerPanel = new JPanel(new GridLayout(5, 1, 0, 5));
        controllerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        controllerPanel.add(makeLabel("Controller Panel"));

        JPanel controllerButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));

        computeCompletion = new JButton("Compute Completion");
        controllerHomeButton = new JButton("Home");

        controllerButtons.add(computeCompletion);     
        controllerButtons.add(controllerHomeButton);

        controllerPanel.add(controllerButtons);

        // output
        outputArea = new JTextArea(8, 30);
        outputArea.setEditable(false);
        controllerPanel.add(new JScrollPane(outputArea));

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
        cardLayout.show(cards, "welcome");
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

        // FIX: open a JFileChooser so the user can select the log file,
        // then pass the chosen File to handleComputation.
        // Previously this was: handleComputation(//file) — a syntax error
        // with a dangling comment instead of an actual argument.
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
            String result = owner.sendVehicleInfo("localhost", 5000);
 
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
 
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vehicle Year must be a valid number.");
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
            String result = client.jobRequest("localhost", 5000);
            if( "ACCEPTED".equals(result)){
                JOptionPane.showMessageDialog(this, "Job Accepted by Server");
            } else if("REJECTED".equals(result)){
                JOptionPane.showMessageDialog(this, "Job Rejected by Server");
            }else{
                JOptionPane.showMessageDialog(this,"Server Communication Error");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Job duration must be a valid number.");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid deadline format. Please use: yyyy-MM-ddTHH:mm\nExample: 2025-04-01T14:30");
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

                // Expected format (from Client.fileText()):
                // Client ID: X | Timestamp: Y | Approx job duration (min): Z | Job deadline: W
                
                String[] parts = line.split("\\|");
                if (parts.length < 4) {
                    throw new IllegalArgumentException("Invalid client line: " + line);
                }
                 
                String clientID = parts[0].split(":",2)[1].trim();
                // parts[1] is "Timestamp: ..." — used as arrival time                String jobID = parts[1].trim().split(":", 2)[1].trim(); 
                LocalDateTime arrival  = LocalDateTime.parse(parts[1].split(":", 2)[1].trim());
                int duration           = Integer.parseInt(parts[2].split(":", 2)[1].trim());
                LocalDateTime deadline = LocalDateTime.parse(parts[3].split(":", 2)[1].trim());

                // Use clientID as jobID since the log has no separate job identifier
                Job j = new Job(clientID, clientID, arrival, null, duration, deadline);
                jobs.add(j);
            }
        

        if (jobs.isEmpty()) {
            outputArea.setText("No client jobs found in the selected file");
            return;
        }

        // Sort by arrival time for FCFS order
        jobs.sort(Comparator.comparing(Job::getArrivalTime));

        
        for (Job j : jobs){
            vc.assignJob(j);
        } 
        LocalDateTime start = LocalDateTime.now();
        String report = vc.completion();

        outputArea.setText("==== Starting at" + start + "====\n" + report);
           
            }catch (Exception e){
                outputArea.setText("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
       


    // Gianna: go back home
    private void goHome() {
        handleClear();
        cardLayout.show(cards, "Home");
    }
}
