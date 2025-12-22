package trading;

import java.util.HashMap;
import java.util.Map;

public class StockPortfolio {
    private Map<String, StockHolding> holdings;

    public StockPortfolio() {
        this.holdings = new HashMap<>();
    }

    public void addShares(String stockName, int quantity) {
        if (holdings.containsKey(stockName)) {
            holdings.get(stockName).addShares(quantity);
        } else {
            holdings.put(stockName, new StockHolding(stockName, quantity));
        }
    }

    public boolean sellShares(String stockName, int quantity) {
        if (!holdings.containsKey(stockName)) {
            System.out.println("Invalid stock name");
            return false;
        }
        StockHolding holding = holdings.get(stockName);
        if (!holding.removeShares(quantity)) {
            System.out.println("Insufficient stock quantity");
            return false;
        }
        if (holding.isEmpty()) {
            holdings.remove(stockName);
        }
        return true;
    }

    public boolean reserveStocks(String stockName, int quantity) {
        if (!holdings.containsKey(stockName)) {
            System.out.println("Invalid stock name\n");
            return false;
        }
        StockHolding holding = holdings.get(stockName);
        if (!holding.reserve(quantity)) {
            System.out.println("Insufficient stock quantity to reserve!");
            return false;
        }
        System.out.println("Stock : " + stockName + " | Reserved quantity : " + quantity + " , Successfully!");
        return true;
    }

    public void releaseReservedStocks(String stockName, int quantity) {
        if (!holdings.containsKey(stockName)) {
            System.out.println("Invalid stock name\n");
            return;
        }
        StockHolding holding = holdings.get(stockName);
        if (!holding.releaseReserved(quantity)) {
            System.out.println("Insufficient stock quantity to release reserve!\n");
        }
    }

    public int getAvailableQuantity(String stockName) {
        if (!holdings.containsKey(stockName)) {
            System.out.println("Invalid stockName\n");
            return 0;
        }
        return holdings.get(stockName).getAvailableQuantity();
    }

    public int getTotalQuantity(String stockName) {
        if (!holdings.containsKey(stockName)) {
            return 0;
        }
        return holdings.get(stockName).getTotalQuantity();
    }

    public int getReservedQuantity(String stockName) {
        if (!holdings.containsKey(stockName)) {
            return 0;
        }
        return holdings.get(stockName).getReservedQuantity();
    }

    public StockHolding getHolding(String stockName) {
        return holdings.get(stockName);
    }

    public boolean hasHoldings() {
        return !holdings.isEmpty();
    }

    public boolean isEmpty() {
        return holdings.isEmpty();
    }

    public void displayHoldings() {
        if (holdings.isEmpty()) {
            System.out.println("No holdings! Buy stocks\n");
        } else {
            System.out.println("+------------+------------+------------+------------+");
            System.out.printf(
                    "|%-12s|%-12s|%-12s|%-12s|\n",
                    "Stock Name", "Total", "Reserved", "Available"
            );
            System.out.println("+------------+------------+------------+------------+");
            for (StockHolding holding : holdings.values()) {
                System.out.printf(
                        "|%-12s|%-12d|%-12d|%-12d|\n",
                        holding.getStockName(), 
                        holding.getTotalQuantity(), 
                        holding.getReservedQuantity(), 
                        holding.getAvailableQuantity()
                );
            }
            System.out.println("+------------+------------+------------+------------+");
        }
    }
}