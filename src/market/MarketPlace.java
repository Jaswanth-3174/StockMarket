package market;

import account.*;
import trading.*;
import util.*;

import java.util.*;

public class MarketPlace {

    private Map<String, TreeSet<Order>> buyBook;
    private Map<String, TreeSet<Order>> sellBook;

    private Map<Integer, TradingAccount> tradingAccounts;
    private Map<Integer, DematAccount> dematAccounts;
    private Map<Integer, User> users;
    private List<Transaction> transactions;
    private Map<Integer, Order> ordersById;

    public MarketPlace() {
        this.buyBook = new HashMap<>();
        this.sellBook = new HashMap<>();
    }

    public void setReferences(Map<Integer, TradingAccount> tradingAccounts,
                              Map<Integer, DematAccount> dematAccounts,
                              Map<Integer, User> users,
                              List<Transaction> transactions,
                              Map<Integer, Order> ordersById) {
        this.tradingAccounts = tradingAccounts;
        this.dematAccounts = dematAccounts;
        this.users = users;
        this.transactions = transactions;
        this.ordersById = ordersById;
    }

    public void removeOrder(Order order) {
        String stock = order.getStockName();
        if (order.isBuy()) {
            TreeSet<Order> buys = buyBook.get(stock);
            if (buys != null) {
                buys.remove(order);
            }
        } else {
            TreeSet<Order> sells = sellBook.get(stock);
            if (sells != null) {
                sells.remove(order);
            }
        }
    }

    public void addBuyOrder(Order o) {
        if (!buyBook.containsKey(o.getStockName())) {
            TreeSet<Order> set = new TreeSet<>((a, b) -> {
                if (b.getPrice() != a.getPrice()) {
                    return Double.compare(b.getPrice(), a.getPrice());
                }
                return Integer.compare(a.getOrderId(), b.getOrderId());
            });
            buyBook.put(o.getStockName(), set);
        }
        buyBook.get(o.getStockName()).add(o);
        
        ordersById.put(o.getOrderId(), o);
        TradingAccount trade = o.getTradingAccount();
        if (trade != null) {
            trade.addOrder(o.getOrderId());
        }
        
        System.out.println("BUY order added: " + o.getOrderId());

        autoMatch(o.getStockName());
    }

    public void addSellOrder(Order o) {
        if (!sellBook.containsKey(o.getStockName())) {
            TreeSet<Order> set = new TreeSet<>((a, b) -> {
                if (a.getPrice() != b.getPrice()) {
                    return Double.compare(a.getPrice(), b.getPrice());
                }
                return Integer.compare(a.getOrderId(), b.getOrderId());
            });
            sellBook.put(o.getStockName(), set);
        }
        sellBook.get(o.getStockName()).add(o);
        
        ordersById.put(o.getOrderId(), o);
        TradingAccount trade = o.getTradingAccount();
        if (trade != null) {
            trade.addOrder(o.getOrderId());
        }
        
        System.out.println("SELL order added: " + o.getOrderId());

        autoMatch(o.getStockName());
    }

