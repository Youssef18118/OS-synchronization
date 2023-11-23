import java.util.Queue;
import java.util.LinkedList;
import java.util.Scanner;

class semaphore {
    protected int value = 0;

    protected semaphore() {
        value = 0;
    }

    protected semaphore(int initial) {
        value = initial;
    }

    public synchronized void P() {
        value--;
        if (value < 0) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
    }

    public synchronized void V() {
        value++;
        if (value <= 0) {
            notify();
        }
    }
}

class Router {
    private int maxConnections;
    private semaphore semaphore;
    private Queue<String> connections;

    public Router(int maxConnections) {
        this.maxConnections = maxConnections;
        this.semaphore = new semaphore(maxConnections);
        this.connections = new LinkedList<>();
    }

    public void connect(String deviceName) throws InterruptedException {
        semaphore.P();
        connections.add(deviceName);
        System.out.println("- Connection " + connections.size() + ": " + deviceName + " login");
    }

    public void performOnlineActivity(String deviceName) throws InterruptedException {
        System.out.println("- Connection " + connections.size() + ": " + deviceName + " performs online activity");
        Thread.sleep((long) (Math.random() * 1000)); // Simulate online activity with a random waiting time
    }

    public void disconnect(String deviceName) throws InterruptedException {
        connections.remove(deviceName);
        System.out.println("- Connection " + connections.size() + ": " + deviceName + " Logged out");
        semaphore.V();
    }
}

class Device extends Thread {
    private String name;
    private String type;
    private Router router;

    public Device(String name, String type, Router router) {
        this.name = name;
        this.type = type;
        this.router = router;
    }

    @Override
    public void run() {
        try {
            router.connect("(" + name + ")(" + type + ")arrived");
            router.performOnlineActivity(name);
            router.disconnect(name);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Network {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Get user input for the number of WI-FI connections (N)
        System.out.println("What is the number of WI-FI Connections?");
        int maxConnections = scanner.nextInt();

        // Get user input for the total number of devices (TC)
        System.out.println("What is the number of devices Clients want to connect?");
        int totalDevices = scanner.nextInt();

        // Create a router with the specified maximum connections
        Router router = new Router(maxConnections);

        // Collect input for each device's name and type
        Device[] devices = new Device[totalDevices];
        for (int i = 0; i < totalDevices; i++) {
            System.out.println("Enter the details for device " + (i + 1) + ":");
            String name = scanner.next();
            String type = scanner.next();

            // Create the device (without starting it)
            devices[i] = new Device(name, type, router);
        }

        // Close the scanner
        scanner.close();

        // Start the devices after collecting all input
        for (Device device : devices) {
            device.start();
        }
    }
}