import java.util.Scanner;
import java.util.InputMismatchException;

public class InputHandler {

    private Scanner scanner;

    public InputHandler(Scanner scanner){
        this.scanner = scanner;
    }

    public int getInteger(String string){
        while (true){
            System.out.print(string);
            try {
                int num = scanner.nextInt();
                scanner.nextLine();
                if(isNegative(num)){
                    System.out.println("Input can't be negative!");
                }
                else return num;
            }catch (InputMismatchException e){
                System.out.println("Invalid input! Enter integer");
                scanner.nextLine();
            }
        }
    }

    public String getString(String string){
        while (true){
            System.out.print(string);
            String input = scanner.nextLine();
            if(input.isEmpty()){
                System.out.println("Input cannot be empty!");
            }else{
                return input;
            }
        }
    }

    public double getDouble(String string){
        while(true){
            System.out.print(string);
            try{
                double d = scanner.nextDouble();
                scanner.nextLine();
                if(isNegative(d)){
                    System.out.println("Input can't be negative!");
                }
                else return d;
            }catch (InputMismatchException e){
                System.out.println("Invalid input! Enter Double/Integer");
                scanner.nextLine();
            }
        }
    }

    public boolean isNegative(int i){
        if(i < 0){
            return true;
        }
        return false;
    }

    public boolean isNegative(double d){
        if(d < 0.0){
            return true;
        }
        return false;
    }

}
