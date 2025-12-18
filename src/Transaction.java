import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

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

    public int getTransactionId() {
        return transactionId;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public int getSellerId() {
        return sellerId;
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

    // Print row for all transactions view (shows buyer and seller info)
    public void printRow(Map<Integer, User> users) {
        String buyerName = users.containsKey(buyerId) ? users.get(buyerId).getUserName() : "User#" + buyerId;
        String sellerName = users.containsKey(sellerId) ? users.get(sellerId).getUserName() : "User#" + sellerId;
        System.out.printf("| %-8d | %-8s | %-5d | %-10.2f | %-12.2f | %-12s | %-12s | %-10s |%n",
                transactionId, stockName, quantity, price, total, buyerName, sellerName, getFormattedTime());
    }

    // Print row for user-specific view
    public void printRow(int userId, Map<Integer, User> users) {
        String type;
        String counterpartyName;
        String counterpartyLabel;
        
        if (userId == buyerId) {
            type = "BUY";
            counterpartyLabel = "From";
            counterpartyName = users.containsKey(sellerId) ? users.get(sellerId).getUserName() : "User#" + sellerId;
        } else {
            type = "SELL";
            counterpartyLabel = "To";
            counterpartyName = users.containsKey(buyerId) ? users.get(buyerId).getUserName() : "User#" + buyerId;
        }
        
        System.out.printf("| %-8d | %-6s | %-8s | %-5d | %-10.2f | %-12.2f | %-6s %-12s | %-10s |%n",
                transactionId, type, stockName, quantity, price, total, counterpartyLabel, counterpartyName, getFormattedTime());
    }

    // Static method to print table header for all transactions
    public static void printTableHeader() {
        System.out.println("+----------+----------+-------+------------+--------------+--------------+--------------+------------+");
        System.out.printf("| %-8s | %-8s | %-5s | %-10s | %-12s | %-12s | %-12s | %-10s |%n",
                "Trans ID", "Stock", "Qty", "Price", "Total", "Buyer", "Seller", "Time");
        System.out.println("+----------+----------+-------+------------+--------------+--------------+--------------+------------+");
    }

    // Static method to print table header for user-specific transactions
    public static void printUserTableHeader() {
        System.out.println("+----------+--------+----------+-------+------------+--------------+---------------------+------------+");
        System.out.printf("| %-8s | %-6s | %-8s | %-5s | %-10s | %-12s | %-19s | %-10s |%n",
                "Trans ID", "Type", "Stock", "Qty", "Price", "Total", "Counterparty", "Time");
        System.out.println("+----------+--------+----------+-------+------------+--------------+---------------------+------------+");
    }

    // Static method to print table footer
    public static void printTableFooter() {
        System.out.println("+----------+----------+-------+------------+--------------+--------------+--------------+------------+");
    }

    // Static method to print user table footer
    public static void printUserTableFooter() {
        System.out.println("+----------+--------+----------+-------+------------+--------------+---------------------+------------+");
    }

    @Override
    public String toString() {
        return "Transaction #" + transactionId + " | " + stockName + " | Qty: " + quantity + 
               " | Price: " + price + " | Total: " + total + " | Time: " + getFormattedTime();
    }
}