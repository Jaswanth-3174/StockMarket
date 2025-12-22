package trading;

import account.*;

public class Order {
    private static int idCounter = 1;
    
    private int orderId;
    private User user;
    private String stockName;
    private int originalQuantity;  // Original quantity when order was placed
    private int quantity;  // Remaining quantity
    private double price;
    private boolean isBuy;
    private String status;  // OPEN, PARTIAL, FILLED, CANCELLED
    private long timestamp;

    public Order(User user, String stockName, int quantity, double price, boolean isBuy) {
        this.orderId = idCounter++;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public int getUserId() {
        return user.getUserId();
    }

    public int getTradingAccountId() {
        return user.getTradingAccount().getTradingAccountId();
    }

    public TradingAccount getTradingAccount() {
        return user.getTradingAccount();
    }

    public DematAccount getDematAccount() {
        return user.getDematAccount();
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

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setOriginalQuantity(int originalQuantity) {
        this.originalQuantity = originalQuantity;
    }

    // all orders
    public void printRow() {
        String type = isBuy ? "BUY" : "SELL";
        double total = quantity * price;
        System.out.printf("| %-8d | %-6s | %-8s | %-8d | %-8d | %-8d | %-10.2f | %-12.2f | %-10s |%n",
                orderId, type, stockName, originalQuantity, getFilledQuantity(), quantity, 
                price, total, status);
    }

    // user specific
    public void printRow(User user) {
        String type = isBuy ? "BUY" : "SELL";
        double total = quantity * price;
        System.out.printf("| %-8d | %-6s | %-8s | %-8d | %-8d | %-8d | %-10.2f | %-12.2f | %-10s |%n",
                orderId, type, stockName, originalQuantity, getFilledQuantity(), quantity, 
                price, total, status);
    }

    @Override
    public String toString() {
        String type = isBuy ? "BUY" : "SELL";
        return "trading.Order{id=" + orderId + ", " + type + ", " + stockName + ", qty=" + quantity + ", price=" + price + ", status=" + status + "}";
    }
}