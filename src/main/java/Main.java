import java.sql.SQLException;
import java.util.List;


import Controller.SocialMediaController;
import io.javalin.Javalin;
import DAO.*;
import Model.*;

/**
 * This class is provided with a main method to allow you to manually run and test your application. This class will not
 * affect your program in any way and you may write whatever code you like here.
 */
public class Main {
    public static void main(String[] args) {
        SocialMediaController controller = new SocialMediaController();
        Javalin app = controller.startAPI();
        
        app.start(8080);

        AccountDAO accountDao = new AccountDAO();
        MessageDAO messageDao = new MessageDAO();


        // // Perform database operations
        // Account test = new Account("Testing", "12345"); 
        // Account test1 = new Account("Testing1", "453"); 
        // Account test2 = new Account("Testing2", "78"); 
        // Account test3 = new Account("Testing3", "687"); 
        // Account test4 = new Account("Testing4", "787"); 

        // try {
        //     accountDao.insert(test);
        //     accountDao.insert(test1);
        //     accountDao.insert(test2);
        //     accountDao.insert(test3);
        //     accountDao.insert(test4);
        //     Account newAcc = accountDao.get(2);
        //     System.out.println("THE NEWLY CREATED ACCOUNT:" + newAcc);
        // } catch (SQLException e) {
        //     e.printStackTrace();
        // }
      

        //  try {
        //     List<Account> allAccounts =  accountDao.getAll();
        //     for (Account account : allAccounts){
        //         System.out.println(account);
        //     }
        // } catch (SQLException e) {
        //     e.printStackTrace();
        // }
        // try {
        //     int newAcc = accountDao.getIdByUsernamePassword("testuser1", "password");
        //     System.out.println(newAcc);
        // } catch (SQLException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        
        try {
            Message newMsg = new Message(1, "Testing testing testing", 1669947792);
            int mainId = messageDao.insert(newMsg);
            System.out.println(mainId);
            List<Message> allMessages = messageDao.getAll();
            for (Message msg: allMessages){
                System.out.println(msg);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}
