import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class VehicleCloudFrame extends JFrame {

    // ── Shanti: frame-level layout reference
    private CardLayout cardLayout;
    private JPanel cards;

    // ── Hawa: radio buttons on home screen
    private JRadioButton ownerButton;
    private JRadioButton clientButton;

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

    // ── Hawa: submit buttons (declared at class level so listeners can reference them)
    private JButton ownerSubmitButton;
    private JButton clientSubmitButton;
    private JButton ownerHomeButton;
    private JButton clientHomeButton;

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

        // Home Panel 
        JPanel homePanel = new JPanel(null);
        homePanel.setBackground(new Color(255, 220, 230));

        JLabel questionLabel = new JLabel("What type of user are you?");
        questionLabel.setBounds(150, 60, 250, 30);
        homePanel.add(questionLabel);

        ownerButton = new JRadioButton("Owner");
        ownerButton.setBounds(150, 110, 100, 30);
        ownerButton.setBackground(new Color(255, 220, 230));

        clientButton = new JRadioButton("Client");
        clientButton.setBounds(270, 110, 100, 30);
        clientButton.setBackground(new Color(255, 220, 230));

        ButtonGroup group = new ButtonGroup();
        group.add(ownerButton);
        group.add(clientButton);

        homePanel.add(ownerButton);
        homePanel.add(clientButton);

        // Owner Panel 
        JPanel ownerPanel = new JPanel(new GridLayout(9, 1, 0, 5));
        ownerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ownerPanel.add(makeLabel("Owner Registration"));

        ownerPanel.add(makeRow("Owner ID:",        ownerIDField       = new JTextField(15)));
        ownerPanel.add(makeRow("Vehicle ID:",       vehicleIDField     = new JTextField(15)));
        ownerPanel.add(makeRow("Vehicle Model:",    vehicleModelField  = new JTextField(15)));
        ownerPanel.add(makeRow("Vehicle Make:",     vehicleMakeField   = new JTextField(15)));
        ownerPanel.add(makeRow("Vehicle Year:",     vehicleYearField   = new JTextField(15)));
        ownerPanel.add(makeRow("Arrival Time:",     arrivalTimeField   = new JTextField(15)));
        ownerPanel.add(makeRow("Departure Time:",   departureTimeField = new JTextField(15)));

        JPanel ownerButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ownerSubmitButton = new JButton("Submit");
        ownerHomeButton   = new JButton("Home");
        ownerButtons.add(ownerSubmitButton);
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
        clientButtons.add(clientSubmitButton);
        clientButtons.add(clientHomeButton);
        clientPanel.add(clientButtons);

        cards.add(homePanel,   "Home");
        cards.add(ownerPanel,  "Owner");
        cards.add(clientPanel, "Client");

        add(cards, BorderLayout.CENTER);
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
        controllerClearButton.addActionListener(e -> handleClear());

        computeCompletion.addActionListener(e -> handleComputation());
    }

    // Gianna: clear all fields
    private void handleClear() {
        ownerIDField.setText("");
        vehicleIDField.setText("");
        controllerIdField.setText("");
        vehicleModelField.setText("");
        vehicleMakeField.setText("");
        vehicleYearField.setText("");
        arrivalTimeField.setText("");
        departureTimeField.setText("");
        clientIDField.setText("");
        jobDurationField.setText("");
        jobDeadlineField.setText("");
    }



  

    // Gianna: owner submit handler
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

            FileManager.saveUser(owner); // -MEHMETS ADDITION: RECORD OWNER ENTRY DATA, NEEDED FOR DATA STORAGE

            JOptionPane.showMessageDialog(this,
                "Owner Registered Successfully!\nOwner ID: " + ownerID +
                "\nData saved to vehicular_cloud_log.txt"); // CONFIRMS TO SAVE FILE

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

            FileManager.saveUser(client); // MEHMET: SAME THING BUT FOR CLIENT

            JOptionPane.showMessageDialog(this,
                "Client Registered Successfully!\nClient ID: " + clientID +
                "\nData saved to vehicular_cloud_log.txt"); // CLIENT CONFIRMS TO SAVE FILE

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Job duration must be a valid number.");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid deadline format. Please use: yyyy-MM-ddTHH:mm\nExample: 2025-04-01T14:30");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check all fields.");

        }
    }
    private void handleComputation(){
    try{

   //Load jobs from file
   //possibly mehmets part
        File file = new File("/file that holds jobs"); 

        List<Job> jobs = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                
                String[] parts = line.split("\\|");
                if (parts.length < 4) {
                    throw new IllegalArgumentException("Invalid line: " + line);
                }

                String jobID        = parts[0].trim().split(":", 2)[1].trim(); // after "Client ID:"
                LocalDateTime ts = LocalDateTime.parse(parts[1].trim().split(":", 2)[1].trim()); // after "Timestamp:"
                int duration     = Integer.parseInt(parts[2].trim().split(":", 2)[1].trim()); // after "Approx job duration (min):"
                LocalDateTime deadline = LocalDateTime.parse(parts[3].trim().split(":", 2)[1].trim()); // after "Job deadline:"

                Job j = new Job(jobID, duration, deadline);
                j.setTime(ts);
                jobs.add(j);
            }
        }

        if (jobs.isEmpty()) {
            outputArea.setText("No jobs found.");
            return;
        }

        // Sort by arrival time for FCFS order
        jobs.sort(Comparator.comparing(Job::getTime));

        // Enqueue in arrival order 
        Deque<Client> queue = new ArrayDeque<>(clients);

        // Dequeue and compute start/completion times
        LocalDateTime current = null;
        StringBuilder sb = new StringBuilder();
        while (!queue.isEmpty()) {
            Job j = queue.pollFirst();


            LocalDateTime arrival = j.getTime();
            if (current == null || current.isBefore(arrival)) {
                //CPU idle until job arrives
                current = arrival;
            }

            LocalDateTime start = current;
            LocalDateTime completion = start.plusMinutes(j.getJobDurationMinutes());

            long waitMin = Duration.between(arrival, start).toMinutes();
            long tatMin = Duration.between(arrival, completion).toMinutes();
            Long latenessMin = null;
            if (j.getJobDeadline() != null) {
                long diff = Duration.between(j.getJobDeadline(), completion).toMinutes();
                latenessMin = Math.max(0, diff);
            }
           
            sb.append("Job ")
              .append(j.getID())
              .append(" | Arrival: ").append(arrival)
              .append(" | Start: ").append(start)
              .append(" | Completion: ").append(completion)
              .append(" | Wait(min): ").append(waitMin)
              .append(" | Turnaround(min): ").append(tatMin);
            if (latenessMin != null) sb.append(" | Lateness(min): ").append(latenessMin);
            sb.append('\n');

            // Advance the CPU time to the completion of this job
            current = completion;
        }

        // Display results
        outputArea.setText(sb.toString());

        
    } catch (Exception ex) {
        outputArea.setText("Error computing completion times: " + ex.getMessage());
    }
}

    // Gianna: go back home
    private void goHome() {
        handleClear();
        cardLayout.show(cards, "Home");
    }
}
