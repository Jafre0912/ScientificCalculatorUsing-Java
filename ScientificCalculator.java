import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.script.*;

public class ScientificCalculator extends JFrame {

    private JButton[] jButtons;
    private JTextField jTextField;
    private Calculator calculator;

    private static final int BUTTON_WIDTH = 65;
    private static final int BUTTON_HEIGHT = 40;
    private static final int BUTTON_PER_LINE = 7;

    public ScientificCalculator() {
        calculator = new Calculator();
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Scientific Calculator");
        setSize(500, 400);
        setResizable(false);
        setLocationRelativeTo(null); // Center the window

        jTextField = new JTextField();
        jTextField.setEditable(false);
        jTextField.setHorizontalAlignment(JTextField.RIGHT);
        jTextField.setPreferredSize(new Dimension(480, 40));

        String[] buttonsText = {
            "(", ")", "abs", "", "BACK", "C", "/",
            "ceil", "floor", "log", "7", "8", "9", "*",
            "sin", "asin", "exp", "4", "5", "6", "-",
            "cos", "acos", "sqrt", "1", "2", "3", "+",
            "tan", "atan", "round", "0", ".", "%", "="
        };

        jButtons = new JButton[buttonsText.length];
        ActionListener btnActionListener = this::jButtonActionPerformed;

        for (int i = 0; i < buttonsText.length; i++) {
            jButtons[i] = new JButton(buttonsText[i]);
            jButtons[i].setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
            jButtons[i].addActionListener(btnActionListener);
            if (buttonsText[i].isEmpty()) {
                jButtons[i].setEnabled(false);
            }
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, BUTTON_PER_LINE, 5, 5));

        for (JButton button : jButtons) {
            buttonPanel.add(button);
        }

        setLayout(new BorderLayout());
        add(jTextField, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void jButtonActionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();
        
        // Handle the input through the calculator
        calculator.addInput(command);

        // Update the display based on the input
        if (command.equals("=")) {
            String output = calculator.calculate();
            jTextField.setText(output);
        } else {
            String expression = calculator.buildExpression();
            jTextField.setText(expression);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ScientificCalculator::new);
    }
}

class Calculator {
    private ScriptEngine engine;
    private ArrayList<String> list;
    private int openBracket;

    public Calculator() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("JavaScript");
        list = new ArrayList<>();
        openBracket = 0;
    }

    public void addInput(String input) {
        switch (input) {
            case "=":
                break; // Do nothing, handled in jButtonActionPerformed
            case "C":
                reset();
                break;
            case "BACK":
                handleBackspace();
                break;
            case "(":
                handleOpenBracket();
                break;
            case ")":
                handleCloseBracket();
                break;
            default:
                handleInput(input);
                break;
        }
    }

    private void handleBackspace() {
        if (!list.isEmpty()) {
            list.remove(list.size() - 1);
        }
    }

    private void handleOpenBracket() {
        list.add("(");
        openBracket++;
    }

    private void handleCloseBracket() {
        if (openBracket > 0) {
            list.add(")");
            openBracket--;
        }
    }

    private void handleInput(String input) {
        list.add(input);
    }

    public String buildExpression() {
        StringBuilder str = new StringBuilder();
        for (String s : list) {
            str.append(s);
        }
        for (int i = 0; i < openBracket; i++) {
            str.append(")");
        }
        return str.toString();
    }

    private void reset() {
        openBracket = 0;
        list.clear();
    }

    public String calculate() {
        String expression = buildExpression();
        try {
            Object result = engine.eval(expression);
            reset(); // Clear the input after calculating
            return result != null ? result.toString() : "Invalid Expression";
        } catch (ScriptException e) {
            reset(); // Clear the input on error
            return "Error: " + e.getMessage();
        }
    }
}
