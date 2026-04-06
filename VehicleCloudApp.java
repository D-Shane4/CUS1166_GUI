public class VehicleCloudApp {
    public static void main(String[] args) {
        // Javonda (EDITED): start VC Server in background thread
        new Thread(() -> VCServer.main(new String[0])).start();

        new VehicleCloudFrame();
    }
}
