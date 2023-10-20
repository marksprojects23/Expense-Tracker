import java.util.Scanner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;




public class App {
    public static ArrayList<Expense> expenses = new ArrayList<>();
    public static String fileName = "expenses.bat";
// Add more categories as needed


    public static void addExpense(JTextField Amount, JTextField Description, JComboBox<Object> Category, JLabel title, DefaultTableModel model) {
        //Scanner scanner = new Scanner(System.in);
        try {
        double expenseAmount = Double.parseDouble(Amount.getText());
        String expenseDescription = Description.getText();
        int expenseCategory = Category.getSelectedIndex();
        Expense newExpense = new Expense(expenseAmount, expenseDescription, expenseCategory);
        expenses.add(newExpense);
        model.addRow(new Object[]{newExpense.getCategory(), newExpense.getDescription(), newExpense.getAmount()});
        title.setText("Expense added.");
        } catch (InputMismatchException e) {
        //System.out.println("Invalid input. Please enter a valid number.");
        title.setText("Invalid input. Please enter a valid number.");
        }
    }

    public static void viewExpenses() {
        for (Expense expense : expenses) {
            System.out.println("Amount: " + expense.getAmount());
            System.out.println("Description: " + expense.getDescription());
            System.out.println("Category: " + expense.getCategory());
            System.out.println("-----------------------");
        }
    }
    
    public static double calculateExpenses(int i) {
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

public static class ButtonRenderer extends JButton implements TableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText("X");
        return this;
    }
}

public static class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private int clickedRow;

    public ButtonEditor(JCheckBox checkBox, JTable table, DefaultTableModel model, ArrayList<Expense> expenses, ExpenseFileHandler fileHandler) {
        super(checkBox);
        button = new JButton("X");
        button.setOpaque(true);
        button.addActionListener(e -> {
            model.removeRow(clickedRow);
            expenses.remove(clickedRow);
            fileHandler.saveExpenses(expenses);
            updateTotalRow(model);
        });
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        clickedRow = row;
        return button;
    }
}

public static void updateTotalRow(DefaultTableModel model) {
    // Remove existing total row if it exists
    for (int j = 0; j < model.getRowCount(); ++j) {
        if ("Total".equals(model.getValueAt(j, 1))) {
            model.removeRow(j);
        }
    }

    // Calculate total
    double total = 0;
    for (int i = 0; i < model.getRowCount(); i++) {
        Object value = model.getValueAt(i, 2); // Retrieve the value from the table
        if (value != null) {
            if (value instanceof Integer) {
                total += ((Integer) value).doubleValue(); // Convert Integer to double if it's an Integer
            } else if (value instanceof Double) {
                total += (Double) value; // Directly add if it's already a Double
            }
        }
    }
    
    // Add total row
    model.addRow(new Object[]{"", "Total", total});
}

