import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class VehicleCloudFrame extends JFrame {

private VCControllerFrame controllerFrame;
//shows vehicle controller frame when owner/client clicks submit, but only creates it the first time and reuses it after that
private void showControllerFrame() {
    if (controllerFrame == null) {
        controllerFrame = new VCControllerFrame();
    } else {
        controllerFrame.setVisible(true);
        controllerFrame.toFront();
    }
}   
     // Database Connect
    private DatabaseConnection db;

    // ── Shanti: frame-level layout reference
    private CardLayout cardLayout;
    private JPanel cards;

    // ── Hawa: radio buttons on home screen
    private JRadioButton ownerButton;
    private JRadioButton clientButton;
    
    //--Hawa: Start Button on Home page
    private JButton startButton;

    // ── Hawa: Owner panel fields (declared at class level so listeners can read them)
    private JTextField ownerIDField;
    private JTextField vehicleIDField;

    private JComboBox<String> vehicleModelBox; //javonda
    private JComboBox<String> vehicleYearBox;
    private JComboBox<String> vehicleMakeBox;

    private JComboBox<String> arrivalHourBox;
    private JComboBox<String> arrivalMinuteBox;
    private JComboBox<String> arrivalAmPmBox;

    private JComboBox<String> departureHourBox;
    private JComboBox<String> departureMinuteBox;
    private JComboBox<String> departureAmPmBox;

    // ── Hawa: Client panel fields
    private JTextField clientIDField;
    private JComboBox<String> jobDurationBox;
    private JTextField jobDeadlineField;

    // ── Hawa: submit buttons (declared at class level so listeners can reference them)
    private JButton ownerSubmitButton;
    private JButton clientSubmitButton;
    private JButton ownerHomeButton;
    private JButton clientHomeButton;
    
 // ── Hawa: Clear buttons
    private JButton ownerClearButton;
    private JButton clientClearButton;

    private java.util.Map<String, String[]> makeModelMap;

    public VehicleCloudFrame() {
        db = new DatabaseConnection();
        setupFrame();       // Shanti
        createComponents(); // Hawa
        attachListeners();  // Gianna
        setVisible(true);
    }

    // ── Shanti: frame setup
    // FIX: was re-declaring 'frame' as a local variable, shadowing the class field
    private void setupFrame() {
        setSize(700, 500);
        setTitle("Vehicular Cloud Real Time System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 182, 193));
        setLocation(000, 200); // FIX: was centering on screen, changed to specific location for better multi-window experience
        setLayout(new BorderLayout());
    }

    // ── Hawa: panels, buttons, text fields
    // FIX: all JTextFields now stored as named instance variables so listeners can read them
    // FIX: submit/home buttons declared at class level instead of locally
    private void createComponents() {

        String[] durations = new String[61];
        durations[0] = "Select Minutes";
        for (int i = 1; i <= 60; i++) {
            durations[i] = String.valueOf(i);
        }
        jobDurationBox = new JComboBox<>(durations);

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

        ButtonGroup group = new ButtonGroup();
        group.add(ownerButton);
        group.add(clientButton);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridx = 0;
        homePanel.add(ownerButton, gbc);

        gbc.gridx = 1;
        homePanel.add(clientButton, gbc);

        // Owner Panel Edited Javonda
        JPanel ownerPanel = new JPanel(new GridLayout(9, 1, 0, 5));
        ownerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Text fields for user input
        ownerIDField = new JTextField(15);
        vehicleIDField = new JTextField(15);

        // Map that connects vehicle MAKE - possible MODELS
        makeModelMap = new java.util.HashMap<>();
        makeModelMap.put("Toyota", new String[]{"Camry", "Corolla", "RAV4", "Highlander", "Prius", "Tacoma",
        "Sienna", "4Runner", "Avalon", "Supra", "Tundra", "Sequoia", "Venza", "C-HR", "Yaris", "Land Cruiser","Mirai", "GR86", "bZ4X"
        ,"Vios", "Fortuner", "Innova", "Hilux", "Proace", "Coaster", "Dyna", "HiAce", "FJ Cruiser", "Matrix", "Previa", "RAV4 Prime",
         "Sienna Hybrid", "Tacoma TRD Pro", "Tundra TRD Pro", "4Runner TRD Pro", "Sequoia TRD Pro", "Land Cruiser Heritage Edition",
          "Mirai XLE", "GR86 Premium", "bZ4X Limited"
        });
        makeModelMap.put("BMW", new String[]{"3 Series", "5 Series", "X3", "X5", "Z4","i3", "i8", "M3", "M5", "X1", "X2", "X4", "X6", "X7", "7 Series", "8 Series",
        "2 Series", "4 Series", "M4", "M2", "M8", "iX3", "i4", "i7", "Z3", "Z8", "X5 M", "X6 M", "X7 M", "M760i xDrive", "M850i xDrive","330e", "530e", "745e", "iX", 
        "iX1", "iX5", "iX7", "M3 Competition", "M4 Competition", "M5 Competition", "M8 Competition", "X3 M", "X4 M","X5 M Competition", "X6 M Competition", 
        "X7 M Competition", "Z4 M40i", "Z4 sDrive30i","7 Series M Sport", "8 Series M Sport", "i4 eDrive40", "i4 M50", "iX xDrive50", "iX M60", "M2 CS", "M3 CS", "M4 CS", "M5 CS", "M8 CS", "X3 M Competition", "X4 M Competition", "X5 M Competition", "X6 M Competition", "X7 M Competition", "Z4 M40i", "Z4 sDrive30i",
         "7 Series M Sport", "8 Series M Sport", "i4 eDrive40", "i4 M50", "iX xDrive50", "iX M60", "M2 CS", "M3 CS", "M4 CS", "M5 CS", "M8 CS"
        });
        makeModelMap.put("Honda", new String[]{"Civic", "Accord", "CR-V", "Pilot", "HR-V", "Odyssey", "Ridgeline", "Insight", "Passport",
        "Clarity", "CR-Z", "Element", "S2000", "Prelude", "Crosstour", "FCX Clarity",
         "CR-V Hybrid", "Accord Hybrid", "Insight EX", "Pilot Elite", "Ridgeline Black Edition", "Odyssey Touring", "Civic Type R", "S2000 CR",
        "Clarity Plug-In Hybrid", "HR-V Sport", "Passport Sport", "Accord 2.0T","Civic Si", "CR-V Black Edition", "Pilot Black Edition",
        "Ridgeline RTL-E", "Odyssey Elite", "Insight Touring", "Clarity Electric", "S2000 Base", "Prelude SH", "Crosstour EX-L", 
        "FCX Clarity Fuel Cell", "CR-V Hybrid Black Edition", "Accord Hybrid EX-L", "Insight Touring", "Pilot Elite", "Ridgeline Black Edition",
         "Odyssey Touring", "Civic Type R Limited Edition", "S2000 CR", "Clarity Plug-In Hybrid Touring", "HR-V Sport Touring", "Passport Sport Touring", "Accord 2.0T Sport"
        });
        makeModelMap.put("Tesla", new String[]{"Model 3", "Model S", "Model X", "Model Y", "Cybertruck", "Roadster", "Semi",
        "Model 3 Standard Range Plus", "Model 3 Long Range", "Model 3 Performance", "Model S Long Range", "Model S Plaid", "Model X Long Range", "Model X Plaid", "Model Y Long Range", "Model Y Performance",
         "Cybertruck Single Motor RWD", "Cybertruck Dual Motor AWD", "Cybertruck Tri Motor AWD", "Roadster Base", "Roadster Founders Series", "Semi Truck"
        });
        makeModelMap.put("Nissan", new String[]{"Altima", "Sentra", "Rogue", "Maxima","Leaf", "Murano", "Pathfinder", "Frontier", "Titan", "Versa",
        "Kicks", "Juke", "Armada", "NV200", "GT-R", "370Z", "Z Proto", "Ariya", "Magnite", "Almera", "Navara", "X-Trail","Qashqai", "Note", "Cube", "NV3500 HD", "NV1500 Cargo",
        "NV2500 HD Cargo", "NV3500 HD Passenger", "GT-R Nismo", "370Z Nismo", "Z Proto Nismo","Ariya e-4ORCE", "Magnite Red Edition", "Almera Sportech",
        "Navara Pro-4X", "X-Trail Hybrid, Qashqai e-Power", "Note Aura", "Cube Krom", "NV3500 HD S V6", "NV1500 Cargo S V6", "NV2500 HD Cargo S V6", "NV3500 HD Passenger S V6", "GT-R Nismo Special Edition",
         "370Z Nismo Special Edition", "Z Proto Nismo Special Edition","Ariya e-4ORCE Premiere Edition", "Magnite Red Edition+", "Almera Sportech+", "Navara Pro-4X Premium", "X-Trail Hybrid e-Power", "Qashqai e-Power"
        });
        makeModelMap.put("Ford", new String[]{"Fusion", "Escape", "Explorer", "Mustang", "F-150", "Focus", "Edge", "Ranger", "Bronco", "EcoSport", "Expedition", "F-250", "F-350",
         "Mustang Mach-E", "Transit", "Taurus", "Flex", "C-Max", "GT", "F-150 Raptor", "Bronco Sport", "EcoSport SES", "Expedition Max", "F-250 Super Duty", "F-350 Super Duty", "Mustang Mach-E GT", "Transit Connect", 
         "Taurus SHO", "Flex SEL", "C-Max Energi", "GT Premium", "F-150 Raptor", "Bronco Sport Badlands", "EcoSport Titanium", "Expedition Max Platinum", "F-250 Super Duty Lariat", "F-350 Super Duty Lariat", 
         "Mustang Mach-E GT Performance Edition", "Transit Connect XLT", "Taurus SHO Premium", "Flex Titanium", "C-Max Energi Premium", "GT Premium", "F-150 Raptor", "Bronco Sport Badlands", "EcoSport Titanium", 
         "Expedition Max Platinum", "F-250 Super Duty Lariat", "F-350 Super Duty Lariat", "Mustang Mach-E GT Performance Edition", "Transit Connect XLT", "Taurus SHO Premium", "Flex Titanium", "C-Max Energi Premium", "GT Premium"
        });

         makeModelMap.put("RAM", new String[]{"1500", "2500", "3500", "ProMaster", "ProMaster City", "1500 Classic", "2500 Heavy Duty", "3500 Heavy Duty", "ProMaster Rapid", "ProMaster Window Van", "1500 Rebel",
          "2500 Power Wagon", "3500 Limited", "ProMaster City Tradesman", "ProMaster City Wagon", "1500 Laramie", "2500 Laramie", "3500 Laramie", "ProMaster City SLT", "ProMaster City Wagon SLT", "1500 Big Horn", "2500 Big Horn", 
          "3500 Big Horn", "ProMaster City SLT", "ProMaster City Wagon SLT", "1500 Lone Star", "2500 Lone Star", "3500 Lone Star", "ProMaster City Limited", "ProMaster City Wagon Limited"
         });

         makeModelMap.put("Chevrolet", new String[]{"Silverado", "Equinox", "Malibu", "Traverse", "Tahoe", "Suburban", "Colorado", "Camaro", "Impala", "Blazer", "Trailblazer", "Sonic", "Spark", "Bolt EV", "Corvette", 
         "Silverado 1500", "Equinox Premier", "Malibu LT", "Traverse RS", "Tahoe LT", "Suburban LT", "Colorado ZR2",
        "Camaro SS", "Impala LTZ", "Blazer RS", "Trailblazer ACTIV", "Sonic Premier", "Spark LS", "Bolt EUV", "Corvette Stingray", "Silverado 2500HD", "Equinox Midnight Edition",
        "Malibu Premier", "Traverse Premier", "Tahoe RST", "Suburban RST", "Colorado Z71", "Camaro LT1", "Impala Premier", "Blazer Premier", "Trailblazer Premier",
        "Sonic Redline", "Spark ACTIV", "Bolt EV Premier", "Corvette Z06", "Silverado 3500HD", "Equinox RS", "Malibu RS", "Traverse RS", "Tahoe RS", "Suburban RS", "Colorado LTZ", "Camaro LT", "Impala LT", "Blazer LT",
        "Trailblazer LT", "Sonic LT", "Spark Premier",
        "Bolt EUV Premier", "Corvette Grand Sport"
        });
         makeModelMap.put("Mercedes-Benz", new String[]{"C-Class", "E-Class", "S-Class", "GLC", "GLE", "GLS", "A-Class", "CLA", "CLS", "G-Class", "SL-Class", "AMG GT", "C-Class Sedan", "E-Class Sedan", "S-Class Sedan",
          "GLC SUV", "GLE SUV", "GLS SUV", "A-Class Sedan", "CLA Coupe",
          "CLS Coupe", "G-Class SUV", "SL Roadster", "AMG GT Coupe",
          "C-Class Coupe", "E-Class Coupe", "S-Class Coupe", "GLC Coupe", "GLE Coupe", "GLS Maybach", "A-Class Hatchback", "CLA Shooting Brake", "CLS Shooting Brake", "G-Class Maybach",
          "SL Roadster AMG Line", "AMG GT Roadster", "C-Class Cabriolet", "E-Class Cabriolet", "S-Class Cabriolet", "GLC Cabriolet", "GLE Maybach", "GLS Maybach", "A-Class Sedan AMG Line", "CLA Coupe AMG Line",
          "CLS Coupe AMG Line", "G-Class SUV AMG Line", "SL Roadster AMG Line", "AMG GT Roadster"
        });
         makeModelMap.put("Volkswagen", new String[]{"Golf", "Passat", "Tiguan", "Jetta", "Atlas", "Beetle", "Arteon", "ID.4", "Polo", "Touareg", "Golf GTI", "Passat R-Line", "Tiguan SE", "Jetta GLI",
          "Atlas Cross Sport", "Beetle Final Edition", "Arteon SEL", "ID.4 Pro", "Polo S", "Touareg R-Line", "Golf R", "Passat V6", "Tiguan SEL", "Jetta S", "Atlas SE", "Beetle Dune", "Arteon SEL Premium",
           "ID.4 Pro S", "Polo SEL", "Touareg R-Line", "Golf Alltrack", "Passat GT", "Tiguan Limited Edition", "Jetta SE", "Atlas SEL Premium", "Beetle GSR", "Arteon SEL Premium R-Line", "ID.4 AWD", "Polo SEL Premium", "Touareg R-Line Black Edition"
        });
         makeModelMap.put("Audi", new String[]{"A3", "A4", "A6", "Q5", "Q7", "Q8", "A5", "A7", "A8", "TT", "R8", "A3 Sedan", "A4 Sedan", "A6 Sedan", "Q5 SUV", "Q7 SUV", "Q8 SUV", "A5 Coupe", "A7 Sportback", 
         "A8 L", "TT Coupe", "R8 V10", "A3 Cabriolet", "A4 Allroad", "A6 Allroad", "Q5 Sportback", "Q7 S line", "Q8 60TFSI e", "A5 Sportback", "A7 Sportback", "A8 L", "TT Roadster", "R8 Spyder", "A3 Sportback", "A4 Avant",
         "A6 Avant", "Q5 S line", "Q7 S line", "Q8 60TFSI e", "A5 Sportback", "A7 Sportback", "A8 L", "TT Roadster", "R8 Spyder", "A3 Sportback", "A4 Avant", "A6 Avant", "Q5 S line", "Q7 S line", "Q8 60TFSI e",
         "A5 Sportback", "A7 Sportback", "A8 L", "TT Roadster", "R8 Spyder", "A3 Sportback", "A4 Avant", "A6 Avant", "Q5 S line", "Q7 S line", "Q8 60TFSI e", "A5 Sportback", "A7 Sportback", "A8 L", "TT Roadster", "R8 Spyder"
        });
        makeModelMap.put("Hyundai", new String[]{"Elantra", "Sonata", "Tucson", "Santa Fe", "Kona", "Palisade", "Venue", "Ioniq 5", "Accent", "Veloster", "Santa Cruz", "Nexo", "Ioniq 6", "Elantra N", "Sonata N Line", 
        "Tucson N Line", "Santa Fe Hybrid", "Kona Electric", "Palisade Calligraphy", "Venue Denim", "Ioniq 5 Limited", "Accent Blue", "Veloster N", "Santa Cruz ZH2", "Nexo FCEV", "Ioniq 6 Limited", "Elantra N Redline",
         "Sonata N Line Midnight Edition", "Tucson N Line Night Edition", "Santa Fe Hybrid Calligraphy", "Kona Electric Premium", "Palisade Calligraphy", "Venue Denim", "Ioniq 5 Limited", "Accent Blue", "Veloster N", "Santa Cruz ZH2", 
         "Nexo FCEV", "Ioniq 6 Limited", "Elantra N Redline", "Sonata N Line Midnight Edition", "Tucson N Line Night Edition", "Santa Fe Hybrid Calligraphy", "Kona Electric Premium"
        });
        makeModelMap.put("Kia", new String[]{"Soul", "Sportage", "Sorento", "Telluride", "Seltos", "Niro", "Carnival", "K5", "Stinger", "Forte", "Rio", "EV6", "Seltos X-Line", "Niro EV", "Carnival VIP", "K5 GT", "Stinger GT",
         "Forte GT", "Rio S", "EV6 GT-Line", "Soul EV", "Sportage SX", "Sorento X-Line", "Telluride Nightfall Edition", "Seltos X-Line", "Niro EV", "Carnival VIP", "K5 GT", "Stinger GT", "Forte GT", "Rio S", "EV6 GT-Line", "Soul EV", "Sportage SX",
          "Sorento X-Line", "Telluride Nightfall Edition", "Seltos X-Line", "Niro EV", "Carnival VIP", "K5 GT", "Stinger GT", "Forte GT", "Rio S", "EV6 GT-Line"
        });
        makeModelMap.put("Subaru", new String[]{"Outback", "Forester", "Crosstrek", "Impreza", "Ascent", "Legacy", "WRX", "BRZ", "XV", "WRX STI", "Outback Wilderness", "Forester Sport", "Crosstrek Hybrid", "Impreza 5-Door", "Ascent Premium", "Legacy B4", "WRX S4", "BRZ tS",
         "XV Hybrid", "WRX STI S209", "Outback Onyx Edition", "Forester Wilderness", "Crosstrek Sport", "Impreza Sport", "Ascent Limited", "Legacy Outback", "WRX S4", "BRZ tS"
        });
        makeModelMap.put("Mazda", new String[]{"Mazda3", "Mazda6", "CX-5", "CX-9", "MX-5 Miata", "CX-30", "CX-50", "Mazda2", "Mazda5", "Mazda CX-3", "Mazda CX-4", "Mazda CX-7", "Mazda CX-8", "Mazda CX-9 Signature", "MX-5 Miata RF", "CX-30 Turbo", "CX-50 2.5T",
         "Mazda2 Sedan", "Mazda5 Grand Touring", "Mazda CX-3 Sport", "Mazda CX-4 2.0L", "Mazda CX-7 2.5L", "Mazda CX-8 2.5L", "Mazda CX-9 Signature", "MX-5 Miata RF", "CX-30 Turbo", "CX-50 2.5T"
        });
        makeModelMap.put("Dodge", new String[]{"Charger", "Durango","Journey","Grand Caravan", "Challenger", "Dart", "Viper", "Ram 1500", "Ram 2500", "Ram 3500", "Charger SRT Hellcat", "Durango SRT Hellcat", "Journey Crossroad", "Grand Caravan SXT", "Challenger R/T",
         "Dart GT", "Viper ACR", "Ram 1500 Rebel", "Ram 2500 Power Wagon", "Ram 3500 Limited", "Charger SRT Hellcat Redeye", "Durango SRT Hellcat Redeye", "Journey GT", "Grand Caravan GT", "Challenger R/T Scat Pack", "Dart GT", "Viper GTC", "Ram 1500 Limited", 
         "Ram 2500 Laramie", "Ram 3500 Laramie", "Charger SRT Hellcat Redeye Widebody", "Durango SRT Hellcat Redeye Widebody", "Journey GT", "Grand Caravan GT", "Challenger R/T Scat Pack Widebody", "Dart GT", "Viper GTC", "Ram 1500 Limited", "Ram 2500 Laramie", "Ram 3500 Laramie"
        });
        makeModelMap.put("Jeep", new String[]{"Wrangler","Grand Cherokee","Cherokee","Compass","Renegade", "Gladiator", "Wrangler Unlimited", "Grand Cherokee L", "Cherokee Trailhawk", "Compass Trailhawk", "Renegade Trailhawk", "Gladiator Mojave", "Wrangler Rubicon", "Grand Cherokee Summit",
         "Cherokee Latitude", "Compass Latitude", "Renegade Latitude", "Gladiator Rubicon", "Wrangler Unlimited Sahara", "Grand Cherokee L Summit Reserve", "Cherokee Limited", "Compass Limited", "Renegade Limited", "Gladiator Rubicon 392", "Wrangler 4xe", "Grand Cherokee L Overland", 
         "Cherokee Limited X", "Compass Limited X", "Renegade Limited X", "Gladiator Mojave"
        });

        makeModelMap.put("Lexus", new String[]{"RX","ES","NX","GX","LX", "IS", "UX", "RC", "LC", "LS", "LM", "LFA", "RX Hybrid", "ES Hybrid", "NX Hybrid", "GX 460", "LX 570", "IS 350", "UX 250h", "RC F", "LC 500h", "LS 500h", "LM 300h", "LFA Nürburgring Package"
        ,"RX 500h", "ES 300h", "NX 350h", "GX 460", "LX 570", "IS 350 F Sport", "UX 250h", "RC F Track Edition", "LC 500h Inspiration Series", "LS 500h F Sport", "LM 300h Executive", "LFA Nürburgring Package"
        });
         makeModelMap.put("GMC", new String[]{"Sierra","Terrain","Acadia","Yukon", "Yukon XL", "Canyon", "Sierra Denali", "Terrain Denali", "Acadia Denali", "Yukon Denali", "Yukon XL Denali", "Canyon AT4", "Sierra 1500", "Terrain SLE", "Acadia SLE", "Yukon SLE",
          "Yukon XL SLE", "Canyon SLT", "Sierra 2500HD", "Terrain SLT", "Acadia SLT", "Yukon SLT", "Yukon XL SLT", "Canyon Denali", "Sierra 3500HD", "Terrain AT4", "Acadia AT4", "Yukon AT4", "Yukon XL AT4", "Canyon Denali"
        });
        makeModelMap.put("Cadillac", new String[]{"Escalade","XT5","XT6","CT5", "CT6", "XT4", "XT4 Sport", "XT5 Luxury", "XT6 Premium Luxury", "CT5-V", "CT6-V", "Escalade ESV", "XT5 Platinum", "XT6 Sport", "CT5-V Blackwing", "CT6-V Blackwing", "Escalade ESV Platinum"
        ,"XT5 Platinum", "XT6 Sport", "CT5-V Blackwing", "CT6-V Blackwing", "Escalade ESV Platinum"
        });
        makeModelMap.put("Acura", new String[]{"RDX","MDX","TLX", "ILX", "NSX", "RLX", "ZDX", "RDX A-Spec", "MDX A-Spec", "TLX A-Spec", "ILX A-Spec", "NSX Type S", "RLX Sport Hybrid SH-AWD", "ZDX Concept"
        });
        makeModelMap.put("Infiniti", new String[]{"Q50","Q60","QX50","QX60", "QX80", "Q50 Red Sport 400", "Q60 Red Sport 400", "QX50 Sensory", "QX60 Sensory", "QX80 Sensory", "Q50 Pure", "Q60 Pure", "QX50 Pure", "QX60 Pure", "QX80 Pure",
         "Q50 Luxe", "Q60 Luxe", "QX50 Luxe", "QX60 Luxe", "QX80 Luxe", "Q50 Sport", "Q60 Sport", "QX50 Sport", "QX60 Sport", "QX80 Sport"});
                            
        makeModelMap.put("Lincoln", new String[]{"Navigator","Corsair","Aviator", "Nautilus", "MKZ", "Continental", "MKT", "Town Car", "Navigator L", "Corsair Grand Touring", "Aviator Grand Touring", "Nautilus Black Label", "MKZ Hybrid", "Continental Coach Door Edition",
          "MKT Town Car Limited Edition", "Town Car Signature Limited Edition", "Navigator L Black Label", "Corsair Grand Touring"});

        makeModelMap.put("Volvo", new String[]{"XC90","XC60","S60","S90", "V60", "V90", "XC40", "S60 Recharge", "S90 Recharge", "V60 Recharge", "V90 Recharge", "XC40 Recharge", "XC90 Recharge", "XC60 Recharge", "S60 Polestar Engineered", "S90 Polestar Engineered",
         "V60 Polestar Engineered", "V90 Polestar Engineered", "XC40 Polestar Engineered", "XC90 Polestar Engineered"});

        makeModelMap.put("Mitsubishi", new String[]{"Outlander","Eclipse Cross","Outlander PHEV", "Mirage", "Lancer", "ASX", "L200", "Pajero", "Outlander Sport", "Eclipse Cross PHEV", "Outlander PHEV SEL", "Mirage G4", "Lancer Evolution", 
        "ASX Ralliart", "L200 Triton", "Pajero Sport", "Outlander Sport SE", "Eclipse Cross PHEV SEL", "Outlander PHEV SE", "Mirage G4 ES", "Lancer Evolution Final Edition", "ASX Ralliart", "L200 Triton Athlete", "Pajero Sport Dakar Edition"});

        makeModelMap.put("Alfa Romeo", new String[]{"Giulia","Stelvio", "4C", "Giulia Quadrifoglio", "Stelvio Quadrifoglio", "4C Spider", "Giulia Ti", "Stelvio Ti", "4C Coupe", "Giulia Veloce", "Stelvio Veloce",
         "4C Launch Edition", "Giulia GTAm", "Stelvio GTAm", "4C Spider Italia", "Giulia Ti Sport", "Stelvio Ti Sport", "4C Coupe Italia", "Giulia Veloce Ti", "Stelvio Veloce Ti", "4C Launch Edition Italia"});

        makeModelMap.put("Ferrari ", new String[]{"488 GTB ","Portofino ","F8 Tributo ","Roma ","SF90 Stradale","812 Superfast ","GTC4Lusso ","F8 Spider ","Monza SP1/SP2 ","Portofino M ","296 GTB ",
        "Daytona SP3 ","812 GTS ","GTC4Lusso T ","Roma Spider ","SF90 Spider", "488 Pista ","Portofino M ","F8 Tributo ","Roma ","SF90 Stradale","812 Superfast ","GTC4Lusso ","F8 Spider ","Monza SP1/SP2 ","Portofino M ","296 GTB "});

        makeModelMap.put("Lamborghini", new String[]{"Huracan","Aventador","Urus", "Sian", "Countach", "Gallardo", "Murciélago", "Reventón", "Veneno", "Centenario", "Aventador SVJ", "Huracán EVO", "Urus Performante", "Sian FKP 37", "Countach LPI 800-4", "Gallardo LP 570-4 Superleggera",
         "Murciélago LP 670-4 SV", "Reventón Roadster", "Veneno Roadster", "Centenario Roadster", "Aventador SVJ Roadster", "Huracán EVO Spyder"});

        makeModelMap.put("Porsche", new String[]{"911","Cayenne","Macan", "Panamera", "Taycan", "Boxster", "Cayman", "918 Spyder", "911 Carrera", "Cayenne E-Hybrid", "Macan S", "Panamera 4S", "Taycan Turbo", "Boxster Spyder", "Cayman GT4", "918 Spyder Weissach Package",
         "911 Turbo S", "Cayenne S", "Macan GTS", "Panamera Turbo S", "Taycan 4S", "Boxster GTS", "Cayman GT4 RS", "918 Spyder Weissach Package", "911 Carrera S", "Cayenne E-Hybrid", "Macan S", "Panamera 4S", "Taycan Turbo", "Boxster Spyder", "Cayman GT4",
          "918 Spyder Weissach Package", "911 Turbo S", "Cayenne S", "Macan GTS", "Panamera Turbo S", "Taycan 4S", "Boxster GTS", "Cayman GT4 RS",});
        
        makeModelMap.put("Bentley", new String[]{"Continental GT","Bentayga", "Flying Spur", "Mulsanne", "Continental GT V8", "Bentayga Speed", "Flying Spur W12", "Mulsanne Speed", "Continental GT Convertible", "Bentayga Hybrid", "Flying Spur V8 S", 
         "Mulsanne Extended Wheelbase", "Continental GT Speed", "Bentayga V8", "Flying Spur W12 S", "Mulsanne Grand Limousine"});

        makeModelMap.put("Rolls-Royce", new String[]{"Phantom","Cullinan", "Ghost", "Wraith", "Dawn", "Phantom Drophead Coupe", "Cullinan Black Badge", "Ghost Black Badge", "Wraith Black Badge", "Dawn Black Badge", "Phantom Extended Wheelbase",
         "Cullinan Silver Bullet", "Ghost Silver Bullet", "Wraith Silver Bullet", "Dawn Silver Bullet"});

        makeModelMap.put("McLaren", new String[]{"720S","P1", "Artura", "Senna", "720S Spider", "P1 GTR", "Artura GT4", "Senna GTR", "720S Coupe", "P1 Roadster", "Artura Pro", "Senna Pro", "720S Spider", "P1 GTR", "Artura GT4", 
        "Senna GTR", "720S Coupe", "P1 Roadster", "Artura Pro", "Senna Pro"});

        makeModelMap.put("Aston Martin", new String[]{"DB11","Vantage", "DBS Superleggera", "Rapide AMR", "Valkyrie", "DB11 Volante", "Vantage Roadster", "DBS Superleggera Volante", "Rapide AMR", "Valkyrie Roadster", "DB11 AMR", "Vantage F1 Edition",
         "DBS Superleggera AMR", "Rapide AMR", "Valkyrie Roadster"});

        makeModelMap.put("Bugatti", new String[]{"Chiron","Veyron", "Divo", "Centodieci", "La Voiture Noire", "Chiron Sport", "Veyron Super Sport", "Divo", "Centodieci", "La Voiture Noire", "Chiron Pur Sport",
         "Veyron Grand Sport Vitesse", "Divo", "Centodieci", "La Voiture Noire"});

        makeModelMap.put("Pagani", new String[]{"Huayra","Zonda", "Huayra BC", "Zonda R", "Huayra Roadster", "Zonda Cinque", "Huayra Imola", "Zonda Tricolore", "Huayra Roadster BC", "Zonda HP Barchetta"});

        makeModelMap.put("Koenigsegg", new String[]{"Agera","Regera", "Jesko", "Gemera", "Agera RS", "Regera", "Jesko Absolut", "Gemera"});

        makeModelMap.put("Rivian", new String[]{"R1T","R1S", "R1T Launch Edition", "R1S Launch Edition", "R1T Adventure Package", "R1S Adventure Package", "R1T Explore Package", "R1S Explore Package"});

        makeModelMap.put("Lucid Motors", new String[]{"Air","Gravity", "Air Pure", "Air Touring", "Air Grand Touring", "Gravity", "Air Pure", "Air Touring", "Air Grand Touring"});

        makeModelMap.put("Polestar", new String[]{"Polestar 1","Polestar 2", "Polestar 3", "Polestar 4", "Polestar 5", "Polestar 6", "Polestar 7", "Polestar 8", "Polestar 9", "Polestar 10"});

        makeModelMap.put("Fisker", new String[]{"Ocean","EMotion", "Ocean Extreme", "EMotion", "Ocean Extreme", "EMotion"});

        makeModelMap.put("Other", new String[]{"Other"});

         // Dropdown for selecting vehicle make (brand)
        vehicleMakeBox = new JComboBox<>(new String[]{
            "Select Make", "Toyota", "BMW", "Honda", "Tesla", "Nissan", "Ford", "RAM", "Chevrolet", "Mercedes-Benz", "Volkswagen", "Audi", "Hyundai",
             "Kia", "Subaru", "Mazda", "Dodge", "Jeep", "Lexus", "GMC", "Cadillac", "Acura", "Infiniti", "Lincoln", "Volvo", "Mitsubishi",
              "Alfa Romeo", "Ferrari", "Lamborghini", "Porsche", "Bentley", "Rolls-Royce", "McLaren", "Aston Martin", "Bugatti", "Pagani", "Koenigsegg",
               "Rivian", "Lucid Motors", "Polestar", "Fisker", "Other"
        });

        // Dropdown for selecting vehicle model (depends on make)
       // Initially disabled until a make is selected
        vehicleModelBox = new JComboBox<>(new String[]{
            "Select Model"
        });
        vehicleModelBox.setEnabled(false);

        // Create dropdown for vehicle years (1995 → 2026)
        String[] years = new String[2026 - 1995 + 2];
        years[0] = "Select Year";
        int index = 1;
        for (int y = 1995; y <= 2026; y++) {
            years[index++] = String.valueOf(y);
        }

        // Year dropdown
        vehicleYearBox = new JComboBox<>(years);

        // Time dropdowns (for arrival/departure)
        // Hours (12-hour format)

        String[] hours = {
            "HH", "01", "02", "03", "04", "05", "06",
            "07", "08", "09", "10", "11", "12"
        };

        // Minutes (00–59)
        String[] minutes = new String[61];
        minutes[0] = "MM";
        for (int i = 0; i < 60; i++) {
            minutes[i + 1] = String.format("%02d", i);
        }

        // AM / PM selection
        String[] ampm = {"AM/PM", "AM", "PM"};

        // Arrival time dropdowns
        arrivalHourBox = new JComboBox<>(hours);
        arrivalMinuteBox = new JComboBox<>(minutes);
        arrivalAmPmBox = new JComboBox<>(ampm);

        // Departure time dropdowns
        departureHourBox = new JComboBox<>(hours);
        departureMinuteBox = new JComboBox<>(minutes);
        departureAmPmBox = new JComboBox<>(ampm);

        // Add components to panel
        ownerPanel.add(makeLabel("Owner Registration"));
        ownerPanel.add(makeRow("Owner ID:", ownerIDField));// Owner ID input
        ownerPanel.add(makeRow("Vehicle ID:", vehicleIDField));// Vehicle ID input

        ownerPanel.add(makeRow("Vehicle Make:", vehicleMakeBox)); // Make dropdown
        ownerPanel.add(makeRow("Vehicle Model:", vehicleModelBox)); // Model dropdown
        ownerPanel.add(makeRow("Vehicle Year:", vehicleYearBox)); // Year dropdown

        // Custom row for time input (hour + minute + AM/PM)
        ownerPanel.add(makeTimeRow("Arrival Time:", arrivalHourBox, arrivalMinuteBox, arrivalAmPmBox));
        ownerPanel.add(makeTimeRow("Departure Time:", departureHourBox, departureMinuteBox, departureAmPmBox));

        // Panel for buttons (Submit, Clear, Home)
        JPanel ownerButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));

        ownerSubmitButton = new JButton("Submit"); // sends data to server
        ownerHomeButton = new JButton("Home"); // go back to main screen
        ownerClearButton = new JButton("Clear"); // reset fields

        // Add buttons to panel
        ownerButtons.add(ownerSubmitButton);
        ownerButtons.add(ownerClearButton);
        ownerButtons.add(ownerHomeButton);

        // Add button panel to main owner panel
        ownerPanel.add(ownerButtons);

        // Client Panel
        JPanel clientPanel = new JPanel(new GridLayout(5, 1, 0, 5));
        clientPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add the title at the top of the client form
        clientPanel.add(makeLabel("Client Registration"));

        // Add a row for Client ID input using a text field
        clientPanel.add(makeRow("Client ID:",             clientIDField   = new JTextField(15)));
        // Add a row for Job Duration input using a combo box
        clientPanel.add(makeRow("Job Duration (min):", jobDurationBox));
        // Add a row for Job Deadline input using a text field
        clientPanel.add(makeRow("Job Deadline (yyyy-MM-ddTHH:mm):", jobDeadlineField = new JTextField(15)));

        // Panel for buttons (Submit, Clear, Home)
        JPanel clientButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientSubmitButton = new JButton("Submit");
        clientHomeButton   = new JButton("Home");
        clientClearButton = new JButton ("Clear");

        // Add buttons to panel
        clientButtons.add(clientSubmitButton);
        clientButtons.add(clientClearButton);
        clientButtons.add(clientHomeButton);
        clientPanel.add(clientButtons);

        // Add all panels to card layout
        cards.add(welcomePanel, "Welcome");
        cards.add(homePanel,   "Home");
        cards.add(ownerPanel,  "Owner");
        cards.add(clientPanel, "Client");

        // Add card panel to frame
        add(cards, BorderLayout.CENTER);
        //make sure the welcome page shows first
        cardLayout.show(cards, "Welcome"); // FIX: was "welcome" (lowercase), CardLayout is case-sensitive
    }

    // Hawa: helper to build a labeled row 
    private JPanel makeRow(String labelText, JTextField field) {
    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
    JLabel label = new JLabel(labelText); // Create label with provided text
    label.setPreferredSize(new Dimension(180, 25));
    // Set a fixed size for the text field for better alignment
    row.add(label);
    row.add(field);
    return row;
}

    // Overloaded helper to build a labeled row with a combo box instead of text field
    private JPanel makeRow(String labelText, JComboBox<String> comboBox) {
    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel label = new JLabel(labelText); // Create label with provided text
    label.setPreferredSize(new Dimension(180, 25));
    comboBox.setPreferredSize(new Dimension(165, 25));
    // Set a fixed size for the combo box for better alignment
    row.add(label);
    row.add(comboBox);
    return row;

}
    // Helper to build a row for time input (hour + minute + AM/PM)
    private JPanel makeTimeRow(String labelText, JComboBox<String> hourBox,
    JComboBox<String> minuteBox, JComboBox<String> amPmBox) { // FIX: added amPmBox to parameters so we can build the full time input row
    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Align components to the left
    JLabel label = new JLabel(labelText); // Create label with provided text
    label.setPreferredSize(new Dimension(180, 25));

    // Set fixed sizes for the time dropdowns for better alignment
    hourBox.setPreferredSize(new Dimension(60, 25));
    minuteBox.setPreferredSize(new Dimension(60, 25));
    amPmBox.setPreferredSize(new Dimension(80, 25));

     // Add components to the row in order: label, hour dropdown, ":", minute dropdown, AM/PM dropdown
    row.add(label);
    row.add(hourBox);
    row.add(new JLabel(":"));
    row.add(minuteBox);
    row.add(amPmBox);

    return row;
    }

    // Helper to create styled section labels (like form titles)
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
        startButton.addActionListener(e -> cardLayout.show(cards, "Home"));
        //home buttons
        ownerHomeButton.addActionListener(e -> goHome());
        clientHomeButton.addActionListener(e -> goHome());
       //submit buttons
        ownerSubmitButton.addActionListener(e -> handleOwnerSubmit());
        clientSubmitButton.addActionListener(e -> handleClientSubmit());
        //clear buttons
        ownerClearButton.addActionListener(e -> handleClear());
        clientClearButton.addActionListener(e -> handleClear());

         vehicleMakeBox.addActionListener(e -> {
        String selectedMake = (String) vehicleMakeBox.getSelectedItem();

        vehicleModelBox.removeAllItems();
        vehicleModelBox.addItem("Select Model");

        if (selectedMake != null && makeModelMap.containsKey(selectedMake)) {
            for (String model : makeModelMap.get(selectedMake)) {
                vehicleModelBox.addItem(model);
            }
            vehicleModelBox.setEnabled(true);
        } else {
            vehicleModelBox.setEnabled(false);
        }
    });
    }

    // Gianna: clear all fields
    
    private void handleClear() {
        ownerIDField.setText("");
        vehicleIDField.setText("");

        // Reset make/model/year dropdowns
        vehicleMakeBox.setSelectedIndex(0);
        vehicleModelBox.removeAllItems();
        vehicleModelBox.addItem("Select Model");
        vehicleModelBox.setEnabled(false);
        // Reset year dropdown
        vehicleYearBox.setSelectedIndex(0);
        // Reset time dropdowns
        arrivalHourBox.setSelectedIndex(0);
        arrivalMinuteBox.setSelectedIndex(0);
        arrivalAmPmBox.setSelectedIndex(0);
        // Reset departure time dropdowns
        departureHourBox.setSelectedIndex(0);
        departureMinuteBox.setSelectedIndex(0);
        departureAmPmBox.setSelectedIndex(0);
        // Reset client fields
        clientIDField.setText("");
        jobDurationBox.setSelectedIndex(0);
        jobDeadlineField.setText("");
    }
    

    // Gianna: owner submit handler (EDITED & PATCHED BY MEHMET)
     private void handleOwnerSubmit() {
        try {
        // Read all input values from the form
        String ownerID = ownerIDField.getText().trim();
        String vehicleID = vehicleIDField.getText().trim();
        String vehicleMake = (String) vehicleMakeBox.getSelectedItem();
        String vehicleModel = (String) vehicleModelBox.getSelectedItem();
        String yearText = (String) vehicleYearBox.getSelectedItem();
        // Arrival time inputs
        String arrivalHour = (String) arrivalHourBox.getSelectedItem();
        String arrivalMinute = (String) arrivalMinuteBox.getSelectedItem();
        String arrivalAmPm = (String) arrivalAmPmBox.getSelectedItem();
        // Departure time inputs
        String departureHour = (String) departureHourBox.getSelectedItem();
        String departureMinute = (String) departureMinuteBox.getSelectedItem();
        String departureAmPm = (String) departureAmPmBox.getSelectedItem();
            if (ownerID.isEmpty() || vehicleID.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Owner ID and Vehicle ID are required.");
                return;
            }
            // Check that all dropdowns have a valid selection (not the default index 0)
            if (vehicleMakeBox.getSelectedIndex() == 0 ||
                vehicleModelBox.getSelectedIndex() == 0 ||
                vehicleYearBox.getSelectedIndex() == 0 ||
                arrivalHourBox.getSelectedIndex() == 0 ||
                arrivalMinuteBox.getSelectedIndex() == 0 ||
                arrivalAmPmBox.getSelectedIndex() == 0 ||
                departureHourBox.getSelectedIndex() == 0 ||
                departureMinuteBox.getSelectedIndex() == 0 ||
                departureAmPmBox.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Please complete all dropdown selections.");
                return;
            }
            // Validate that year is a number
            int vehicleYear = Integer.parseInt(yearText);

            // Convert arrival and departure times to a consistent format (e.g. "HH:mm AM/PM")
            String arrivalTime = arrivalHour + ":" + arrivalMinute + " " + arrivalAmPm;
            String departureTime = departureHour + ":" + departureMinute + " " + departureAmPm;

            // Create Owner object with the collected data
            Owner owner = new Owner(ownerID, vehicleID, vehicleModel, vehicleMake,
                                    vehicleYear, arrivalTime, departureTime);
 
            // Send to VC Controller server and receive decision  ← SOCKET PATH
            // (mirrors handleClientSubmit — server now owns the file write)


            showControllerFrame(); // Show the controller frame immediately after submitting owner info, before waiting for server response, to improve user experience and provide feedback that the submission is being processed

        // Javonda (EDITED): run owner request in background so GUI does not freeze
        // Create a new thread so the GUI does not freeze while sending data to server

        new Thread(() -> {

        // Send vehicle/owner info to server (localhost, port 5050) Server will respond with "ACCEPTED", "REJECTED", or error
        String result = owner.sendVehicleInfo("localhost", 5050);

        // Switch back to Swing UI thread to safely update the interface
        SwingUtilities.invokeLater(() -> {
         
        // If server accepted the vehicle registration // handling db logic

        if ("ACCEPTED".equals(result)) {
            // Milestone 6 Hawa
            db.insertUser(ownerID, "owner");
            LocalDateTime timestamp = LocalDateTime.now(); // not being used
            LocalDateTime jobDeadline = null; // owner does not use this

            db.insertRequest(
            "REQ-" + System.currentTimeMillis(),
            ownerID,
            timestamp,
            vehicleID,
            vehicleMake,
            vehicleModel,
            vehicleYear,
            arrivalTime,
            departureTime,
            null,
            jobDeadline
        );

            JOptionPane.showMessageDialog(this,
                "Vehicle Registered Successfully!\nOwner ID: " + ownerID +
                "\nData saved to vehicular_cloud_log.txt");

         // If server rejected the registration (ex. due to pending request or invalid data)
        } else if ("REJECTED".equals(result)) {

            JOptionPane.showMessageDialog(this,
                "Registration Rejected by VC Controller.\nData was NOT saved.");
        // If something went wrong (connection issue, null response, etc.)
        } else {
            JOptionPane.showMessageDialog(this, "Server Communication Error.");
        }
     });
        }).start(); // Start the thread so everything runs without freezing the GUI

    //the Owner class's sendVehicleInfo method- handles the socket communication, including connecting to the server, sending the data, and returning the server's response as a string.
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vehicle year must be a valid number.");
        } catch (Exception e) { // catch any other unexpected exceptions to prevent crashes and show a user-friendly message
            JOptionPane.showMessageDialog(this, "Invalid input. Please check all fields.");
        }
     }
     
    // Gianna: client submit handler
    private void handleClientSubmit() {
        try {
            String clientID          = clientIDField.getText().trim();
           if (jobDurationBox.getSelectedIndex() == 0) {
    JOptionPane.showMessageDialog(this, "Please select a job duration.");
    return;
    }

        int jobDurationMinutes = Integer.parseInt((String) jobDurationBox.getSelectedItem());
            LocalDateTime jobDeadline = LocalDateTime.parse(
                jobDeadlineField.getText().trim(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
            );

            if (clientID.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Client ID is required.");
                return;
            }

            Client client = new Client(clientID, jobDurationMinutes, jobDeadline);

         showControllerFrame(); // Show the controller frame immediately after submitting client info, before waiting for server response, to improve user experience and provide feedback that the submission is being processed
        // milestone 5 - redirected data to socket in Client class by calling jobrequest, returns message based on response from server 
        // Javonda (EDITED): run client request in background so GUI does not freeze
        new Thread(() -> {
            String result = client.jobRequest("localhost", 5050);

        // Switch back to the Swing UI thread to safely update the interface with the result from the server
            SwingUtilities.invokeLater(() -> {

        // If server accepted the job
        if ("ACCEPTED".equals(result)) {
            JOptionPane.showMessageDialog(this, "Job Accepted by Server");

         // If server rejected the job
        } else if ("REJECTED".equals(result)) {
            JOptionPane.showMessageDialog(this, "Job Rejected by Server");

        // If something went wrong (no response or error)
        } else {
            JOptionPane.showMessageDialog(this, "Server Communication Error"); // FIX: added message for null/invalid response from server to improve user feedback in case of connection issues or server errors
        }
            });
        }).start();// Start the thread actually runs the code inside it 

        } catch (NumberFormatException e) {// catch invalid number format for job duration
            JOptionPane.showMessageDialog(this, "Job duration must be a valid number.");

        } catch (DateTimeParseException e) { // catch invalid date format for job deadline
            JOptionPane.showMessageDialog(this, "Deadline must be in format: yyyy-MM-ddTHH:mm");

        } catch (Exception e) { // catch any other unexpected exceptions to prevent crashes and show a user-friendly message
            JOptionPane.showMessageDialog(this, "Invalid input. Please check all fields."); // FIX: added generic catch for any other exceptions to improve robustness and user feedback in case of unforeseen errors
        }
    }  

    // Gianna: go back home
    private void goHome() {
        handleClear();
        cardLayout.show(cards, "Home");
    }
}
    




