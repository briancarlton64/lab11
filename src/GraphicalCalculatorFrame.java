import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Class representing a Calculator with Graphical elements.
 *
 * @author Stephen
 * @version 2019-04-17
 */
@SuppressWarnings("serial")
public class GraphicalCalculatorFrame extends JFrame {
    private static final int FRAME_WIDTH = 500;
    private static final int FRAME_HEIGHT = 700;

    /**
     * Interactive panel that the user clicks on to modify portions of an equation. The panel effectively works as
     * a simple equation editor. The panel has 5 regions that may be clicked - 3 operand regions and 2 operator
     * regions. That is, the Panel displays an expression with 5 editable regions. Regions are marked by a black
     * bounding box and when the region is clicked it is "selected" and highlighted yellow.
     * <p>
     * The equation represented by this panel is of the form:
     * operand0 operator0 operand1 operator1 operand2 = result
     */
    @SuppressWarnings("unused")
    private final class GraphicalCalculatorPanel extends JPanel implements MouseListener {
        /**
         * Width and height for the panel. Width matches the enclosing frame.
         */
        private static final int PANEL_WIDTH = FRAME_WIDTH;
        private static final int PANEL_HEIGHT = 300;

        /**
         * Size of the regions.
         */
        private static final int REGION_WIDTH = 50;
        private static final int REGION_HEIGHT = 50;

        /**
         * Define top-left corner of first region and x increment between corners.
         */
        private static final int REGION_START_X = 50;
        private static final int REGION_START_Y = 50;
        private static final int REGION_INC_X = 60;


        /**
         * Color to highlight the selected region:
         */
        Color highlight = new Color(255, 255, 0, 127);

        /**
         * The editable regions. These are stored as rectangles to represent where to draw them as well as to
         * represent the area on the panel that selects the region when clicked.
         * <p>
         * regions[0] refers to the left-most region displayed
         */
        Rectangle[] regions;

        /**
         * The points at which the parts of the equation are drawn (operators, operands, = sign, result). The regions
         * are drawn around the displayed text for the editable parts of the equation.
         */
        Point[] textPoints;

        /**
         * The region selected that should be highlighted yellow.
         */
        private int selectedRegion = 0;

        /**
         * The operands of the equation. Used for evaluation and for drawing the expression.
         */
        private int[] operands = {0, 0, 0};

        /**
         * The operators of the equation. Used for evaluation and for drawing the expression.
         */
        private String[] operators = {"+", "+"};

        /**
         * Creates the panel and sets up the listeners and member variables.
         */
        public GraphicalCalculatorPanel() {
            this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
            this.setupRegions();
            this.setupTextPoints();
            this.addMouseListener(this);
        }

        /**
         * Set up the regions that may be clicked.
         * <p>
         * The regions are associated with the operands and operators as such:
         * region0 = operand0
         * region1 = operator0
         * region2 = operand1
         * region3 = operator1
         * region4 = operand2
         */
        private void setupRegions() {
            // The regions should be spaced horizontally by REGION_INC_X
            // The regions should be the same size
            // There should be 5 regions
            regions = new Rectangle[5];
            for (int i = 0; i < regions.length; i++) {
                regions[i] = new Rectangle(REGION_INC_X + i * REGION_INC_X, REGION_START_Y, REGION_WIDTH, REGION_HEIGHT);
            }
        }

        /**
         * Sets up the points at which text is drawn (i.e. the parts of the equation)...
         */
        private void setupTextPoints() {
            textPoints = new Point[7];

            int startX = REGION_START_X + 20;
            int startY = REGION_START_Y + 30;

            for (int pt = 0; pt < textPoints.length; pt++) {
                textPoints[pt] = new Point(startX + pt * REGION_INC_X, startY);
            }
        }

