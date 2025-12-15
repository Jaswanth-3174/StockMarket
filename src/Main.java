import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;
public class Main {

    static Validator validator;
    static HashMap<Integer, User> users;
    static HashMap<Integer, DematAccount> demats;
    static HashMap<Integer, TradingAccount> tradings;

    static ArrayList<Order> buyOrders, sellOrders;
    static HashMap<Integer, Order> ordersById;

    static ArrayList<Transaction> transactions;

    static int userId = 1, dematAccountId = 1, tradingAccountId = 1, transactionId = 1, orderId = 1;

    static ArrayList<String> stockSymbols;

    static MarketPlace marketPlace;

    static Scanner sc;
    static InputHandler inputHandler;

    static User promoter1, promoter2, promoter3;

    static {
        validator = new Validator();
        users = new HashMap<>();
        demats = new HashMap<>();
        tradings = new HashMap<>();
        buyOrders = new ArrayList<>();
        sellOrders = new ArrayList<>();
        ordersById = new HashMap<>();
        transactions = new ArrayList<>();
        sc = new Scanner(System.in);
        inputHandler = new InputHandler(sc);

        stockSymbols = new ArrayList<>();
        stockSymbols.add("TCS");
        stockSymbols.add("NIFTY");
        stockSymbols.add("SBI");
        stockSymbols.add("INFY");

        marketPlace = new MarketPlace();
        marketPlace.setReferences(tradings, demats, users, transactions, transactionId);

        // Promoter 1
        promoter1 = new User(userId++, "Ram", "Ab.11111", "AWSD12J", true);
        DematAccount dematAccount1 = getOrCreateDematByPAN(promoter1.getPanNumber());
        promoter1.setDematAccountId(dematAccount1.getDemandAccountId());
        users.put(promoter1.getUserId(), promoter1);
        TradingAccount tradingAccount1 = new TradingAccount(tradingAccountId++, promoter1.getUserId());
        tradings.put(tradingAccount1.getTradingAccountId(), tradingAccount1);
        promoter1.setTradingAccountId(tradingAccount1.getTradingAccountId());
        dematAccount1.addShares("TCS", 1000);
        dematAccount1.addShares("NIFTY", 1500);
        dematAccount1.addShares("SBI", 2000);
        dematAccount1.addShares("INFY", 1200);

        // Promoter1 places SELL
        DematAccount p1Demat = demats.get(promoter1.getDematID());
        int p1SellQty = 300;
        double p1SellPrice = 1500.5;
        if (p1Demat.reserveStocks("TCS", p1SellQty)) {
            Order p1SellOrder = new Order(orderId++, promoter1.getUserId(), promoter1.getTradingAccountId(), "TCS", p1SellQty, p1SellPrice, false);
            marketPlace.addSellOrder(p1SellOrder);
            ordersById.put(p1SellOrder.getOrderId(), p1SellOrder);
            tradings.get(p1SellOrder.getTradingAccountId()).addOrder(p1SellOrder.getOrderId());
            System.out.println("Promoter1 SELL placed: " + p1SellOrder.getOrderId());
        }
    }

