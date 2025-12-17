public class Order {
    private static int idCounter = 1;
    
    private int orderId;
    private int userId;
    private int tradingAccountId;
    private String stockName;
    private int originalQuantity;  // Original quantity when order was placed
    int quantity;  // Remaining quantity
    private double price;
    private boolean isBuy;
    private String status;  // OPEN, PARTIAL, FILLED, CANCELLED
    private long timestamp;

    public Order(int userId, int tradingAccountId, String stockName, int quantity, double price, boolean isBuy) {
        this.orderId = idCounter++;
        this.userId = userId;
        this.tradingAccountId = tradingAccountId;
        this.stockName = stockName;
        this.originalQuantity = quantity;
        this.quantity = quantity;
        this.price = price;
        this.isBuy = isBuy;
        this.status = "OPEN";
        this.timestamp = System.currentTimeMillis();
    }

    public static int getIdCounter() {
        return idCounter;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getUserId() {
        return userId;
    }

    public int getTradingAccountId() {
        return tradingAccountId;
    }

    public String getStockName() {
        return stockName;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getOriginalQuantity() {
        return originalQuantity;
    }

    public int getFilledQuantity() {
        return originalQuantity - quantity;
    }

    public double getPrice() {
        return price;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Print row for all orders view (no user context)
    public void printRow() {
        String type = isBuy ? "BUY" : "SELL";
        double total = quantity * price;
        System.out.printf("| %-8d | %-6s | %-8s | %-8d | %-8d | %-8d | %-10.2f | %-12.2f | %-10s |%n",
                orderId, type, stockName, originalQuantity, getFilledQuantity(), quantity, 
                price, total, status);
    }

    // Print row for user-specific view
    public void printRow(int userId) {
        String type = isBuy ? "BUY" : "SELL";
        double total = quantity * price;
        System.out.printf("| %-8d | %-6s | %-8s | %-8d | %-8d | %-8d | %-10.2f | %-12.2f | %-10s |%n",
                orderId, type, stockName, originalQuantity, getFilledQuantity(), quantity, 
                price, total, status);
    }

    // Static method to print table header
    public static void printTableHeader() {
        System.out.println("+----------+--------+----------+----------+----------+----------+------------+--------------+------------+");
        System.out.printf("| %-8s | %-6s | %-8s | %-8s | %-8s | %-8s | %-10s | %-12s | %-10s |%n",
                "Order ID", "Type", "Stock", "Original", "Filled", "Remaining", "Price", "Total", "Status");
        System.out.println("+----------+--------+----------+----------+----------+----------+------------+--------------+------------+");
    }

    // Static method to print table footer
    public static void printTableFooter() {
        System.out.println("+----------+--------+----------+----------+----------+----------+------------+--------------+------------+");
    }

    @Override
    public String toString() {
        String type = isBuy ? "BUY" : "SELL";
        return "Order{id=" + orderId + ", " + type + ", " + stockName + ", qty=" + quantity + ", price=" + price + ", status=" + status + "}";
    }
}