public static void refreshTable(DefaultTableModel expenseModel, ArrayList<Expense> freshExpenses, int filter, Map<Integer, String> categoriesMap){
    expenseModel.setRowCount(0);
    if (filter == 0){
        for (Expense expense : freshExpenses) {
            expenseModel.addRow(new Object[]{expense.getCategory(), expense.getDescription(), expense.getAmount()});
        }
    }
    else if (filter != 0){
        for (Expense expense : freshExpenses) {
            if (expense.getCategory() == (filter - 1)){
                expenseModel.addRow(new Object[]{expense.getCategory(), expense.getDescription(), expense.getAmount()});
            }
        }
        
    }
    for (int row = 0; row < expenseModel.getRowCount(); row++) {
        Object categoryValue = expenseModel.getValueAt(row, 0);
        if (categoryValue instanceof Integer){
            int categoryId = (int) categoryValue;
            String categoryName = categoriesMap.get(categoryId);
            expenseModel.setValueAt(categoryName, row, 0);
        }
    }
    updateTotalRow(expenseModel);
}

    public static void main(String[] args) {
        Map<Integer, String> categoriesMap = new HashMap<>();
        categoriesMap.put(0, "Food");
        categoriesMap.put(1, "Entertainment");
        categoriesMap.put(2, "Travel");
        categoriesMap.put(3, "Utilities");

        ExpenseFileHandler fileHandler = new ExpenseFileHandler(fileName);

        JFrame frame = new JFrame("Expense Tracker");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main Layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JLabel appLabel = new JLabel("Expense Tracker");
        mainPanel.add(appLabel, BorderLayout.NORTH);

        // CardLayout for center
        CardLayout cl = new CardLayout();
        JPanel centerPanel = new JPanel(cl);
        JPanel addPanel = new JPanel(new GridLayout(3, 2));
        addPanel.add(new JLabel("Amount:"));
        JTextField amountField = new JTextField(10);
        addPanel.add(amountField);
        addPanel.add(new JLabel("Description:"));
        JTextField descriptionField = new JTextField(10);
        addPanel.add(descriptionField);
        addPanel.add(new JLabel("Category:"));
        JComboBox<Object> categoryField = new JComboBox<>(new Object[] {"Food", "Entertainment", "Travel", "Utilities" });
        addPanel.add(categoryField);
        
        DefaultTableModel expenseModel = new DefaultTableModel(new String[]{"Category", "Description", "Amount", "Delete"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return row != getRowCount() - 1; // make the last row non-editable
            }
        };
        expenses = fileHandler.loadExpenses();
        for (Expense expense : expenses) {
            expenseModel.addRow(new Object[]{expense.getCategory(), expense.getDescription(), expense.getAmount()});
        }
        expenseModel.addRow(new Object[]{"", "Total", 0});
        updateTotalRow(expenseModel);
        JTable expenseTable = new JTable(expenseModel);
        JScrollPane viewPanel = new JScrollPane(expenseTable);
        TableColumn deleteColumn = expenseTable.getColumnModel().getColumn(3);
        deleteColumn.setCellRenderer(new ButtonRenderer());
        deleteColumn.setCellEditor(new ButtonEditor(new JCheckBox(), expenseTable, expenseModel, expenses, fileHandler));
        //JScrollPane categoryPanel = new JScrollPane(categoryTable);
        centerPanel.add(addPanel, "Add");
        centerPanel.add(viewPanel, "View");
        //centerPanel.add(categoryPanel, "Category");
        cl.show(centerPanel, "Add");
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Action buttons at the bottom
        JPanel actionPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Expense");
        JButton addButton2 = new JButton("Add Expenses");
        addButton2.setVisible(false);
        JComboBox<Object> setCategory = new JComboBox<>(new Object[] {"All", "Food", "Entertainment", "Travel", "Utilities" });
        setCategory.setVisible(false);
        JButton viewButton = new JButton("View Expenses");
        addButton.addActionListener(e -> {
            addExpense(amountField, descriptionField, categoryField, appLabel, expenseModel);
            fileHandler.saveExpenses(expenses);
            updateTotalRow(expenseModel);
        });
        viewButton.addActionListener(e -> {
            appLabel.setText("Expenses");
            cl.show(centerPanel, "View");
            addButton.setVisible(false);
            viewButton.setVisible(false);
            addButton2.setVisible(true);
            setCategory.setVisible(true);
            refreshTable(expenseModel, fileHandler.loadExpenses(), setCategory.getSelectedIndex(), categoriesMap);
        });
        addButton2.addActionListener(e -> {
            appLabel.setText("Expense Tracker");
            cl.show(centerPanel, "Add");
            addButton.setVisible(true);
            viewButton.setVisible(true);
            addButton2.setVisible(false);
            setCategory.setVisible(false);
        });
        setCategory.addActionListener(e -> {refreshTable(expenseModel, fileHandler.loadExpenses(), setCategory.getSelectedIndex(), categoriesMap);});
        // ... (add action listeners)
        actionPanel.add(addButton);
        actionPanel.add(viewButton);
        actionPanel.add(addButton2);
        actionPanel.add(setCategory);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        // Add main panel to frame and show it
        frame.add(mainPanel);
        frame.setVisible(true);
        

        
        // Scanner scanner = new Scanner(System.in);
        // while (true) {
        // System.out.print("1 to add expense, 2 to view expenses, 3 to quit: ");
        // int addorview = scanner.nextInt();
        // scanner.nextLine();
        // if (addorview == 1){
        //     addExpense(scanner);
        //     fileHandler.saveExpenses(expenses);
        // }
        // else if (addorview == 2){
        //     expenses = fileHandler.loadExpenses();
        //     viewExpenses();
        // }
        // else {
        //     break;
        // }
        // }
        // scanner.close();
        

        




        //System.out.println("Expense Details:");
        //System.out.println("Amount: $" + expenseAmount);
        //System.out.println("Description: " + expenseDescription);
        //System.out.println("Category Code: " + expenseCategory);

    }
}
