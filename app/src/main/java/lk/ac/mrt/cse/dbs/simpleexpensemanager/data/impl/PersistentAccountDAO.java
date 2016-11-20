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
        Cursor cursor=database.rawQuery("SELECT account_no FROM Account",null);

        cursor.moveToFirst();
        ArrayList<String> resultSet = new ArrayList<String>();
        while (cursor.moveToNext())
        {
            resultSet.add(cursor.getString(cursor.getColumnIndex("account_no")));
        }
        cursor.close();
        return resultSet;

    }

    @Override
    public List<Account> getAccountsList() {
        Cursor cursor=database.rawQuery("SELECT * FROM Account",null);
        cursor.moveToFirst();
        ArrayList<Account> resultSet=new ArrayList<Account>();
        while (cursor.moveToNext())
        {
            Account account = new Account(cursor.getString(cursor.getColumnIndex("account_no")),
                    cursor.getString(cursor.getColumnIndex("bank")),
                    cursor.getString(cursor.getColumnIndex("account_holder")),
                    cursor.getDouble(cursor.getColumnIndex("initial_balance")));

            resultSet.add(account);
        }
        cursor.close();
        return resultSet;




    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Cursor cursor=database.rawQuery("SELECT * FROM Account WHERE account_no="+accountNo,null);
        cursor.moveToFirst();
        Account account=null;
        if(cursor.moveToFirst()) {
            account = new Account(cursor.getString(cursor.getColumnIndex("account_no")),
                    cursor.getString(cursor.getColumnIndex("bank")),
                    cursor.getString(cursor.getColumnIndex("account_holder")),
                    cursor.getDouble(cursor.getColumnIndex("initial_balance")));
        }
        else{
            throw new InvalidAccountException("Incorrect account number");
        }
        cursor.close();
        return account;
    }

    @Override
    public void addAccount(Account account) {
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
        String query="DELETE FROM Account where account_no= ?";
        SQLiteStatement stat=database.compileStatement(query);
        stat.bindString(1,accountNo);
        stat.executeUpdateDelete();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        String query="UPDATE Account SET initial_balance=initial_balance +? WHERE account_no=?";
        SQLiteStatement stat=database.compileStatement(query);
        if(expenseType==ExpenseType.EXPENSE){
            stat.bindDouble(1,-amount);
        }
        else{
            stat.bindDouble(1,+amount);
        }
        stat.bindString(2,accountNo);
        stat.executeUpdateDelete();

    }
}