    public static void main(String[] args) {
        System.out.println("---------- WELCOME TO TRADING ----------");
        while (true) {
            mainMenu();
            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    System.out.println("Exited!");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    static void mainMenu(){
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3, Exit\n");
        System.out.print("Enter your choice : ");
    }

    static void printUserMenu(User user) {
        System.out.println("\n--- USER MENU (" + user.getUserName() + ") ---");
        System.out.println("1. View Portfolio");
        System.out.println("2. Place BUY Order");
        System.out.println("3. Place SELL Order");
        System.out.println("4. View Order Book");
        System.out.println("5. View My Transactions");
        System.out.println("6. View All Transactions");
        System.out.println("7. Add money from Savings account");
        System.out.println("8. Logout");
        System.out.print("Enter choice: ");
    }

    static void register() {
        String name = inputHandler.getString("Enter username: ");
        String pass = inputHandler.getString("Enter password: ");
        String confirmPass = inputHandler.getString("Enter password again: ");
        if(!pass.equals(confirmPass)){
            System.out.println("Passwords don't match! Register again");
            return;
        }
        String pan = inputHandler.getString("Enter PAN number: ");

        User user = new User(userId++, name, pass, pan, false);
        users.put(user.getUserId(), user);

        // automatically linking demat by PAN
        DematAccount demat = getOrCreateDematByPAN(pan);
        user.setDematAccountId(demat.getDemandAccountId());

        // creating new trading account
        TradingAccount trade = new TradingAccount(tradingAccountId++, user.getUserId());
        tradings.put(trade.getTradingAccountId(), trade);
        user.setTradingAccountId(trade.getTradingAccountId());

        System.out.println("Registered! User ID: " + user.getUserId());
    }

    static void login() {
        int id = inputHandler.getInteger("Enter User ID: ");

        String pass = inputHandler.getString("Enter password: ");
        User user = users.get(id);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        if (!validator.validatePassword(pass)) {
            System.out.println("Wrong password.");
            return;
        }

        System.out.println("Login successful! Welcome " + user.getUserName());
        userMenu(user);
    }

    static void userMenu(User user) {
        while (true) {
            printUserMenu(user);
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    viewPortfolio(user);
                    break;
                case 2:
                    placeBuyOrder(user);
                    break;
                case 3:
                    placeSellOrder(user);
                    break;
                case 4:
                    viewOrderBook();
                    break;
                case 5:
                    viewMyTransactions(user);
                    break;
                case 6:
                    viewAllTransactions();
                    break;
                case 7:
                    addMoney(user);
                    break;
                case 8:
                    System.out.println("Logged out.");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    static void viewPortfolio(User user) {
        System.out.println("\n--- PORTFOLIO ---");

        // Demat holdings
        DematAccount demat = demats.get(user.getDematID());
        if (demat != null) {
            demat.getHoldings();
        } else {
            System.out.println("No Demat account.");
        }

        // Trading account balance
        TradingAccount ta = tradings.get(user.getTradingAccountId());
        if (ta != null) {
            ta.showBalances();
        }
    }

    static void placeBuyOrder(User user) {
        System.out.println("\nAvailable stocks: " + stockSymbols);
        System.out.print("Enter stock name: ");
        String stock = sc.nextLine().toUpperCase();

        if (!stockSymbols.contains(stock)) {
            System.out.println("Invalid stock.");
            return;
        }

        System.out.print("Enter quantity: ");
        int qty = sc.nextInt();
        System.out.print("Enter price: ");
        double price = sc.nextDouble();
        sc.nextLine();

        double total = qty * price;
        int tradeId = user.getTradingAccountId();
        TradingAccount trade = tradings.get(tradeId);

        if (!trade.reserveBalance(total)) {
            System.out.println("Insufficient balance.");
            return;
        }

        Order order = new Order(orderId++, user.getUserId(), tradeId, stock, qty, price, true);
        int originalQty = qty;
        marketPlace.addBuyOrder(order);
        ordersById.put(order.getOrderId(), order);
        trade.addOrder(order.getOrderId());

        // Show final status
        if (order.getStatus().equals("FILLED")) {
            System.out.println("\nOrder #" + order.getOrderId() + " completely filled!");
        } else if (order.getStatus().equals("PARTIAL")) {
            System.out.println("\nOrder #" + order.getOrderId() + " partially filled. Remaining: " + order.getQuantity() + " shares waiting.");
        } else {
            System.out.println("\nBUY order #" + order.getOrderId() + " placed. Waiting for matching sell order.");
        }
    }

    static void placeSellOrder(User user) {
        DematAccount demat = demats.get(user.getDematID());
        if (demat == null) {
            System.out.println("No Demat account.");
            return;
        }

        demat.getHoldings();
        System.out.print("Enter stock name: ");
        String stock = sc.nextLine().toUpperCase();

        System.out.print("Enter quantity: ");
        int qty = sc.nextInt();
        System.out.print("Enter price (per 1 stock): ");
        double price = sc.nextDouble();
        sc.nextLine();

        if (!demat.reserveStocks(stock, qty)) {
            System.out.println("Cannot reserve stocks.");
            return;
        }

        int tradeId = user.getTradingAccountId();
        TradingAccount trade = tradings.get(tradeId);

        Order order = new Order(orderId++, user.getUserId(), tradeId, stock, qty, price, false);
        marketPlace.addSellOrder(order);
        ordersById.put(order.getOrderId(), order);
        trade.addOrder(order.getOrderId());

        // Show final status
        if (order.getStatus().equals("FILLED")) {
            System.out.println("\nOrder #" + order.getOrderId() + " completely filled!");
        } else if (order.getStatus().equals("PARTIAL")) {
            System.out.println("\nOrder #" + order.getOrderId() + " partially filled. Remaining: " + order.getQuantity() + " shares waiting.");
        } else {
            System.out.println("\nSELL order #" + order.getOrderId() + " placed. Waiting for matching buy order.");
        }
    }

    static void viewOrderBook() {
        System.out.println("\n--- ORDER BOOKS ---");
        for (String stock : stockSymbols) {
            marketPlace.printBook(stock);
        }
    }

    static void viewMyTransactions(User user) {
        System.out.println("\n--- MY TRANSACTIONS ---");
        boolean found = false;
        
        System.out.println("+----------+--------+----------+----------+------------+--------------+------------+");
        System.out.printf("| %-8s | %-6s | %-8s | %-8s | %-14s | %-12s | %-10s |%n",
                "Trans ID", "Type", "Stock", "Qty", "Price(1 stock)", "Total", "Time");
        System.out.println("+----------+--------+----------+----------+------------+--------------+------------+");
        
        for (Transaction t : transactions) {
            if (t.getBuyerId() == user.getUserId() || t.getSellerId() == user.getUserId()) {
                t.printRowForUser(user.getUserId());
                found = true;
            }
        }
        
        System.out.println("+----------+--------+----------+----------+------------+--------------+------------+");
        
        if (!found) {
            System.out.println("No transactions yet.");
        }
    }

    static void viewAllTransactions() {
        System.out.println("\n--- ALL TRANSACTIONS ---");
        if (transactions.isEmpty()) {
            System.out.println("No transactions yet.");
        } else {
            System.out.println("+----------+----------+----------+------------+--------------+------------+");
            System.out.printf("| %-8s | %-8s | %-8s | %-14s | %-12s | %-10s |%n",
                    "Trans ID", "Stock", "Qty", "Price(1 stock)", "Total", "Time");
            System.out.println("+----------+----------+----------+------------+--------------+------------+");
            for (Transaction t : transactions) {
                t.printRow("");
            }
            System.out.println("+----------+----------+----------+------------+--------------+------------+");
        }
    }

    static void addMoney(User user){
        double amount = inputHandler.getDouble("Enter the amount to add : ");
        if(amount <= 0){
            System.out.println("Enter the amount from that 0.0");
            return;
        }
        int tradeId = user.getTradingAccountId();
        TradingAccount temp1 = tradings.get(tradeId);
        System.out.println("Added " + amount + " Successfully!");
        temp1.credit(amount);
    }

    static DematAccount getOrCreateDematByPAN(String pan) {
        for (DematAccount d : demats.values()) {
            if (d.getPanNumber().equals(pan)) {
                System.out.println("Linked existing Demat for PAN: " + pan);
                return d;
            }
        }
        DematAccount newDemat = new DematAccount(dematAccountId++, pan);
        demats.put(newDemat.getDemandAccountId(), newDemat);
        System.out.println("Created new Demat for PAN: " + pan);
        return newDemat;
    }
}