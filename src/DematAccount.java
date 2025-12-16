import java.util.HashMap;

public class DematAccount {
    private static int idCounter = 1;
    
    private int demandAccountId;
    private String panNumber;
    private String password;
    private HashMap<String, Integer> stockHoldings;
    private HashMap<String, Integer> reservedHoldings;

    public DematAccount(String panNumber, String password){
        this.demandAccountId = idCounter++;
        this.panNumber = panNumber;
        this.password = password;
        this.stockHoldings = new HashMap<>();
        this.reservedHoldings = new HashMap<>();
    }

    public static int getIdCounter() {
        return idCounter;
    }

    public int getDemandAccountId(){
        return this.demandAccountId;
    }

    public String getPanNumber(){
        return this.panNumber;
    }

    // Password never leaves this class - InputHandler passed in, password checked internally
    public boolean authenticateWithPrompt(InputHandler inputHandler, String prompt) {
        String pass = inputHandler.getString(prompt);
        return this.password.equals(pass);
    }

    // For internal use only when password is already obtained
    boolean authenticate(String pass) {
        return this.password.equals(pass);
    }

    public void addShares(String stockName, int quantity){
        stockHoldings.put(stockName, stockHoldings.getOrDefault(stockName, 0) + quantity);
    }

    public boolean sellShares(String stockName, int quantity){
        if(!reservedHoldings.containsKey(stockName)){
            System.out.println("Invalid stock name");
            return false;
        }
        int q = reservedHoldings.get(stockName);
        if(q < quantity){
            System.out.println("Insufficient stock quantity");
            return false;
        }
        reservedHoldings.put(stockName, q - quantity);
        if(reservedHoldings.get(stockName) == 0) reservedHoldings.remove(stockName);

        stockHoldings.put(stockName, stockHoldings.get(stockName) - quantity);
        if(stockHoldings.get(stockName) == 0) stockHoldings.remove(stockName);
        return true;
    }

    public boolean reserveStocks(String stockName, int quantity){
        if(!stockHoldings.containsKey(stockName)){
            System.out.println("Invalid stock name\n");
            return false;
        }
        int available = getAvailableQuantity(stockName);
        if (available < quantity) {
            System.out.println("Insufficient stock quantity to reserve!");
            return false;
        }
        reservedHoldings.put(stockName, reservedHoldings.getOrDefault(stockName, 0) + quantity);
        System.out.println("Stock : " + stockName + " | Reserved quantity : " + quantity + " , Successfully!");
        return true;
    }

    public void releaseReservedStocks(String stockName, int quantity){
        if(!reservedHoldings.containsKey(stockName)){
            System.out.println("Invalid stock name\n"); return;
        }
        if(reservedHoldings.get(stockName) < quantity){
            System.out.println("Insufficient stock quantity to release reserve!\n");
            return;
        }
        reservedHoldings.put(stockName, reservedHoldings.get(stockName) - quantity);
        if(reservedHoldings.get(stockName) == 0) reservedHoldings.remove(stockName);
    }

    public void getHoldings(){
        if(stockHoldings.isEmpty()){
            System.out.println("No holdings! Buy stocks\n");
        }else{
            System.out.println("+------------+------------+------------+------------+");
            System.out.printf(
                    "|%-12s|%-12s|%-12s|%-12s|\n",
                    "Stock Name", "Total", "Reserved", "Available"
            );
            System.out.println("+------------+------------+------------+------------+");
            for(String stockName : stockHoldings.keySet()){
                int total = stockHoldings.get(stockName);
                int reserved = reservedHoldings.getOrDefault(stockName, 0);
                int available = total - reserved;
                System.out.printf(
                        "|%-12s|%-12s|%-12s|%-12s|\n",
                        stockName, total, reserved, available
                );
            }
            System.out.println("+------------+------------+------------+------------+");
        }
    }

    public int getAvailableQuantity(String stockName){
        if(!stockHoldings.containsKey(stockName)){
            System.out.println("Invalid stockName\n");
            return 0;
        }
        int stHoldings = stockHoldings.get(stockName);
        int reHoldings = reservedHoldings.getOrDefault(stockName, 0);
        return stHoldings - reHoldings;
    }

    public boolean hasHoldings() {
        return !stockHoldings.isEmpty();
    }
}
