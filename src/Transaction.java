import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {
    private static int idCounter = 1;
    
    private int transactionId;
    private User buyer;
    private User seller;
    private String stockName;
    private int quantity;
    private double price;
    private double total;
    private long timeStamp;

    public Transaction(User buyer, User seller, String stockName, int quantity, double price, double total){
        this.transactionId = idCounter++;
        this.buyer = buyer;
        this.seller = seller;
        this.stockName = stockName;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.timeStamp = System.currentTimeMillis();
    }

    public static int getIdCounter() {
        return idCounter;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public User getBuyer() {
        return buyer;
    }

    public User getSeller() {
        return seller;
    }

    public int getBuyerId() {
        return buyer.getUserId();
    }

    public int getSellerId() {
        return seller.getUserId();
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

    public double getTotal() {
        return total;
    }

    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(timeStamp));
    }

    public void printRow() {
        System.out.printf("| %-8d | %-8s | %-5d | %-10.2f | %-12.2f | %-12s | %-12s | %-10s |%n",
                transactionId, stockName, quantity, price, total, 
                buyer.getUserName(), seller.getUserName(), getFormattedTime());
    }

    public void printRow(User user) {
        String type;
        String counterpartyName;
        String counterpartyLabel;
        
        if (user == buyer) {
            type = "BUY";
            counterpartyLabel = "From";
            counterpartyName = seller.getUserName();
        } else {
            type = "SELL";
            counterpartyLabel = "To";
            counterpartyName = buyer.getUserName();
        }
        
        System.out.printf("| %-8d | %-6s | %-8s | %-5d | %-10.2f | %-12.2f | %-6s %-12s | %-10s |%n",
                transactionId, type, stockName, quantity, price, total, counterpartyLabel, counterpartyName, getFormattedTime());
    }

    @Override
    public String toString() {
        return "Transaction #" + transactionId + " | " + stockName + " | Qty: " + quantity + 
               " | Price: " + price + " | Total: " + total + " | Time: " + getFormattedTime();
    }
}