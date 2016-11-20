package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;

import static android.database.sqlite.SQLiteDatabase.CREATE_IF_NECESSARY;

/**
 * Created by tamas on 11/20/2016.
 */

public class PersistentExpenseManager extends ExpenseManager {
    private Context context;
    public PersistentExpenseManager(Context context){
            this.context=context;
        try {
            setup();
        } catch (ExpenseManagerException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void setup() throws ExpenseManagerException {
        SQLiteDatabase database=context.openOrCreateDatabase("140384P", context.MODE_PRIVATE,  null);
        database.execSQL("CREATE TABLE IF NOT EXISTS Account(" +
                "account_no VARCHAR PRIMARY KEY,"+
                "bank VARCHAR," +
                "account_holder VARCHAR,"+
                "initial_balance REAL"+
        ");");
        database.execSQL("CREATE TABLE IF NOT EXISTS TransactionLog("+
                "id INTEGER PRIMARY_KEY,"+
                "date Date,"+
                "account_no VARCHAR,"+
                "type INT,"+
                "amount REAL,"+
                "FOREIGN KEY(account_no)References Account(Account_no)"+
                ");");
        ;
        AccountDAO persistentAccountDAO=new PersistentAccountDAO(database);
        setAccountsDAO(persistentAccountDAO);
        TransactionDAO persistentTransactionDAO=new PersistentTransactionDAO(database);
        setTransactionsDAO(persistentTransactionDAO);



    }


}
