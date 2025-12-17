public class User {
    private static int idCounter = 1;
    private int userId;
    private String userName;
    private String password;
    private String panNumber;
    private TradingAccount tradingAccount;
    private DematAccount dematAccount;
    private boolean isPromoter;
    private boolean isDeleted;

    public User(String userName, String password, String panNumber, boolean isPromoter){
        this.userId = idCounter++;
        this.userName = userName;
        this.password = password;
        this.panNumber = panNumber;
        this.tradingAccount = null;
        this.dematAccount = null;
        this.isPromoter = isPromoter;
        this.isDeleted = false;
    }

    public static int getIdCounter() {
        return idCounter;
    }

    public boolean login(InputHandler inputHandler) {
        String pass = inputHandler.getString("Enter password: ");
        return this.password.equals(pass);
    }

    public int getUserId() {
        return this.userId;
    }

    public String getUserName(){
        return this.userName;
    }

    public DematAccount getDematAccount() {
        return this.dematAccount;
    }

    public void setDematAccount(DematAccount dematAccount){
        this.dematAccount = dematAccount;
    }

    public String getPanNumber() {
        return this.panNumber;
    }

    public void setTradingAccount(TradingAccount tradingAccount){
        this.tradingAccount = tradingAccount;
    }

    public TradingAccount getTradingAccount(){
        return this.tradingAccount;
    }

    public boolean isPromoter() {
        return this.isPromoter;
    }

    public boolean isDeleted() {
        return this.isDeleted;
    }

    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }

}
