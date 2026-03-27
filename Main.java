
import java.util.*;

abstract class Room {
    private int beds;
    private double size;
    private double price;

    public Room(int beds, double size, double price) {
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public int getBeds() { return beds; }
    public double getSize() { return size; }
    public double getPrice() { return price; }

    public abstract String getRoomType();

    public void displayDetails() {
        System.out.println("Room Type: " + getRoomType());
        System.out.println("Beds: " + beds);
        System.out.println("Size: " + size + " sq.ft");
        System.out.println("Price: $" + price);
    }
}

class SingleRoom extends Room {
    public SingleRoom() { super(1, 200, 100); }
    public String getRoomType() { return "Single Room"; }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super(2, 350, 180); }
    public String getRoomType() { return "Double Room"; }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super(3, 600, 300); }
    public String getRoomType() { return "Suite Room"; }
}


class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public void updateAvailability(String type, int count) {
        inventory.put(type, count);
    }

    public void displayInventory() {
        System.out.println(" Inventory");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }
    }
}

class SearchService {
    public void search(RoomInventory inventory, Room[] rooms) {
        System.out.println("\nAvailable Rooms");

        for (Room room : rooms) {
            int available = inventory.getAvailability(room.getRoomType());

            if (available > 0) {
                room.displayDetails();
                System.out.println("Available: " + available);
            }
        }
    }
}

class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}


class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void add(Reservation r) {
        queue.offer(r);
        System.out.println("Request added: " + r.getGuestName());
    }

    public Reservation next() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

class BookingService {

    private RoomInventory inventory;
    private Map<String, Set<String>> allocated = new HashMap<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void process(BookingQueue queue) {

        System.out.println("\nProcessing Bookings ");

        while (!queue.isEmpty()) {

            Reservation r = queue.next();
            String type = r.getRoomType();
            int available = inventory.getAvailability(type);

            if (available > 0) {

                String roomId = type.substring(0, 2).toUpperCase()
                        + "-" + UUID.randomUUID().toString().substring(0, 5);

                allocated.putIfAbsent(type, new HashSet<>());
                allocated.get(type).add(roomId);

                inventory.updateAvailability(type, available - 1);

                System.out.println("CONFIRMED");
                System.out.println("Guest: " + r.getGuestName());
                System.out.println("Room: " + type);
                System.out.println("ID: " + roomId);
            } else {
                System.out.println("FAILED for " + r.getGuestName() + " (No availability)");
            }
        }
    }
}

public class Main {

    public static void main(String[] args) {

        System.out.println("Welcome to Hotel Booking System");
        System.out.println("Version: 1.0");

        Room[] rooms = {
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        };

        RoomInventory inventory = new RoomInventory();

        SearchService search = new SearchService();
        search.search(inventory, rooms);


        BookingQueue queue = new BookingQueue();
        queue.add(new Reservation("Alice", "Single Room"));
        queue.add(new Reservation("Bob", "Double Room"));
        queue.add(new Reservation("Charlie", "Suite Room"));
        queue.add(new Reservation("David", "Suite Room"));


        BookingService bookingService = new BookingService(inventory);
        bookingService.process(queue);


        System.out.println("\nFinal Inventory:");
        inventory.displayInventory();

        System.out.println("\nApplication Terminated.");
    }
}