import java.util.*;
import java.io.*;
import java.io.BufferedReader;


public class LibrarySystem {

    private HashMap<String, User> userMap = new HashMap<>();
    private HashSet<Book> bookSet = new HashSet<>();

    private User currentUser;
    private boolean userLoggedIn;

    private String userPath;
    private String bookPath;

    LibrarySystem(String userPath, String bookPath) {

        this.userPath = userPath;
        this.bookPath = bookPath;

        loadFile();
        currentUser = null;
        userLoggedIn = false;
        printMainScreen();

    }

    private void loadFile(){
        //Load User first, so we can set books to particular owner
        userMap.clear();
        bookSet.clear();

        userMap = this.loadUsers(userPath);
        bookSet = this.loadBooks(bookPath);
    }

    private void saveFile(){

        try {
            //Admin,111111,1,Administator
            OutputStream userOutput = new FileOutputStream(userPath, false);
            for(User user: userMap.values()){
                String userString = user.getUserName()+ "," + user.getPassword() + "," + user.getUserId()
                            + "," + (user.getClass() == Administrator.class ? "Administrator" : "CommonUser") + "\n";
                userOutput.write(userString.getBytes());

            }
            userOutput.close();

            //I love C++,ISBN123475,Northeastern,SiyingLi
            OutputStream bookOutput = new FileOutputStream(userPath, false);
            for(Book book: bookSet){
                String bookString = book.getBookName() + "," + book.getBookISBN() + "," + book.getBookPublisher()
                                + "," + (book.isBorrowedOut() ? book.getOwner().getUserName() : "Library") + "\n";
                bookOutput.write(bookString.getBytes());
            }
            bookOutput.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashSet<Book> loadBooks(String bookPath){

        HashSet<Book> bookSet = new HashSet<>();
        //Load Data from the files in the project
        try {
            String thisLine;
            InputStream fis=new FileInputStream(bookPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            while ((thisLine = br.readLine()) != null) {
                Book newBook = processBookInput(thisLine);
                if(newBook != null){
                    bookSet.add(newBook);
                }
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return bookSet;
    }

    private HashMap<String, User> loadUsers(String userPath){

        HashMap<String, User> userMap = new HashMap<>();
        try {
            String thisLine;
            InputStream fis=new FileInputStream(userPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            while ((thisLine = br.readLine()) != null) {
                User newUser = processUserInput(thisLine);
                if(newUser != null){
                    userMap.put(newUser.getUserName(),newUser);
                }
            }
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return userMap;
    }

    private Book processBookInput(String bookLine){

        String[] bookLineSplit = bookLine.split(",");

        if(bookLineSplit.length != 4 )
            return null;
        else{
            String bookName = bookLineSplit[0];
            String bookISBN = bookLineSplit[1];
            String bookPublisher = bookLineSplit[2];
            String bookOwner = bookLineSplit[3];

            Book currentBook = new Book(bookName, bookISBN, bookPublisher);


            if(!userMap.containsKey(bookOwner) || bookOwner.trim().equals("Library")){
                return currentBook;
            } else{
                currentBook.setOwner(userMap.get(bookOwner));
                currentBook.setBorrowedOut(true);
                return currentBook;
            }
        }
    }

    private User processUserInput(String userLine){

        //Sample Line  Lisiying|123456|237|CommonUser

        String[] userLineSplit = userLine.split(",");

        if(userLineSplit.length != 4 )
            return null;
        else{
            String userName = userLineSplit[0];
            String userPassword = userLineSplit[1];
            String userID = userLineSplit[2];
            String userType = userLineSplit[3];

            if(userType.trim().equals("CommonUser")){
                return new CommonUser(userName, userPassword,Integer.valueOf(userID));
            } else if (userType.trim().equals("Administrator")){
                return new Administrator(userName, userPassword,Integer.valueOf(userID));
            }else{
                return null;
            }

        }
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

    private void printMainScreen(){

        if(!userLoggedIn){
            System.out.println("Please Log In...");
            if(tryLogin()){
                userLoggedIn = true;
                loadFile();
                if (currentUser.getClass() == Administrator.class){
                    System.out.println("Welcome Adminstrator");
                    printAdministratorScreen();
                } else if (currentUser.getClass() == CommonUser.class){
                    System.out.println("Welcome Use " + currentUser.getUserName());
                    printUserMainScreen();
                }
            }else{
                System.out.println("Login Fails.");
            }
        } else{
            System.out.println("Please Press 1 to Logout ...");
        }
    }

    private void printUserMainScreen(){
        System.out.println("Please Input Command \n1 Logout \n2 List Books \n3 Borrow Books \n4 Return Books");
        UserLogic();
        printUserMainScreen();
    }

    private void printAdministratorScreen(){
        System.out.println("Please Input Command \n" +
                            "1 Logout \n" +
                            "2 List Books \n" +
                            "3 Register Books ");
        AdministratorLogic();
        printAdministratorScreen();
    }

    private void printLine(){
        System.out.println("##################################################");
    }

    private void printRegisterBookScreen(){
        printLine();
        System.out.println("Register Books to the library...\n");
        addNewBook();
        printAdministratorScreen();

    }

    private void printBookScreen(){

        if(currentUser.getClass() == CommonUser.class) {
            printLine();
            System.out.println("Books you have borrowed...\n");
            for (Book book : bookSet) {
                if (!book.isBorrowedOut())
                    continue;
                if (book.getOwner().getUserName().equals(currentUser.getUserName())) {
                    System.out.println(book.getBookName());
                }
            }
            printLine();
        }
        System.out.println("Books in the library...\n");
        for(Book book : bookSet){
            if(!book.isBorrowedOut()){
                System.out.println(book.getBookName());
            }
        }
        printLine();
    }

    public void printBorrowScreen(){
        printLine();
        System.out.println("Books in the library...\n");
        HashMap<Integer, Book> bookMap = new HashMap<>();
        int index = 1;
        for(Book book : bookSet){
            if(!book.isBorrowedOut()){
                bookMap.put(index, book);
                System.out.println(index + "\t" + book.getBookName());
                index ++;
            }
        }
        printLine();
        borrowBookLogic(bookMap);

    }

    public boolean borrowBookLogic(HashMap<Integer, Book> bookMap){
        System.out.println("Print Number to Borrow, 0 to exit");
        Scanner scanner = new Scanner(System.in);
        int command = scanner.nextInt();

        if(command == 0){
            System.out.println("Exiting to Main Menu");
            printUserMainScreen();
            return false;
        }

        else if(bookMap.containsKey(command)){
            System.out.println("Borrow Successful");
            bookMap.get(command).setBorrowedOut(true);
            bookMap.get(command).setOwner(currentUser);
            return true;

        } else {
            System.out.println("Wrong Borrow Number");
            borrowBookLogic(bookMap);
            return false;
        }
    }

    private void printReturnScreen(){
        printLine();
        System.out.println("Books you owned ...\n");
        HashMap<Integer, Book> bookMap = new HashMap<>();
        int index = 1;
        for(Book book : bookSet){
            if(!book.isBorrowedOut())
                continue;
            if(book.getOwner().equals(currentUser)){
                bookMap.put(index, book);
                System.out.println(index + "\t" + book.getBookName());
                index ++;
            }
        }
        printLine();
        returnBookLogic(bookMap);

    }

    private boolean returnBookLogic(HashMap<Integer, Book> bookMap){
        System.out.println("Print Number to Return, 0 to exit");
        Scanner scanner = new Scanner(System.in);
        int command = scanner.nextInt();

        if(command == 0){
            System.out.println("Exiting to Main Menu");
            printUserMainScreen();
            return false;
        }

        else if(bookMap.containsKey(command)){
            System.out.println("Return Successful");
            bookMap.get(command).setBorrowedOut(false);
            bookMap.get(command).setOwner(null);
            return true;
        } else {
            System.out.println("Wrong Borrow Book Number");
            borrowBookLogic(bookMap);
            return false;
        }
    }


    private void addNewBook(){
        printLine();
        System.out.println("Please Enter BookName");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.next();
        System.out.println("Please Enter BookISBN");
        String isbn = scanner.next();
        System.out.println("Please Enter Publisher");
        String publisher = scanner.next();

        Book newBook = new Book(name, isbn, publisher);
        bookSet.add(newBook);
        System.out.println("Add Book Successful");


    }


    private void AdministratorLogic(){
        Scanner scanner = new Scanner(System.in);
        int command = scanner.nextInt();

        switch (command){
            case 1 :
                currentUser = null;
                userLoggedIn = false;
                saveFile();
                printMainScreen();
                break;
            case 2 :
                printBookScreen();
                break;
            case 3 :
                printRegisterBookScreen();
                break;
            default :
                System.out.println("Wrong Command");
        }
    }



    private void UserLogic(){

        Scanner scanner = new Scanner(System.in);
        int command = scanner.nextInt();

        switch (command){
            case 1 :
                currentUser = null;
                userLoggedIn = false;
                saveFile();
                printMainScreen();
                break;
            case 2 :
                printBookScreen();
                break;
            case 3 :
                printBorrowScreen();
                break;
            case 4 :
                printReturnScreen();
                break;
            default :
                System.out.println("Wrong Command");

        }
    }


    private boolean tryLogin(){

        System.out.println("Please Put in your User Name");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.next();
        System.out.println("Please Put in your Password");
        String password = scanner.next();

        return (checkPassword(name, password));
    }



}
