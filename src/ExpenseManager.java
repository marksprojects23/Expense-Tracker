import java.util.ArrayList;


public class ExpenseManager {                                                           // My spankin' new ExpenseManager.
    private ArrayList<Expense> expenses;                                                // It has it's own ArrayList.

    public ExpenseManager() {                                                           // A constructor, where
        this.expenses = new ArrayList<>();                                                   // it is initialized.
    }

    public Expense addExpense(double amount, String description, int categoryIndex) {   // This class has it's own addExpense.
        Expense newExpense = new Expense(amount, description, categoryIndex);           // It creates a new Expense object from the data throw into it,
        expenses.add(newExpense);                                                       // adds it to the initialized ArrayList of this instance of ExpenseManager,
        return newExpense;                                                              // then it returns the new Expense object. Very nice.
    }
    
    public double calculateExpenses(int i) {
        double total = 0.0;
        for (Expense expense : expenses) {
            if (i != 0)
                if (expense.getCategory() == i)
                    total += expense.getAmount();
            else
                total += expense.getAmount();
        }
        return total;
    }    

    public void loadExpensesFromFile(ExpenseFileHandler fileHandler) {
        expenses = fileHandler.loadExpenses();
    }

    public ArrayList<Expense> getExpenses() {
        return expenses;
    }

}
