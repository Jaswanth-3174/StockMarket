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

        TreeSet<Order> buys = buyBook.get(stock);
        TreeSet<Order> sells = sellBook.get(stock);

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

            buy.quantity -= qty;
            sell.quantity -= qty;

            if (buy.quantity == 0) {
                buy.setStatus("FILLED");
                buys.remove(buy);
            } else {
                buy.setStatus("PARTIAL");
            }

            if (sell.quantity == 0) {
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
}