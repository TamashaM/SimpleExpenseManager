package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * Created by tamas on 11/20/2016.
 */

public class PersistentAccountDAO implements AccountDAO{
    private SQLiteDatabase database;
    public PersistentAccountDAO(SQLiteDatabase database){
        this.database=database;
    }
   @Override
    public List<String> getAccountNumbersList() {
        //Query to select all account numbers from the account table
        Cursor cursor=database.rawQuery("SELECT account_no FROM Account",null);

        cursor.moveToFirst();
        ArrayList<String> resultSet = new ArrayList<String>();
        while (cursor.moveToNext())
        {
            //adding each account number
            resultSet.add(cursor.getString(cursor.getColumnIndex("account_no")));
        }
        cursor.close();
       //returning account number list
        return resultSet;

    }

    @Override
    public List<Account> getAccountsList() {
        //query to get all all accounts
        Cursor cursor=database.rawQuery("SELECT * FROM Account",null);
        cursor.moveToFirst();
        ArrayList<Account> resultSet=new ArrayList<Account>();
        while (cursor.moveToNext())
        {
            //create account object for each account with retrived details
            Account account = new Account(cursor.getString(cursor.getColumnIndex("account_no")),
                    cursor.getString(cursor.getColumnIndex("bank")),
                    cursor.getString(cursor.getColumnIndex("account_holder")),
                    cursor.getDouble(cursor.getColumnIndex("initial_balance")));

            resultSet.add(account);
        }
        cursor.close();
        //return accounts
        return resultSet;




    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        //query to get the account for a specific accountNo
        Cursor cursor=database.rawQuery("SELECT * FROM Account WHERE account_no="+accountNo,null);
        cursor.moveToFirst();
        Account account=null;
        if(cursor.moveToFirst()) {
            // create account object if the accountNo exists
            account = new Account(cursor.getString(cursor.getColumnIndex("account_no")),
                    cursor.getString(cursor.getColumnIndex("bank")),
                    cursor.getString(cursor.getColumnIndex("account_holder")),
                    cursor.getDouble(cursor.getColumnIndex("initial_balance")));
        }
        else{
            //throw an if the accountNo doesnt exist
            throw new InvalidAccountException("Incorrect account number");
        }
        cursor.close();
        // return the account
        return account;
    }

    @Override
    public void addAccount(Account account) {
        //query to insert an account
        String query="INSERT INTO Account(account_no,bank,account_holder,initial_balance) VALUES(?,?,?,?)";
        SQLiteStatement stat=database.compileStatement(query);

        stat.bindString(1,account.getAccountNo());
        stat.bindString(2,account.getBankName());
        stat.bindString(3,account.getAccountHolderName());
        stat.bindDouble(4,account.getBalance());
        stat.executeInsert();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        //query to delete account
        String query="DELETE FROM Account where account_no= ?";
        SQLiteStatement stat=database.compileStatement(query);
        stat.bindString(1,accountNo);
        stat.executeUpdateDelete();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        //query toupdate balance for a specfic account number
        String query="UPDATE Account SET initial_balance=initial_balance +? WHERE account_no=?";
        SQLiteStatement stat=database.compileStatement(query);
        if(expenseType==ExpenseType.EXPENSE){
            //if its an expense reduce the amount
            stat.bindDouble(1,-amount);
        }
        else{
            //if its an income add the amount
            stat.bindDouble(1,+amount);
        }
        stat.bindString(2,accountNo);
        stat.executeUpdateDelete();

    }
}
