import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventObject;

public class Main {
    private static ArrayList<Double> incomes = new ArrayList<>();
    private static ArrayList<Double> expenses = new ArrayList<>();
    private static JLabel totalIncomeValue = new JLabel("0.0");
    private static JLabel totalExpenseValue = new JLabel("0.0");
    private static JLabel balanceValue = new JLabel("0.0");

    private static DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Tipo", "Descrição", "Quantia", "Deletar"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 3;
        }
    };

    public static void main(String[] args) {
        JFrame frame = new JFrame("Organizador de Finanças Pessoais");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 600);
        frame.setLocationRelativeTo(null);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));

        JButton addIncomeButton = new JButton("Adicionar receita");
        JButton addExpenseButton = new JButton("Adcionar despesa");

        Font buttonFont = new Font("Arial", Font.PLAIN, 12);
        addIncomeButton.setFont(buttonFont);
        addExpenseButton.setFont(buttonFont);

        addIncomeButton.addActionListener(e -> openAddDialog("Receita"));
        addExpenseButton.addActionListener(e -> openAddDialog("Despesa"));

        buttonPanel.add(addIncomeButton);
        buttonPanel.add(addExpenseButton);

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        table.getColumn("Deletar").setCellRenderer(new ButtonRenderer());
        table.getColumn("Deletar").setCellEditor(new ButtonEditor(new JCheckBox(), table));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 6));

        JLabel totalIncomeLabel = new JLabel("Receita Total:");
        JLabel totalExpenseLabel = new JLabel("Despesa Total:");
        JLabel balanceLabel = new JLabel("Saldo:");

        Color green = new Color(0, 102, 0);
        Color red = new Color(102, 0, 0);
        Color blue = new Color(0, 0, 102);

        Font labelFont = new Font("Arial", Font.BOLD, 16);
        totalIncomeLabel.setFont(labelFont);
        totalIncomeLabel.setForeground(green);
        totalIncomeValue.setFont(labelFont);
        totalIncomeValue.setForeground(green);
        totalExpenseLabel.setFont(labelFont);
        totalExpenseLabel.setForeground(red);
        totalExpenseValue.setFont(labelFont);
        totalExpenseValue.setForeground(red);
        balanceLabel.setFont(labelFont);
        balanceLabel.setForeground(blue);
        balanceValue.setFont(labelFont);
        balanceValue.setForeground(blue);

        bottomPanel.add(totalIncomeLabel);
        bottomPanel.add(totalIncomeValue);
        bottomPanel.add(totalExpenseLabel);
        bottomPanel.add(totalExpenseValue);
        bottomPanel.add(balanceLabel);
        bottomPanel.add(balanceValue);

        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static void openAddDialog(String type) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Adicionar " + type);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new GridLayout(4, 2));

        JLabel labelLabel = new JLabel("Descrição:");
        JTextField labelField = new JTextField();
        JLabel amountLabel = new JLabel("Quantia:");
        JTextField amountField = new JTextField();
        JButton addButton = new JButton("Adicionar");

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                double amount = Double.parseDouble(amountField.getText());
                String label = labelField.getText();
                if (type.equals("Receita")) {
                    incomes.add(amount);
                    tableModel.addRow(new Object[]{"Receita", label, amount, "Deletar"});
                } else {
                    expenses.add(amount);
                    tableModel.addRow(new Object[]{"Despesa", label, amount, "Deletar"});
                }
                updateBalance();
                dialog.dispose();
            }
        });

        dialog.add(labelLabel);
        dialog.add(labelField);
        dialog.add(amountLabel);
        dialog.add(amountField);
        dialog.add(new JLabel());
        dialog.add(addButton);

        dialog.setVisible(true);
    }

    private static void updateBalance() {
        double totalIncome = incomes.stream().mapToDouble(Double::doubleValue).sum();
        double totalExpense = expenses.stream().mapToDouble(Double::doubleValue).sum();
        double balance = totalIncome - totalExpense;
        totalIncomeValue.setText(String.valueOf(totalIncome));
        totalExpenseValue.setText(String.valueOf(totalExpense));
        balanceValue.setText(String.valueOf(balance));
    }

    // Renderer for the "Delete" button
    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Deletar");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }


    static class ButtonEditor extends DefaultCellEditor {
        private JTable table;
        private JButton button;
        private int row;

        public ButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            this.table = table;
            this.button = new JButton("Deletar");

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int modelRow = table.convertRowIndexToModel(row);
                    String type = (String) tableModel.getValueAt(modelRow, 0);
                    double amount = (double) tableModel.getValueAt(modelRow, 2);

                    // Remove the row from the table model
                    tableModel.removeRow(modelRow);

                    // Remove from incomes or expenses by index, instead of amount
                    if (type.equals("Receita")) {
                        incomes.remove(modelRow);  // Use index to remove
                    } else if (type.equals("Despesa")) {
                        expenses.remove(modelRow);  // Use index to remove
                    }
                    updateBalance();
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            return button;
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            return true;
        }
    }
}