package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by tamas on 11/20/2016.
 */

public class PersistentTransactionDAO implements TransactionDAO {
    private SQLiteDatabase database;
    public PersistentTransactionDAO(SQLiteDatabase database){
        this.database=database;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        String query = "INSERT INTO TransactionLog (account_no,type,amount,date) VALUES (?,?,?,?)";
        SQLiteStatement stat = database.compileStatement(query);

        stat.bindString(1,accountNo);
        stat.bindLong(2,(expenseType == ExpenseType.EXPENSE) ? 0 : 1);
        stat.bindDouble(3,amount);
        stat.bindLong(4,date.getTime());

        stat.executeInsert();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        Cursor cursor = database.rawQuery("SELECT * FROM TransactionLog",null);

        ArrayList<Transaction> resultSet = new ArrayList<Transaction>();
        cursor.moveToFirst();
        while(cursor.moveToNext()){
            Transaction transaction = new Transaction(new Date(cursor.getLong(cursor.getColumnIndex("date"))),
                    cursor.getString(cursor.getColumnIndex("account_no")),

                    (cursor.getInt(cursor.getColumnIndex("type")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME,
                    cursor.getDouble(cursor.getColumnIndex("amount")));
            resultSet.add(transaction);
        }
        cursor.close();
        return resultSet;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        Cursor cursor = database.rawQuery("SELECT * FROM TransactionLog LIMIT " + limit,null);
        cursor.moveToFirst();
        ArrayList<Transaction> resultSet = new ArrayList<Transaction>();

        while(cursor.moveToNext()){
            Transaction transaction = new Transaction(new Date(cursor.getLong(cursor.getColumnIndex("date"))),
                    cursor.getString(cursor.getColumnIndex("account_no")),
                    (cursor.getInt(cursor.getColumnIndex("type")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME,
                    cursor.getDouble(cursor.getColumnIndex("amount")));
            resultSet.add(transaction);
        }
        cursor.close();
        return resultSet;
    }
}
