import java.util.*;

// ================== ABSTRACTION ==================
abstract class ParkingZone {
    protected String name;

    ParkingZone(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract String predict(int hour, boolean event);
}

// ================== INHERITANCE + POLYMORPHISM ==================
class OfficeZone extends ParkingZone {
    OfficeZone() { super("Office"); }

    @Override
    public String predict(int hour, boolean event) {
        if (event) return "Low";
        if (hour >= 9 && hour <= 18) return "Low";
        return "High";
    }
}

class MarketZone extends ParkingZone {
    MarketZone() { super("Market"); }

    @Override
    public String predict(int hour, boolean event) {
        if (event) return "Low";
        if (hour >= 17 && hour <= 21) return "Low";
        return "Medium";
    }
}

class ResidentialZone extends ParkingZone {
    ResidentialZone() { super("Residential"); }

    @Override
    public String predict(int hour, boolean event) {
        if (event) return "Medium";
        if (hour >= 22 || hour <= 6) return "High";
        return "Medium";
    }
}

// ================== ENGINE ==================
class PredictionEngine {
    public ParkingZone getZone(int choice) {
        switch (choice) {
            case 1: return new OfficeZone();
            case 2: return new MarketZone();
            case 3: return new ResidentialZone();
            default: return null;
        }
    }
}

// ================== ENCAPSULATION ==================
class Result {
    private String status;
    private String message;

    Result(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() { return status; }
    public String getMessage() { return message; }

    public void display() {
        System.out.println("Result: " + status + " | " + message);
    }
}

class Booking {
    private int bookingId;
    private String time;
    private String event;

    Booking(int id, String time, String event) {
        this.bookingId = id;
        this.time = time;
        this.event = event;
    }

    public void createBooking() {
        System.out.println("Booking Created");
    }

    public void cancelBooking() {
        System.out.println("Booking Cancelled");
    }
}

class ParkingSlot {
    private int slotId;
    private String zone;
    private boolean isAvailable;

    ParkingSlot(int id, String zone, boolean available) {
        this.slotId = id;
        this.zone = zone;
        this.isAvailable = available;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public String getZone() {
        return zone;
    }

    public void allocate() {
        isAvailable = false;
    }
}

class Record {
    private int id;
    private String details;

    Record(int id, String details) {
        this.id = id;
        this.details = details;
    }

    public void show() {
        System.out.println(details);
    }
}

// ================== USER ==================
class User {
    private int userId;
    private String name;

    User(int id, String name) {
        this.userId = id;
        this.name = name;
    }

    public void performActions() {
        System.out.println("User inputs processed...");
    }

    public void viewResult(Result result) {
        System.out.println("Final Output: " + result.getStatus() + " | " + result.getMessage());
    }

    public void viewParkingInfo(String zone, boolean available) {
        System.out.println("Zone: " + zone + " | Status: " + (available ? "Available" : "Full"));
    }
}

// ================== ADMIN ==================
class Admin {
    private int adminId;
    private String name;

    Admin(int id, String name) {
        this.adminId = id;
        this.name = name;
    }

    public void updateData() {
        System.out.println("Admin updated system data");
    }

    public void manageSystem() {
        System.out.println("System managed");
    }

    public void viewRecords(List<Record> records) {
        System.out.println("----- Records -----");
        for (Record r : records) {
            r.show();
        }
    }
}

// ================== SYSTEM ==================
class ParkingSystem {
    private String systemName;

    ParkingSystem(String name) {
        this.systemName = name;
    }

    public boolean checkAvailability(String prediction) {
        return !prediction.equals("Low"); // decision step
    }

    public Result processBooking(Booking booking, ParkingSlot slot, String prediction) {

        if (slot.isAvailable()) {
            booking.createBooking(); // ✔ only when available
            slot.allocate();
            return new Result("Available",
                    "Parking booked in " + slot.getZone() + " | Prediction: " + prediction);
        } else {
            booking.cancelBooking();
            return new Result("Not Available",
                    "No parking in " + slot.getZone() + " | Prediction: " + prediction);
        }
    }
}

// ================== MAIN ==================
public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        User user = new User(1, "User");
        Admin admin = new Admin(101, "Admin");
        PredictionEngine engine = new PredictionEngine();
        ParkingSystem system = new ParkingSystem("Smart Parking");

        List<Record> records = new ArrayList<>();

        int choice;

        do {
            System.out.println("\n1. Office\n2. Market\n3. Residential\n4. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();

            if (choice == 4) break;

            ParkingZone zone = engine.getZone(choice);
            if (zone == null) {
                System.out.println("Invalid choice");
                continue;
            }

            System.out.print("Enter hour (0-23): ");
            int hour = sc.nextInt();

            System.out.print("Event? (1-Yes / 0-No): ");
            boolean event = sc.nextInt() == 1;

            user.performActions();

            // STEP 1: Prediction
            String prediction = zone.predict(hour, event);

            // STEP 2: Decision (matches diagram)
            boolean available = system.checkAvailability(prediction);

            ParkingSlot slot = new ParkingSlot(1, zone.getName(), available);

            // STEP 3: Show availability
            user.viewParkingInfo(zone.getName(), available);

            // STEP 4: Booking (only if available)
            Booking booking = new Booking(1, String.valueOf(hour), event ? "Yes" : "No");
            Result result = system.processBooking(booking, slot, prediction);

            // STEP 5: Output
            result.display();
            user.viewResult(result);

            // STEP 6: Save record
            records.add(new Record(records.size() + 1,
                    "Zone: " + zone.getName() +
                            ", Hour: " + hour +
                            ", Event: " + event +
                            ", Result: " + result.getStatus()));

            // ADMIN
            System.out.println("\n--- ADMIN ---");
            admin.updateData();
            admin.manageSystem();
            admin.viewRecords(records);

        } while (true);

        sc.close();
    }
}