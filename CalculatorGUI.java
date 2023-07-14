import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalculatorGUI {

    private JTextField inputField;
    private JLabel resultLabel;

    public CalculatorGUI() {
        JFrame frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 400);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = createInputPanel();
        JPanel buttonPanel = createButtonPanel();

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);

        
        frame.setVisible(true);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Font f = new Font("Calibri", Font.PLAIN, 30);
        Font f1 = new Font("Calibri", Font.PLAIN, 20);
        inputField = new JTextField(15);
        inputField.setFont(f);
        resultLabel = new JLabel("Result: ");
        resultLabel.setFont(f);
        JButton clear = new JButton("AC");
        clear.setFont(f1);
        panel.add(inputField, BorderLayout.NORTH);
        panel.add(resultLabel);
        panel.add(clear, BorderLayout.SOUTH);
        ActionListener a = (ae) -> {
            inputField.setText("");
            resultLabel.setText("Result: ");
            inputField.requestFocus();
        };
        clear.addActionListener(a);
        return panel;
    }

    private JPanel createButtonPanel() {
        
        JPanel panel = new JPanel(new GridLayout(4, 4, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] buttons = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+"
        };

        
        for (String button : buttons) {
            JButton btn = new JButton(button);
            btn.addActionListener(new ButtonClickListener());
            panel.add(btn);
        }
        
        return panel;
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            switch (command) {
                case "=":
                    evaluateExpression();
                    break;
                default:
                    inputField.setText(inputField.getText() + command);
                    break;
            }
        }

        private void evaluateExpression() {
            String expression = inputField.getText();
            if(expression.isEmpty())
            {
                JOptionPane.showMessageDialog(null, "NO INPUT", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                try {
                        double result = eval(expression);
                        String res = String.valueOf(result);
                        if(res.matches("Infinity"))
                        {
                            resultLabel.setText("Error: Division by 0");
                        }
                        else {
                            resultLabel.setText("Result: " + result);
                        }
                        
                    } catch (ArithmeticException ex) {
                        resultLabel.setText("Error: Division by zero");
                    } catch (NumberFormatException ex) {
                        resultLabel.setText("Error: Invalid input");
                    }
            }
        }

        private double eval(String expression) {
            return new Eval().evaluate(expression);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalculatorGUI());
    }

    private class Eval {
        public double evaluate(String expression) {
            return new Object() {
                int pos = -1, ch;

                void nextChar() {
                    ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
                }

                boolean eat(int charToEat) {
                    while (ch == ' ') nextChar();
                    if (ch == charToEat) {
                        nextChar();
                        return true;
                    }
                    return false;
                }

                double parse() {
                    nextChar();
                    double x = parseExpression();
                    if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                    return x;
                }

                double parseExpression() {
                    double x = parseTerm();
                    for (; ; ) {
                        if (eat('+')) x += parseTerm();
                        else if (eat('-')) x -= parseTerm();
                        else return x;
                    }
                }

                double parseTerm() {
                    double x = parseFactor();
                    for (; ; ) {
                        if (eat('*')) x *= parseFactor();
                        else if (eat('/')) x /= parseFactor();
                        else return x;
                    }
                }

                double parseFactor() {
                    if (eat('+')) return parseFactor();
                    if (eat('-')) return -parseFactor();

                    double x;
                    int startPos = this.pos;
                    if (eat('(')) {
                        x = parseExpression();
                        eat(')');
                    } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                        while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                        x = Double.parseDouble(expression.substring(startPos, this.pos));
                    } else {
                        throw new RuntimeException("Unexpected: " + (char) ch);
                    }

                    return x;
                }
            }.parse();
        }
    }
}