public class Order {
    private int orderId;
    private int userId;
    private int tradingAccountId;
    private String stockName;
    int quantity;  // package-private so MarketPlace can modify
    private double price;
    private boolean isBuy;
    private String status;  // OPEN, PARTIAL, FILLED, CANCELLED
    private long timestamp;

    public Order(int orderId, int userId, int tradingAccountId, String stockName, int quantity, double price, boolean isBuy) {
        this.orderId = orderId;
        this.userId = userId;
        this.tradingAccountId = tradingAccountId;
        this.stockName = stockName;
        this.quantity = quantity;
        this.price = price;
        this.isBuy = isBuy;
        this.status = "OPEN";
        this.timestamp = System.currentTimeMillis();
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

    @Override
    public String toString() {
        String type = isBuy ? "BUY" : "SELL";
        return "Order{id=" + orderId + ", " + type + ", " + stockName + ", qty=" + quantity + ", price=" + price + ", status=" + status + "}";
    }
}