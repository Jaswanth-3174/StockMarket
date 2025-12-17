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
        TradingAccount trade = tradingAccounts.get(o.getTradingAccountId());
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
        TradingAccount trade = tradingAccounts.get(o.getTradingAccountId());
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

        TreeSet<Order> buys = buyBook.get(stock);
        TreeSet<Order> sells = sellBook.get(stock);

        if (buys == null || sells == null || buys.isEmpty() || sells.isEmpty()) {
            return;
        }

        boolean matchFound = true;
        while (matchFound && !buys.isEmpty() && !sells.isEmpty()) {
            matchFound = false;
            Order buy = null;
            Order sell = null;
            
            for (Order b : buys) {
                for (Order s : sells) {
                    if (b.getUserId() == s.getUserId()) { // skips same user
                        continue;
                    }
                    if (b.getPrice() >= s.getPrice()) {
                        buy = b;
                        sell = s;
                        matchFound = true;
                        break;
                    }
                }
                if (matchFound) break;
            }
            
            if (!matchFound || buy == null || sell == null) {
                break;
            }

            int qty = Math.min(buy.getQuantity(), sell.getQuantity());
            double tradePrice = sell.getPrice();
            double totalPaid = qty * tradePrice;
            double buyerReserved = qty * buy.getPrice();
            double refund = buyerReserved - totalPaid;

            TradingAccount buyerTrade = tradingAccounts.get(buy.getTradingAccountId());
            TradingAccount sellerTrade = tradingAccounts.get(sell.getTradingAccountId());
            User buyerUser = users.get(buy.getUserId());
            User sellerUser = users.get(sell.getUserId());
            DematAccount buyerDemat = buyerUser.getDematAccount();
            DematAccount sellerDemat = sellerUser.getDematAccount();

            sellerDemat.sellShares(stock, qty);
            buyerTrade.debit(totalPaid);
            sellerTrade.credit(totalPaid);
            buyerDemat.addShares(stock, qty);

            if (refund > 0) {
                buyerTrade.releaseReservedBalance(refund);
            }

            transactions.add(new Transaction(
                buy.getUserId(), sell.getUserId(),
                buy.getTradingAccountId(), sell.getTradingAccountId(),
                stock, qty, tradePrice, totalPaid
            ));

            System.out.println("\n+----- ORDER MATCHED -----+");
            System.out.println("Stock          : " + stock);
            System.out.println("Quantity       : " + qty);
            System.out.println("Price(1 stock) : Rs." + tradePrice);
            System.out.println("Total Amount   : Rs." + totalPaid);
            System.out.println("Buyer ID       : " + buy.getUserId() + " (" + buyerUser.getUserName() + ")");
            System.out.println("Seller ID      : " + sell.getUserId() + " (" + sellerUser.getUserName() + ")");
            System.out.println();

            buys.remove(buy);
            sells.remove(sell);
            buy.quantity -= qty;
            sell.quantity -= qty;

            if (buy.quantity == 0) {
                buy.setStatus("FILLED");
            } else {
                buy.setStatus("PARTIAL");
                buys.add(buy);
            }
            if (sell.quantity == 0) {
                sell.setStatus("FILLED");
            } else {
                sell.setStatus("PARTIAL");
                sells.add(sell);
            }
        }
    }

//    public int match(String stock,
//                     Map<Integer, TradingAccount> tradingAccounts,
//                     Map<Integer, DematAccount> dematAccounts,
//                     Map<Integer, User> users,
//                     List<Transaction> transactions,
//                     int txnId) {
//
//        TreeSet<Order> buys = buyBook.get(stock);
//        TreeSet<Order> sells = sellBook.get(stock);
//
//        if (buys == null || sells == null || buys.isEmpty() || sells.isEmpty()) {
//            System.out.println("No orders to match for " + stock);
//            return txnId;
//        }
//
//        while (!buys.isEmpty() && !sells.isEmpty()) {
//            Order buy = buys.first();
//            Order sell = sells.first();
//
//            if (buy.getPrice() < sell.getPrice()) {
//                System.out.println("No price cross. BUY: " + buy.getPrice() + " < SELL: " + sell.getPrice());
//                break;
//            }
//
//            int qty = Math.min(buy.getQuantity(), sell.getQuantity());
//            double tradePrice = sell.getPrice();
//            double totalPaid = qty * tradePrice;
//            double buyerReserved = qty * buy.getPrice();
//            double refund = buyerReserved - totalPaid;
//
//            TradingAccount buyerTrade = tradingAccounts.get(buy.getTradingAccountId());
//            TradingAccount sellerTrade = tradingAccounts.get(sell.getTradingAccountId());
//            User buyerUser = users.get(buy.getUserId());
//            User sellerUser = users.get(sell.getUserId());
//            DematAccount buyerDemat = dematAccounts.get(buyerUser.getDematID());
//            DematAccount sellerDemat = dematAccounts.get(sellerUser.getDematID());
//
//            // Execute trade
//            sellerDemat.sellShares(stock, qty);
//            buyerTrade.debit(totalPaid);
//            sellerTrade.credit(totalPaid);
//            buyerDemat.addShares(stock, qty);
//
//            // Refund extra reserved amount to buyer
//            if (refund > 0) {
//                buyerTrade.releaseReservedBalance(refund);
//                System.out.println("Refunded to buyer: " + refund);
//            }
//
//            // Record transaction
//            transactions.add(new Transaction(
//                    txnId++,
//                    buy.getUserId(), sell.getUserId(),
//                    buy.getTradingAccountId(), sell.getTradingAccountId(),
//                    stock, qty, tradePrice, totalPaid
//            ));
//
//            System.out.println("MATCHED: BUY " + buy.getOrderId() + " vs SELL " + sell.getOrderId() +
//                    " | qty=" + qty + " @ " + tradePrice + " | Total=" + totalPaid);
//
//            // Update quantities
//            buys.remove(buy);
//            sells.remove(sell);
//            buy.quantity -= qty;
//            sell.quantity -= qty;
//            if (buy.quantity > 0) buys.add(buy);
//            if (sell.quantity > 0) sells.add(sell);
//        }
//        return txnId;
//    }

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
}