        /**
         * Draws the bounding boxes for all the regions in the panel.
         * <p>
         * Draws the text representing the expression.
         * <p>
         * Highlights the selected region.
         *
         * @param g The graphics object for drawing.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            //Draw bounding boxes around each region.
            for (Rectangle rec : regions) {
                g.drawRect(rec.x, rec.y, rec.width, rec.height);
            }


            // Draw the text at the specified text points:
            // Pattern is: operand operator operand operator operand = result
            for (int pt = 0; pt < textPoints.length; pt++) {
                String drawString = "";
                switch (pt) {
                    case 0:
                        drawString = "" + operands[0];
                        break;
                    case 1:
                        drawString = operators[0];
                        break;
                    case 2:
                        drawString = "" + operands[1];
                        break;
                    case 3:
                        drawString = operators[1];
                        break;
                    case 4:
                        drawString = "" + operands[2];
                        break;
                    case 5:
                        drawString = "=";
                        break;
                    case 6:
                        drawString = "" + this.evaluate();
                        break;
                }
                g.drawString(drawString, textPoints[pt].x, textPoints[pt].y);
            }
            //Draw a translucent rectangle on the selected region.
            g.setColor(new Color(255, 255, 0, 127));
            g.fillRect(regions[selectedRegion].x, regions[selectedRegion].y,
                    regions[selectedRegion].width, regions[selectedRegion].height);
        }

        /**
         * When the panel is clicked, check if the point clicked lies within any of the editable regions
         * of the expression. If so, that region set to the be the new selected region. Repaint at the
         * end.
         * <p>
         * (hint: look at the java.awt.Rectangle class for useful methods that you can use to check if
         * a point is within the rectangle)
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            // If the mouse clicked within a region, set that region to be the selected region.
            for (int i = 0; i < regions.length; i++) {
                if (regions[i].contains(e.getPoint())) {
                    selectedRegion = i;
                }
            }

            // Repaint the panel (this will implicitly call paintComponent):
            this.repaint();
        }

        /**
         * Attempts to set the content of the selected region. If the region is and operand the content must be
         * a single-digit number (0-9). If the region is an operator the content must be "+", "-", or "*".
         * <p>
         * Should call repaint at the end.
         *
         * @param content The value that the program will attempt to set the selected region to.
         * @return True if the region content is sucessfully set. False otherwise.
         */
        public boolean setSelectedRegionContents(String content) {

            if ((selectedRegion + 2) % 2 == 0 && (content.equals("1") || content.equals("2") ||
                    content.equals("3") || content.equals("4") || content.equals("5") ||
                    content.equals("6") || content.equals("7") || content.equals("8") ||
                    content.equals("9") || content.equals("0"))) {
                operands[selectedRegion / 2] = Integer.parseInt(content);
                this.revalidate();
                this.repaint();
                return true;
            }
            if ((selectedRegion + 2) % 2 == 1 && (content.equals("+") || content.equals("-") ||
                    content.equals("*"))) {
                operators[(selectedRegion - 1) / 2] = (content);
                this.revalidate();
                this.repaint();
                return true;
            }


            this.revalidate();
            this.repaint();

            return false;
        }

        /**
         * Evaluates the equation on the panel. Operations are performed left-to-right, ignoring operator precendence
         * (e.g. and equation of 2+3*4 will return (2+3)*4 = 20)
         *
         * @return The evaluation of the expression displayed by the Panel.
         */
        public int evaluate() {
            int sum1 = operands[0];
            switch (operators[0]) {
                case "+":
                    sum1 += operands[1];
                    break;
                case "-":
                    sum1 -= operands[1];
                    break;
                case "*":
                    sum1 *= operands[1];
                    break;
            }
            switch (operators[1]) {
                case "+":
                    sum1 += operands[2];
                    break;
                case "-":
                    sum1 -= operands[2];
                    break;
                case "*":
                    sum1 *= operands[2];
                    break;
            }
            return sum1;
        }

        /**
         * DO NOT MODIFY - DOES NOTHING
         */
        @Override
        public void mousePressed(MouseEvent e) {
        }

        /**
         * DO NOT MODIFY - DOES NOTHING
         */
        @Override
        public void mouseReleased(MouseEvent e) {
        }

        /**
         * DO NOT MODIFY - DOES NOTHING
         */
        @Override
        public void mouseEntered(MouseEvent e) {
        }

        /**
         * DO NOT MODIFY - DOES NOTHING
         */
        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    /**
     * panel for displaying and interacting with the expression
     */
    GraphicalCalculatorPanel gcPanel = new GraphicalCalculatorPanel();

    //==================================================================================================================
    // Panels for component grouping and organization:
    //==================================================================================================================

