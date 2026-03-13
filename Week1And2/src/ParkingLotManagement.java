import java.util.*;

class ParkingSpot {

    String licensePlate;
    long entryTime;
    boolean occupied;

    ParkingSpot() {
        licensePlate = null;
        entryTime = 0;
        occupied = false;
    }
}

public class ParkingLotManagement {

    private ParkingSpot[] table;
    private int capacity;
    private int occupiedSpots;
    private int totalProbes;

    public ParkingLotManagement(int capacity) {
        this.capacity = capacity;
        this.table = new ParkingSpot[capacity];

        for (int i = 0; i < capacity; i++)
            table[i] = new ParkingSpot();

        occupiedSpots = 0;
        totalProbes = 0;
    }

    // Hash function
    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % capacity;
    }

    // Park vehicle
    public void parkVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (table[index].occupied) {
            index = (index + 1) % capacity;
            probes++;
        }

        table[index].licensePlate = plate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].occupied = true;

        occupiedSpots++;
        totalProbes += probes;

        System.out.println(
                "parkVehicle(\"" + plate + "\") → Assigned spot #" +
                        index + " (" + probes + " probes)"
        );
    }

    // Exit vehicle
    public void exitVehicle(String plate) {

        int index = hash(plate);

        while (table[index].occupied) {

            if (table[index].licensePlate.equals(plate)) {

                long durationMillis =
                        System.currentTimeMillis() - table[index].entryTime;

                double hours = durationMillis / (1000.0 * 60 * 60);

                double fee = Math.ceil(hours * 5); // $5/hour

                table[index].occupied = false;
                table[index].licensePlate = null;

                occupiedSpots--;

                System.out.println(
                        "exitVehicle(\"" + plate + "\") → Spot #" +
                                index + " freed, Duration: " +
                                String.format("%.2f", hours) +
                                "h, Fee: $" + fee
                );

                return;
            }

            index = (index + 1) % capacity;
        }

        System.out.println("Vehicle not found");
    }

    // Find nearest available spot
    public int findNearestSpot() {

        for (int i = 0; i < capacity; i++) {
            if (!table[i].occupied)
                return i;
        }

        return -1;
    }

    // Parking statistics
    public void getStatistics() {

        double occupancy =
                (occupiedSpots * 100.0) / capacity;

        double avgProbes =
                occupiedSpots == 0 ? 0 :
                        (double) totalProbes / occupiedSpots;

        System.out.println(
                "Occupancy: " +
                        String.format("%.2f", occupancy) + "%");

        System.out.println(
                "Avg Probes: " +
                        String.format("%.2f", avgProbes));

        System.out.println(
                "Nearest Available Spot: #" +
                        findNearestSpot());
    }

    public static void main(String[] args) {

        ParkingLotManagement parking =
                new ParkingLotManagement(500);

        parking.parkVehicle("ABC-1234");
        parking.parkVehicle("ABC-1235");
        parking.parkVehicle("XYZ-9999");

        parking.exitVehicle("ABC-1234");

        parking.getStatistics();
    }
}