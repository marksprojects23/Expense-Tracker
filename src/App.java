import javax.swing.*;                       // Swing, as needed for the GUI elements.
import javax.swing.table.DefaultTableModel; // Eventually, I needed tables.
import javax.swing.table.TableCellRenderer; // And table cell renderers.
import javax.swing.table.TableColumn;       // As well as columns.

import java.awt.*;                          // Just incase. We can't go wrong with AWT.

import java.util.ArrayList;                 // This project needs an Array List.
import java.util.HashMap;                   // Weirdly enough, I needed a HashMap for this project.
import java.util.HashSet;
import java.util.InputMismatchException;    // This is for validation, though I might not need this after I clean up my code.
import java.util.Map;                       // I definitely needed this too.
import java.util.Set;
import java.util.Iterator;





public class App {                          // Our main class.
    public static String fileName = "expenses.bat";                 // This is the universal list of expenses kept in storage.
    public static Map<Integer, String> categoriesMap = new HashMap<>();
    public static Map<String, Integer> categoriesMap2 = new HashMap<>();
    static {
        categoriesMap.put(0, "Food");
        categoriesMap.put(1, "Entertainment");
        categoriesMap.put(2, "Travel");
        categoriesMap.put(3, "Utilities");
        for (Map.Entry<Integer, String> entry : categoriesMap.entrySet()) {
            categoriesMap2.put(entry.getValue(), entry.getKey());
        }
    }
    

    public void updateUI(Expense newExpense, DefaultTableModel model, JLabel title) {                               // This is a function simply to update the UI with new Expense changes.
        model.addRow(new Object[]{newExpense.getCategory(), newExpense.getDescription(), newExpense.getAmount()});  // To the GUI's table, it adds a fresh new row.
        title.setText("Expense added.");                                                                       // And then there's the label change to inform the user.
    }

    public void addExpenseWrapper(JTextField Amount, JTextField Description, JComboBox<Object> Category, JLabel title, DefaultTableModel model, ExpenseManager manager) {
        try {
            double expenseAmount = Double.parseDouble(Amount.getText());
            String expenseDescription = Description.getText();
            int expenseCategory = Category.getSelectedIndex();
    
            Expense newExpense = manager.addExpense(expenseAmount, expenseDescription, expenseCategory);
            updateUI(newExpense, model, title);
        } catch (NumberFormatException e) {
            title.setText("Invalid input. Please enter a valid number.");
        }
    }

    // public static void viewExpenses() {
    //     for (Expense expense : expenses) {
    //         System.out.println("Amount: " + expense.getAmount());
    //         System.out.println("Description: " + expense.getDescription());
    //         System.out.println("Category: " + expense.getCategory());
    //         System.out.println("-----------------------");
    //     }
    // }

