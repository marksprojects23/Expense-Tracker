import java.io.*;
import java.util.ArrayList;

public class ExpenseFileHandler {
    private String fileName;

    public ExpenseFileHandler(String fileName) {
        this.fileName = fileName;
    }

    public boolean saveExpenses(ArrayList<Expense> expenses) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            outputStream.writeObject(expenses);
            System.out.println("Expenses saved successfully.");
            return true;
        } catch (IOException e) {
            System.err.println("Error saving expenses: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Expense> loadExpenses() {
        ArrayList<Expense> expenses = new ArrayList<>();
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName))) {
            expenses = (ArrayList<Expense>) inputStream.readObject();
            System.out.println("Expenses loaded successfully.");
            return expenses;
        } catch (FileNotFoundException e) {
            System.out.println("Expense file not found.");
            return expenses;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading expenses: " + e.getMessage());
            return expenses;
        }
    }
}
