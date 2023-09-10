package Controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.Javalin;
import io.javalin.http.Context;
import Model.Account;
import Model.Message;
import DAO.*;


/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    AccountDAO accountDao = new AccountDAO();
    MessageDAO messageDao = new MessageDAO();

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::handleRegister);
        app.post("/login", this::handleLogin);
        app.post("/messages", this::handlePostMessage);
        app.get("/messages", this::handleGetAllMsgs);
        app.get("/messages/{message_id}", this::handleGetMsgById);
        app.get("/accounts/{account_id}/messages", this::handleGetMsgsFromAccount);
        app.delete("/messages/{message_id}", this::handleDelMsgById);
        app.patch("/messages/{message_id}", this::handleMsgUpdate);

        // app.get("/register", this::registerUser);
        // app.get("/register", this::registerUser);
        // app.get("/register", this::registerUser);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */

    private void handleRegister(Context context) throws Exception{
        // used this for help: https://www.tabnine.com/code/java/methods/io.javalin.Context/body

        Account registeredAccount = new ObjectMapper().readValue(context.body(), Account.class);
        if (registeredAccount.getUsername().length() <= 0){
            context.status(400); //.result("Username cannot be empty.");
            return;
        }
        else if (registeredAccount.getPassword().length() < 4){
            context.status(400); //.result("Password must be atleast 4 characters long.");
            return;
        }

        // if account username already exists, return error
        List<Account> allAccounts = accountDao.getAll();
        for (Account account : allAccounts){
            if (account.getUsername().equals(registeredAccount.getUsername())){
                context.status(400); //.result("Username already exists.");
                return;
            }

        }
        //if all works well, persist to database and return json representation as string
        int mainId = accountDao.insert(registeredAccount);
        registeredAccount.setAccount_id(mainId);


        context.status(200);
        context.result(new ObjectMapper().writeValueAsString(registeredAccount));
        return;

    }

    private void handleLogin(Context context) throws Exception{
        Account req = new ObjectMapper().readValue(context.body(),Account.class);
        String username = req.getUsername();
        String password = req.getPassword();

        //context.result("INSIDE FUNCTION TESTING:" + username + password);
        int userId = accountDao.getIdByUsernamePassword(username, password); //return -1 if not in database

        if (userId == -1){
            context.status(401);
            return;
        }
        else{
            context.status(200);
            context.result( new ObjectMapper().writeValueAsString(accountDao.get(userId)));
        }
        return;
    }

    private void handlePostMessage(Context context) throws Exception {
        Message req = new ObjectMapper().readValue(context.body(), Message.class);
        String msgText = req.getMessage_text();
        long time = req.getTime_posted_epoch();
        int accId = req.getPosted_by();
        Account postedBy = accountDao.get(accId);

        if (postedBy == null){ //invalid account
            context.status(400);
            return;
        }

        Message newMsg = new Message(accId, msgText, time);

        if (msgText.length() <= 0 || msgText.length() >= 255){
            context.status(400);
            return;
        }
        int messageId = messageDao.insert(newMsg);

        context.status(200);
        context.result( new ObjectMapper().writeValueAsString(messageDao.get(messageId)));
        return;
    }

    private void handleGetAllMsgs(Context context) throws Exception{
        List<Message> allMessages = messageDao.getAll();
        context.result(new ObjectMapper().writeValueAsString(allMessages));
        return;
    }

    private void handleGetMsgById(Context context) throws Exception{
        int messageId = Integer.parseInt(context.pathParam("message_id"));
        Message retMsg = messageDao.get(messageId);
        if (retMsg == null){
            context.result();
            return;
        }

        context.result(new ObjectMapper().writeValueAsString(retMsg));
        return;
    }

    private void handleDelMsgById(Context context) throws Exception{
        int messageId = Integer.parseInt(context.pathParam("message_id"));
        Message deletedMsg = messageDao.get(messageId);
        if (deletedMsg == null){
            context.status(200);
            context.result();
            return;
        }

        messageDao.delete(messageId);
        context.result(new ObjectMapper().writeValueAsString(deletedMsg));
        return;
    }

    private void handleMsgUpdate(Context context) throws Exception{
        int messageId = Integer.parseInt(context.pathParam("message_id"));
        Message inboundMessage = new ObjectMapper().readValue(context.body(), Message.class);
        String msgText = inboundMessage.getMessage_text();

        Message foundMessage = messageDao.get(messageId);
        if (msgText.length() <= 0 || msgText.length() >= 255){
            context.status(400);
            return;
        }
        if (foundMessage == null){
            context.status(400);
            return;
        }

        foundMessage.setMessage_text(msgText);
        context.result(new ObjectMapper().writeValueAsString(foundMessage));
        return;
    }

    private void handleGetMsgsFromAccount(Context context) throws Exception{
        int accountId = Integer.parseInt(context.pathParam("account_id"));
        Account foundAcc = accountDao.get(accountId);
        if (foundAcc == null){
            context.result(new ObjectMapper().writeValueAsString(new ArrayList<>()));
            return;
        }
        List<Message> allFound = messageDao.getAllMsgsByAccountId(accountId);
        context.result(new ObjectMapper().writeValueAsString(allFound));
        return;
    }
}