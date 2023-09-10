package DAO;

import java.sql.SQLException;
import java.util.List;

import Util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

import Model.Account;

public class AccountDAO implements DAO<Account> {

    @Override
    public Account get(int id) throws SQLException {
        Connection con = ConnectionUtil.getConnection();
        Account account = null;
        String sql = "SELECT account_id, username, password FROM Account WHERE account_id = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        System.out.println("SQL Query: " + sql);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) { // Check if there are any rows
                int accountId = rs.getInt("account_id");
                String accountUser = rs.getString("username");
                String accountPass = rs.getString("password");
                account = new Account(accountId, accountUser, accountPass);
            } else {
                // Handle the case where no rows were returned, e.g., set account to null or throw an exception.

                System.out.println("There are no rows for account get.");
                account = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL Exception occurred.");
        }
    

        return account;
    }

    public int getIdByUsernamePassword(String username, String password) throws SQLException{
        Connection con = ConnectionUtil.getConnection();
        String sql = "SELECT account_id FROM Account WHERE username = ? AND password = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1,username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("account_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }


    @Override
    public List<Account> getAll() throws SQLException {
        Connection con = ConnectionUtil.getConnection();

        String sql = "SELECT * FROM Account";
        Statement s = con.createStatement();
        ResultSet rs = s.executeQuery(sql);
        List<Account> allAccounts = new ArrayList<>();
        while (rs.next()){
            int account_id = rs.getInt("account_id");
            String username = rs.getString("username");
            String password = rs.getString("password");
            Account acc = new Account(account_id,username,password);
            allAccounts.add(acc);
        }

        return allAccounts;
    }

    @Override
    public int insert(Account t) throws SQLException {
        Connection con = ConnectionUtil.getConnection();

        String sql = "INSERT INTO Account (username, password) VALUES (?, ?)";
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1,t.getUsername());
        ps.setString(2, t.getPassword());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()){
            return rs.getInt("account_id");
        }
        else {
            throw new SQLException("Failed to get generated account keys");
        }
    }

    @Override
    public int update(Account t) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public int delete(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
}