    private void autoMatch(String stock) {
        if (tradingAccounts == null || dematAccounts == null || users == null || transactions == null) {
            return;
        }

        TreeSet<Order> buys = buyBook.get(stock);   // desc
        TreeSet<Order> sells = sellBook.get(stock); // asc

        if (buys == null || sells == null || buys.isEmpty() || sells.isEmpty()) {
            return;
        }

        while (!buys.isEmpty() && !sells.isEmpty()) {
            Order buy = null;
            Order sell = null;

            Order bestBuy = buys.first(); // since sorted
            Order bestSell = sells.first();

            if (bestBuy.getPrice() < bestSell.getPrice()) { // no match possible
                break;
            }

            if (bestBuy.getUser() != bestSell.getUser()) { // different users, match found
                buy = bestBuy;
                sell = bestSell;
            }
            else { // same user
                // searching for alternative sell for bestBuy
                for (Order s : sells) {
                    if (s.getUser() != bestBuy.getUser() && bestBuy.getPrice() >= s.getPrice()) {
                        sell = s;
                        buy = bestBuy;
                        break;
                    }
                }
                // searching for alternative buy for bestSell
                if (sell == null) {
                    for (Order b : buys) {
                        if (b.getUser() != bestSell.getUser() && b.getPrice() >= bestSell.getPrice()) {
                            buy = b;
                            sell = bestSell;
                            break;
                        }
                    }
                }
            }

            // No valid match found
            if (buy == null || sell == null) {
                break;
            }

            int qty = Math.min(buy.getQuantity(), sell.getQuantity());
            double stockPrice = sell.getPrice();
            double totalPaid = qty * stockPrice;
            double buyerReserved = qty * buy.getPrice();
            double refund = buyerReserved - totalPaid;

            User buyerUser = buy.getUser();
            User sellerUser = sell.getUser();
            TradingAccount buyerTrade = buyerUser.getTradingAccount();
            TradingAccount sellerTrade = sellerUser.getTradingAccount();
            DematAccount buyerDemat = buyerUser.getDematAccount();
            DematAccount sellerDemat = sellerUser.getDematAccount();

            sellerDemat.sellShares(stock, qty);
            buyerTrade.debit(totalPaid);
            sellerTrade.credit(totalPaid);
            buyerDemat.addShares(stock, qty);

            if (refund > 0) {
                buyerTrade.releaseReservedBalance(refund);
            }

            Transaction t = new Transaction(buyerUser, sellerUser, stock, qty, stockPrice, totalPaid);
            transactions.add(t);

            System.out.println("\n+----- ORDER MATCHED -----+");
            System.out.println("Stock          : " + stock);
            System.out.println("Quantity       : " + qty);
            System.out.println("Price(1 stock) : Rs." + stockPrice);
            System.out.println("Total Amount   : Rs." + totalPaid);
            System.out.println("Buyer ID       : " + buyerUser.getUserId() + " (" + buyerUser.getUserName() + ")");
            System.out.println("Seller ID      : " + sellerUser.getUserId() + " (" + sellerUser.getUserName() + ")");
            System.out.println();

            buy.setQuantity(buy.getQuantity() - qty);
            sell.setQuantity(sell.getQuantity() - qty);

            if (buy.getQuantity() == 0) {
                buy.setStatus("FILLED");
                buys.remove(buy);
            } else {
                buy.setStatus("PARTIAL");
            }

            if (sell.getQuantity() == 0) {
                sell.setStatus("FILLED");
                sells.remove(sell);
            } else {
                sell.setStatus("PARTIAL");
            }
        }
    }

    public void printBook(String stock) {
        System.out.println("\n=== ORDER BOOK: " + stock + " ===");
        System.out.println("SELL:");
        TreeSet<Order> sells = sellBook.get(stock);
        if (sells == null || sells.isEmpty()) {
            System.out.println("  (empty)");
        } else {
            for (Order o : sells) {
                System.out.println("  id=" + o.getOrderId() + " qty=" + o.getQuantity() + " price=" + o.getPrice());
            }
        }
        System.out.println("BUY:");
        TreeSet<Order> buys = buyBook.get(stock);
        if (buys == null || buys.isEmpty()) {
            System.out.println("  (empty)");
        } else {
            for (Order o : buys) {
                System.out.println("  id=" + o.getOrderId() + " qty=" + o.getQuantity() + " price=" + o.getPrice());
            }
        }
    }