    public static class ButtonRenderer extends JButton implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (row < table.getModel().getRowCount() - 1) {
                setText("X");
            } else {
                setEnabled(false);
            }
            return this;
        }
    }

    public static class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int clickedRow;

        public ButtonEditor(JCheckBox checkBox, JTable table, DefaultTableModel model, ArrayList<Expense> expenses, ExpenseFileHandler fileHandler) {
            super(checkBox);
            System.out.println("New ButtonEditor instance created");
            button = new JButton("X");
            button.setOpaque(true);
            button.addActionListener(e -> {
                Set<Object> Filtered = new HashSet<>();
                for (int i = 0; i < (model.getRowCount() - 1); i++) {
                    Object cellValue = model.getValueAt(i, 0);
                    Filtered.add(cellValue);
                }
                if (Filtered.size() == 1) {
                    int count = -1;
                    Iterator<Expense> iterator = expenses.iterator();
                    while (iterator.hasNext()) {
                        Expense expense = iterator.next();
                        if (expense.getCategory() == categoriesMap2.get(Filtered.iterator().next())) {
                            count++;
                            if (count == clickedRow) {
                                iterator.remove(); // Safely remove the element using the iterator
                            }
                        }
                    }
                } else {
                    expenses.remove(clickedRow);
                }
                model.removeRow(clickedRow);
                fileHandler.saveExpenses(expenses);
                updateTotalRow(model);
                table.removeColumn(table.getColumnModel().getColumn(3));
                table.addColumn(new TableColumn(0, 75, new ButtonRenderer(), new ButtonEditor(new JCheckBox(), table, model, expenses, fileHandler)));
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            clickedRow = row;
            return button;
        }

        public void deleteRow(int clickedRow, DefaultTableModel model, ArrayList<Expense> expenses, ExpenseFileHandler fileHandler, JTable expenseTable) {
            int modelRow = expenseTable.convertRowIndexToModel(clickedRow);
            Set<Object> Filtered = new HashSet<>();
            for (int i = 0; i < (model.getRowCount() - 1); i++) {
                Object cellValue = model.getValueAt(i, 0);
                Filtered.add(cellValue);
            }
            if (Filtered.size() == 1) {
                int count = -1;
                Iterator<Expense> iterator = expenses.iterator();
                while (iterator.hasNext()) {
                    Expense expense = iterator.next();
                    if (expense.getCategory() == categoriesMap2.get(Filtered.iterator().next())) {
                        count++;
                        if (count == modelRow) {
                            iterator.remove(); // Safely remove the element using the iterator
                        }
                    }
                }
            } else {
                expenses.remove(modelRow);
            }
            model.removeRow(modelRow);
            fileHandler.saveExpenses(expenses);
            updateTotalRow(model);
        }
        
    }

    public static void updateTotalRow(DefaultTableModel model) {
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

    public static void refreshTable(JTable table, DefaultTableModel expenseModel, ArrayList<Expense> freshExpenses, int filter, ExpenseFileHandler fileHandler){
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
        table.repaint();
        updateTotalRow(expenseModel);
        table.removeColumn(table.getColumnModel().getColumn(3));
        table.addColumn(new TableColumn(0, 75, new ButtonRenderer(), new ButtonEditor(new JCheckBox(), table, expenseModel, freshExpenses, fileHandler)));
    }

    public static void main(String[] args) {
        ExpenseManager expenseManager = new ExpenseManager();
        ExpenseFileHandler fileHandler = new ExpenseFileHandler(fileName);
        expenseManager.loadExpensesFromFile(fileHandler);

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
        expenseManager.loadExpensesFromFile(fileHandler);
        for (Expense expense : expenseManager.getExpenses()) {
            expenseModel.addRow(new Object[]{expense.getCategory(), expense.getDescription(), expense.getAmount()});
        }
        expenseModel.addRow(new Object[]{"", "Total", 0});
        updateTotalRow(expenseModel);
        JTable expenseTable = new JTable(expenseModel);
        JScrollPane viewPanel = new JScrollPane(expenseTable);
        TableColumn deleteColumn = expenseTable.getColumnModel().getColumn(3);
        deleteColumn.setCellRenderer(new ButtonRenderer());
        deleteColumn.setCellEditor(new ButtonEditor(new JCheckBox(), expenseTable, expenseModel, expenseManager.getExpenses(), fileHandler));
        //expenseTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        //expenseTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), expenseTable, expenseModel, expenseManager.getExpenses(), fileHandler));
        centerPanel.add(addPanel, "Add");
        centerPanel.add(viewPanel, "View");
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
            expenseManager.addExpense(Double.parseDouble(amountField.getText()), descriptionField.getText(), categoryField.getSelectedIndex());
            if (fileHandler.saveExpenses(expenseManager.getExpenses())){
                appLabel.setText("Expense saved.");
            }
            else {
                appLabel.setText("Error saving expense.");
            }
            refreshTable(expenseTable, expenseModel, expenseManager.getExpenses(), setCategory.getSelectedIndex(), fileHandler);
        });
        viewButton.addActionListener(e -> {
            appLabel.setText("Expenses");
            cl.show(centerPanel, "View");
            addButton.setVisible(false);
            viewButton.setVisible(false);
            addButton2.setVisible(true);
            setCategory.setVisible(true);
            refreshTable(expenseTable, expenseModel, expenseManager.getExpenses(), setCategory.getSelectedIndex(), fileHandler);
            // expenseTable.removeColumn(expenseTable.getColumnModel().getColumn(3));
            // expenseTable.addColumn(deleteColumn);
            // deleteColumn.setCellRenderer(new ButtonRenderer());
            // deleteColumn.setCellEditor(new ButtonEditor(new JCheckBox(), expenseTable, expenseModel, expenseManager.getExpenses(), fileHandler));
        });
        addButton2.addActionListener(e -> {
            appLabel.setText("Expense Tracker");
            cl.show(centerPanel, "Add");
            addButton.setVisible(true);
            viewButton.setVisible(true);
            addButton2.setVisible(false);
            setCategory.setVisible(false);
        });
        setCategory.addActionListener(e -> {
            refreshTable(expenseTable, expenseModel, expenseManager.getExpenses(), setCategory.getSelectedIndex(), fileHandler);
            //expenseTable.removeColumn(expenseTable.getColumnModel().getColumn(3));
            //expenseTable.addColumn(deleteColumn);
            //deleteColumn.setCellRenderer(new ButtonRenderer());
            //deleteColumn.setCellEditor(new ButtonEditor(new JCheckBox(), expenseTable, expenseModel, expenseManager.getExpenses(), fileHandler));
            //expenseTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
            //expenseTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), expenseTable, expenseModel, expenseManager.getExpenses(), fileHandler));
        });
        // ... (add action listeners)
        actionPanel.add(addButton);
        actionPanel.add(viewButton);
        actionPanel.add(addButton2);
        actionPanel.add(setCategory);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        // Add main panel to frame and show it
        frame.add(mainPanel);
        frame.setVisible(true);
    }
}
