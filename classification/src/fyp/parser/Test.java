import java.util.Scanner;
class Test{
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String in;
        System.out.print("Enter a string: ");
        in = sc.next();
        while(!in.equals("-1")){
            System.out.println(Helper.isNumeric(in));
            in = sc.next();
        }
    }
}