    public boolean modifyOrder(User user, int orderId, int newQuantity, double newPrice) {
        Order order = ordersById.get(orderId);
        
        if (order == null) {
            System.out.println("trading.Order #" + orderId + " not found.");
            return false;
        }
        
        // Check if this user owns the order
        if (order.getUser() != user) {
            System.out.println("You can only modify your own orders.");
            return false;
        }
        
        // Can only modify OPEN or PARTIAL orders
        if (order.getStatus().equals("FILLED") || order.getStatus().equals("CANCELLED")) {
            System.out.println("Cannot modify order with status: " + order.getStatus());
            return false;
        }
        
        // Validate new quantity (must be positive and >= filled quantity for partial orders)
        int filledQty = order.getFilledQuantity();
        if (newQuantity <= 0) {
            System.out.println("New quantity must be positive.");
            return false;
        }
        if (newQuantity < filledQty) {
            System.out.println("New quantity (" + newQuantity + ") cannot be less than already filled quantity (" + filledQty + ").");
            return false;
        }
        
        // Validate new price
        if (newPrice <= 0) {
            System.out.println("New price must be positive.");
            return false;
        }
        
        String stock = order.getStockName();
        int oldRemainingQty = order.getQuantity();
        double oldPrice = order.getPrice();
        int newRemainingQty = newQuantity - filledQty;
        
        // Remove from tree before modifying (TreeSet uses comparator with price)
        removeOrder(order);
        
        if (order.isBuy()) {
            TradingAccount ta = order.getTradingAccount();
            double oldReserved = oldRemainingQty * oldPrice;
            double newReserved = newRemainingQty * newPrice;
            
            // Release old reservation
            ta.releaseReservedBalance(oldReserved);
            
            // Check if user has enough balance for new reservation
            if (ta.getBalance() < newReserved) {
                // Rollback - re-reserve old amount and re-add to tree
                ta.reserveBalance(oldReserved);
                reAddOrder(order);
                System.out.println("Insufficient balance. Need Rs." + newReserved + ", available Rs." + ta.getBalance());
                return false;
            }
            
            // Reserve new amount
            ta.reserveBalance(newReserved);
            
        } else {
            DematAccount da = order.getDematAccount();
            
            // Release old reserved stocks
            da.releaseReservedStocks(stock, oldRemainingQty);
            
            // Check if user has enough stocks for new reservation
            int availableStocks = da.getAvailableQuantity(stock);
            if (availableStocks < newRemainingQty) {
                // Rollback - re-reserve old stocks and re-add to tree
                da.reserveStocks(stock, oldRemainingQty);
                reAddOrder(order);
                System.out.println("Insufficient stocks. Need " + newRemainingQty + ", available " + availableStocks);
                return false;
            }
            
            // Reserve new stocks
            da.reserveStocks(stock, newRemainingQty);
        }
        
        // Update order
        order.setOriginalQuantity(newQuantity);
        order.setQuantity(newRemainingQty);
        order.setPrice(newPrice);
        
        // Update status based on new quantity
        if (filledQty > 0) {
            order.setStatus("PARTIAL");
        } else {
            order.setStatus("OPEN");
        }
        
        // Re-add to tree with new price
        reAddOrder(order);
        
        System.out.println("trading.Order #" + orderId + " modified successfully.");
        System.out.println("New quantity: " + newQuantity + " (remaining: " + newRemainingQty + "), New price: Rs." + newPrice);
        
        // Trigger autoMatch as price/quantity changed
        autoMatch(stock);
        
        return true;
    }
    
    private void reAddOrder(Order order) {
        if (order.isBuy()) {
            if (!buyBook.containsKey(order.getStockName())) {
                TreeSet<Order> set = new TreeSet<>((a, b) -> {
                    if (b.getPrice() != a.getPrice()) {
                        return Double.compare(b.getPrice(), a.getPrice());
                    }
                    return Integer.compare(a.getOrderId(), b.getOrderId());
                });
                buyBook.put(order.getStockName(), set);
            }
            buyBook.get(order.getStockName()).add(order);
        } else {
            if (!sellBook.containsKey(order.getStockName())) {
                TreeSet<Order> set = new TreeSet<>((a, b) -> {
                    if (a.getPrice() != b.getPrice()) {
                        return Double.compare(a.getPrice(), b.getPrice());
                    }
                    return Integer.compare(a.getOrderId(), b.getOrderId());
                });
                sellBook.put(order.getStockName(), set);
            }
            sellBook.get(order.getStockName()).add(order);
        }
    }
    
    public Order getOrderById(int orderId) {
        return ordersById.get(orderId);
    }
}