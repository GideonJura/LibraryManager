import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by siyingli on 4/23/18.
 */
public class LibrarySystem {

    private ArrayList<User> userList = new ArrayList<User>();
    private HashMap<String, User> userMap = new HashMap<String, User>();
    private User currentUser;
    private boolean userLoggedIn;


    LibrarySystem() {

        //TODO: read from DB or txt
        CommonUser commonUser1 = new CommonUser("SiyingLi", "123456", 1);
        CommonUser commonUser2 = new CommonUser("JohnSmith","123456",2);

        userList.add(commonUser1);
        userList.add(commonUser2);
        userMap.put(commonUser1.getUserName(), commonUser1);
        userMap.put(commonUser2.getUserName(), commonUser2);

        //System Start with the Default Administrator
        Administrator defaultAdmin = new Administrator("Super Administrator","LoveGongong",0);
        currentUser = defaultAdmin;
        userList.add(defaultAdmin);

        userLoggedIn = true;

        printScreen();

    }

    public boolean checkPassword(String userName, String password){
        if(!userMap.containsKey(userName)){
            System.out.println("No this User");
            return false;
        }
        else if(!password.equals( userMap.get(userName).getPassword())){
            System.out.println("Password Error");
            return false;
        } else{
            currentUser = userMap.get(userName);
            return true;
        }
    }

    private void printScreen(){

        if(!userLoggedIn){
            if(tryLogin()){
                userLoggedIn = true;
                //refresh UI
                printScreen();
            }
        }
        else if (currentUser.getClass() == Administrator.class){
            System.out.println("Welcome Adminstrator \n" +
                               "1 Logout");
            AdminLogic();
        } else if (currentUser.getClass() == User.class){
            System.out.println("Welcome Use " + currentUser.getUserName());
            UserLogic();
        }
    }

    public void AdminLogic(){

        System.out.println("Please Input Command");
        Scanner scanner = new Scanner(System.in);
        int command = scanner.nextInt();

        switch (command){
            case 1 :
                currentUser = null;
                userLoggedIn = false;
                printScreen();
                break;
        }
    }

    public void UserLogic(){
        System.out.println("Please Input Command \n1 Logout \n2 List Books");
        Scanner scanner = new Scanner(System.in);
        int command = scanner.nextInt();

        switch (command){
            case 1 :
                currentUser = null;
                userLoggedIn = false;
                printScreen();
                break;
            case 2 :
                currentUser = null;
                userLoggedIn = false;
                printScreen();
                break;
        }
    }


    public boolean tryLogin(){

        System.out.println("Please Put in your User Name");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.next();
        System.out.println("Please Put in your Password");
        String password = scanner.next();

        return (checkPassword(name, password));
    }



}
