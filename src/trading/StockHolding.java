package trading;

public class StockHolding {
    private String stockName;
    private int totalQuantity;
    private int reservedQuantity;

    public StockHolding(String stockName) {
        this.stockName = stockName;
        this.totalQuantity = 0;
        this.reservedQuantity = 0;
    }

    public StockHolding(String stockName, int quantity) {
        this.stockName = stockName;
        this.totalQuantity = quantity;
        this.reservedQuantity = 0;
    }

    public String getStockName() {
        return stockName;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public int getAvailableQuantity() {
        return totalQuantity - reservedQuantity;
    }

    public void addShares(int quantity) {
        this.totalQuantity += quantity;
    }

    public boolean removeShares(int quantity) {
        if (reservedQuantity < quantity) {
            return false;
        }
        this.reservedQuantity -= quantity;
        this.totalQuantity -= quantity;
        return true;
    }

    public boolean reserve(int quantity) {
        if (getAvailableQuantity() < quantity) {
            return false;
        }
        this.reservedQuantity += quantity;
        return true;
    }

    public boolean releaseReserved(int quantity) {
        if (reservedQuantity < quantity) {
            return false;
        }
        this.reservedQuantity -= quantity;
        return true;
    }

    public boolean isEmpty() {
        return totalQuantity == 0;
    }

    @Override
    public String toString() {
        return "trading.StockHolding{" + stockName + ", total=" + totalQuantity +
               ", reserved=" + reservedQuantity + ", available=" + getAvailableQuantity() + "}";
    }
}