    /**
     * panel to hold the non-GraphicalCalculatorPanel panels
     */
    JPanel panel0 = new JPanel(new GridLayout(2, 2));
    /**
     * panel for operand text entry
     */
    JPanel panel1 = new JPanel();
    /**
     * panel for the radio buttons
     */
    JPanel panel2 = new JPanel(new GridLayout(3, 0));
    /**
     * panel for the set operand/operator buttons
     */
    JPanel panel3 = new JPanel(new GridLayout(3, 0));
    /**
     * panel for the error message
     */
    JPanel panel4 = new JPanel();

    //==================================================================================================================
    // Operand Entry:
    //==================================================================================================================

    /**
     * Text field for the user's number input
     */
    JTextField operandEntry = new JTextField("00000");

    /**
     * Button to attempt to set the selected region in the Graphical panel to the value in the operand
     * entry text field.
     */
    JButton setOperand = new JButton("Set Operand");

    //==================================================================================================================
    // Operator Entry:
    //==================================================================================================================

    /**
     * Group of operation buttons
     */
    ButtonGroup ops = new ButtonGroup();

    /**
     * add operation radio button
     */
    JRadioButton add = new JRadioButton("+");

    /**
     * divide operation radio button
     */
    JRadioButton subtract = new JRadioButton("-");
    /**
     * multiply operation radio button
     */
    JRadioButton multiply = new JRadioButton("*");

    /**
     * Button to attempt to set the selected region in the Graphical panel to the selected operator (as defined by
     * the radio buttons).
     */
    JButton setOperator = new JButton("Set Operator");

    //==================================================================================================================
    // Misc.
    //==================================================================================================================

    /**
     * Text that display an error message
     */
    JLabel errorMessage = new JLabel();

    //==================================================================================================================
    // Main and constructor:
    //==================================================================================================================

    /**
     * This method builds and operates the GUI window.
     */
    public GraphicalCalculatorFrame() {
        super("GraphicalCalculatorFrame");

        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.setLayout(new GridLayout(2, 0));

        // Add components to panels

        panel1.add(operandEntry);
        panel2.add(setOperand);
        panel2.add(setOperator);
        panel3.add(add);
        panel3.add(subtract);
        panel3.add(multiply);
        panel4.add(errorMessage);
        // Add radio buttons to the button group
        ops.add(add);
        ops.add(subtract);
        ops.add(multiply);
        add.setActionCommand("+");
        subtract.setActionCommand("-");
        multiply.setActionCommand("*");
        //default to + operator
        add.setSelected(true); //remember, the button group ensures only one button is selected

        //Add all subpanels to panel0
        panel0.add(panel1);
        panel0.add(panel2);
        panel0.add(panel3);
        panel0.add(panel4);
        // Adds all panels to frame:
        panel0.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT - 300));

        this.add(panel0);
        this.add(gcPanel);

        // Set ActionListeners on buttons:

        /*
         * Attempts to set the selected region in gcPanel to the operand value in the operandEntry textbox.
         * If the set operation fails, display the error message "Failed to set operand value".
         * If the set operation succeeds, clear any error messages.
         */
        setOperand.addActionListener((e) -> {
                    if (gcPanel.setSelectedRegionContents(operandEntry.getText())) {
                        errorMessage.setText("");
                    } else {
                        errorMessage.setText("Failed to set operand.");
                    }
                }
        );

        /*
         * Attempts to set the selected region in gcPanel to the selected operator (which radio button is pressed).
         * Pass the string:
         * 	"+" if the add button is selected
         *  "-" if the subtract button is selected
         *  "*" if the multiply button is selected
         *
         * If the set operation fails, display the error message "Failed to set operator value".
         * If the set operation succeeds, clear any error messages.
         */
        setOperator.addActionListener((e) -> {
                    if (gcPanel.setSelectedRegionContents(ops.getSelection().getActionCommand())) {
                        errorMessage.setText("");
                    } else {
                        errorMessage.setText("Failed to set operator.");
                    }
                }
        );

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    /**
     * Main method to the program. Creates a new GraphicalCalculatorFrame object,
     * calling its constructor.
     *
     * @param args The program arguments.
     */
    public static void main(String[] args) {
        new GraphicalCalculatorFrame();
    }
}
