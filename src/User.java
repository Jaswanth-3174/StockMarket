public class User {
    private int userId;
    private String userName;
    private String password;
    private String panNumber;
    private int tradingAccountId;
    private int dematID;
    private boolean isPromoter;

    public User(int userId, String userName, String password, String panNumber, boolean isPromoter){
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.panNumber = panNumber;
        this.tradingAccountId = -1;
        this.dematID = -1;
        this.isPromoter = isPromoter;
    }

    public int getUserId() {
        return this.userId;
    }

    public String getUserName(){
        return this.userName;
    }

    public int getDematID() {
        return this.dematID;
    }

    public void setDematAccountId(int id){
        this.dematID = id;
    }

    public String getPanNumber() {
        return this.panNumber;
    }

    public void setTradingAccountId(int id){
        this.tradingAccountId = id;
    }

    public int getTradingAccountId(){
        return this.tradingAccountId;
    }

    public boolean isPromoter() {
        return this.isPromoter;
    }

    void displayUserDetails(){
        System.out.println();
        System.out.println("User details...");
        System.out.println("+----------+----------+------------+------------+");
        System.out.printf(
                "|%-10s|%-10s|%-12s|%-12s|\n",
                "User Id", "User name", "Pan Number", "Is promoter"
        );
        System.out.println("+----------+----------+------------+------------+");
        System.out.printf(
                "|%-10d|%-10s|%-12s|%-12s|\n",
                userId, userName, panNumber, isPromoter
        );
        System.out.println("+----------+----------+------------+------------+");
    }
}
