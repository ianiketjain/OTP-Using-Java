import java.util.Random;
import java.util.Scanner;

public class OTP {
    public static void main(String[] args) {
        String AZ="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String az="abcdefghijklmnopqrstuvwxyz";
        String numm="0123456789";
        String spchar="!@#$%^&*.?<>)";
        
        System.out.println("______________________________________________________\n");
        System.out.println("Welcome to the Competition of COMPETITIVE PROGRAMMING By Aniket");
        System.out.println("______________________________________________________\n");
        
        System.out.print("Enter your name: ");
        Scanner sc=new Scanner(System.in);
        String name=sc.nextLine();
        
        System.out.print("Enter your mobile no: ");
        long number=sc.nextLong();
        System.out.println();
        
        System.out.println("______________________________________________________\n");
        
        Random x= new Random();  
        char[] otp = new char[5];
  
        for (int i = 0; i < 5; i++)
        {
            otp[i] = numm.charAt(x.nextInt(numm.length()));
        }
        String otpString = new String(otp);
        System.out.println("Your One Time Password is : " + otpString + "\nOn Your Registered Mobile Number: " + number);
        System.out.println("______________________________________________________\n");
        
        System.out.print("Enter your OTP: ");
        int check=sc.nextInt();
        int sum=0;
        String passmodify="";
        
        for(int i=0; i<5; i++){
            int rem=otpString.charAt(i) - '0';
            sum=sum*10+rem;
        }
        
        if(check==sum){
            String values = AZ + az + spchar + numm;
            Random pass = new Random();
            char[] password = new char[8];
            
            for (int i = 0; i < 8; i++)
            {
                password[i] = values.charAt(pass.nextInt(values.length()));
            }
    
            passmodify = new String(password);
            passmodify = passmodify.replaceAll("[,\\[\\] ]", ""); // remove commas, brackets, and spaces
    
            System.out.println("YOUR GENERATED PASSWORD IS: " + passmodify + "\n");
            System.out.println("______________________________________________________\n");
    
            System.out.println("USERNAME: " + name);
            System.out.print("PASSWORD: ");
            String checkpass = sc.next();
    
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
            
            if(checkpass.equals(passmodify)){
                System.out.println("Wohoo!! YOU ARE SUCCESSFULLY LOGGED IN. Enjoy Happy coding!\n" );
            } else{
                System.out.println("OOPS!! Try Again.\n");
            }
            
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
        } else{
            System.out.println("Wrong OTP!!");
        }
        
        sc.close();
    }    
}
