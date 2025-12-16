public class User {
    private static int idCounter = 1;
    
    private int userId;
    private String userName;
    private String password;
    private String panNumber;
    private int tradingAccountId;
    private int dematID;
    private boolean isPromoter;
    private boolean isDeleted;

    public User(String userName, String password, String panNumber, boolean isPromoter){
        this.userId = idCounter++;
        this.userName = userName;
        this.password = password;
        this.panNumber = panNumber;
        this.tradingAccountId = -1;
        this.dematID = -1;
        this.isPromoter = isPromoter;
        this.isDeleted = false;
    }

    public static int getIdCounter() {
        return idCounter;
    }

    // Password never leaves this class - InputHandler passed in, password checked internally
    public boolean login(InputHandler inputHandler) {
        String pass = inputHandler.getString("Enter password: ");
        return this.password.equals(pass);
    }

    // For delete confirmation - password checked internally
    public boolean confirmWithPassword(InputHandler inputHandler, String prompt) {
        String pass = inputHandler.getString(prompt);
        return this.password.equals(pass);
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

    public boolean isDeleted() {
        return this.isDeleted;
    }

    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }

//    void displayUserDetails(){
//        System.out.println();
//        System.out.println("User details...");
//        System.out.println("+----------+----------+------------+------------+");
//        System.out.printf(
//                "|%-10s|%-10s|%-12s|%-12s|\n",
//                "User Id", "User name", "Pan Number", "Is promoter"
//        );
//        System.out.println("+----------+----------+------------+------------+");
//        System.out.printf(
//                "|%-10d|%-10s|%-12s|%-12s|\n",
//                userId, userName, panNumber, isPromoter
//        );
//        System.out.println("+----------+----------+------------+------------+");
//    }
}
