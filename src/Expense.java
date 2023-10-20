import java.io.Serializable;

public class Expense implements Serializable{
    private int category;
    private double amount;
    private String description;

    // Constructors, getters, setters, and other methods go here
    public Expense(double amount, String description, int category) {
        this.amount = amount;
        this.description = description;
        this.category = category;
    }
    public double getAmount() {
        return amount;
    }
    public String getDescription() {
        return description;
    }
    public int getCategory() {
        return category;
    }
}
