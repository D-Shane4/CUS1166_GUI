public class VehicleCloudApp {
    public static void main(String[] args) {
        new Thread(() -> VCServer.main(new String[0])).start();

        javax.swing.SwingUtilities.invokeLater(() -> {
            new VehicleCloudFrame();   // owner/client window
        });
    }
}
