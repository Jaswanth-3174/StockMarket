import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {
    private static int idCounter = 1;
    
    private int transactionId;
    private int buyerId;
    private int sellerId;
    private int buyerTradingId;
    private int sellerTradingId;
    private String stockName;
    private int quantity;
    private double price;
    private double total;
    private long timeStamp;

    public Transaction(int buyerId, int sellerId, int buyerTradingId, int sellerTradingId, String stockName, int quantity, double price, double total){
        this.transactionId = idCounter++;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.buyerTradingId = buyerTradingId;
        this.sellerTradingId = sellerTradingId;
        this.stockName = stockName;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.timeStamp = System.currentTimeMillis();
    }

    public static int getIdCounter() {
        return idCounter;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public int getSellerId() {
        return sellerId;
    }

    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(timeStamp));
    }

    public void printRow(String type) {
        if (type.isEmpty()) {
            System.out.printf("| %-8d | %-8s | %-8d | %-10.2f | %-12.2f | %-10s |%n",
                    transactionId, stockName, quantity, price, total, getFormattedTime());
        } else {

            System.out.printf("| %-8d | %-6s | %-8s | %-8d | %-10.2f | %-12.2f | %-10s |%n",
                    transactionId, type, stockName, quantity, price, total, getFormattedTime());
        }
    }

    public void printRowForUser(int userId) {
        String type = (userId == buyerId) ? "BUY" : "SELL";
        System.out.printf("| %-8d | %-6s | %-8s | %-8d | %-10.2f | %-12.2f | %-10s |%n",
                transactionId, type, stockName, quantity, price, total, getFormattedTime());
    }

    @Override
    public String toString() {
        return "Transaction #" + transactionId + " | " + stockName + " | Qty: " + quantity + 
               " | Price: " + price + " | Total: " + total + " | Time: " + getFormattedTime();
    }
}