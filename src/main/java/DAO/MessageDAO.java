package DAO;

import java.sql.SQLException;
import java.util.List;


import Util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

import Model.Message;

public class MessageDAO implements DAO<Message> {


//  message_id integer primary key auto_increment,
//  posted_by integer,
//  message_text varchar(255),
//  time_posted_epoch long,
//  foreign key (posted_by) references Account(account_id)
    @Override
    public Message get(int id) throws SQLException {
        Connection con = ConnectionUtil.getConnection();
        Message msg = null;
        String sql = "SELECT * FROM Message WHERE message_id = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) { // Check if there are any rows
                int msgId = rs.getInt("message_id");
                int postedBy = rs.getInt("posted_by");
                String msgTxt = rs.getString("message_text");
                long timePosted = rs.getLong("time_posted_epoch");
                msg = new Message(msgId, postedBy, msgTxt, timePosted);
            } else {
                // Handle the case where no rows were returned, e.g., set account to null or throw an exception.

                System.out.println("There are no rows for account get.");
                msg = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL Exception occurred.");
        }

        return msg;

    }

    @Override
    public List<Message> getAll() throws SQLException {
        Connection con = ConnectionUtil.getConnection();

        String sql = "SELECT * FROM Message";
        Statement s = con.createStatement();
        ResultSet rs = s.executeQuery(sql);
        List<Message> allMessages = new ArrayList<>();
        while (rs.next()){
            int msgId = rs.getInt("message_id");
            int postedBy = rs.getInt("posted_by");
            String msgTxt = rs.getString("message_text");
            long timePosted = rs.getLong("time_posted_epoch");
            Message msg = new Message(msgId, postedBy, msgTxt, timePosted);
            allMessages.add(msg);
        }

        return allMessages;
    }

    public List<Message> getAllMsgsByAccountId(int accountId) throws SQLException {
        Connection con = ConnectionUtil.getConnection();

        String sql = "SELECT * FROM Message WHERE posted_by = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, accountId);

        ResultSet rs = ps.executeQuery();
        List<Message> allMessages = new ArrayList<>();
        while (rs.next()){
            int msgId = rs.getInt("message_id");
            int postedBy = rs.getInt("posted_by");
            String msgTxt = rs.getString("message_text");
            long timePosted = rs.getLong("time_posted_epoch");
            Message msg = new Message(msgId, postedBy, msgTxt, timePosted);
            allMessages.add(msg);
        }

        return allMessages;
    }

    @Override
    public int insert(Message t) throws SQLException {
        
        Connection con = ConnectionUtil.getConnection();

        String sql = "INSERT INTO Message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); // get the id of the newly created message

        ps.setInt(1,t.getPosted_by());
        ps.setString(2, t.getMessage_text());
        ps.setLong(3, t.getTime_posted_epoch());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()){
            return rs.getInt("message_id");
        }
        else {
            throw new SQLException("Failed to get the generated message key.");
        }
    }


    @Override
    public int update(Message t) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public int delete(int msgId) throws SQLException {
        Connection con = ConnectionUtil.getConnection();

        String sql = "DELETE FROM Message WHERE message_id = ?";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, msgId);
        return ps.executeUpdate(); // rows affected
    }
        
}
