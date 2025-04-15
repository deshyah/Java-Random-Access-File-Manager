package src;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class RandProductMaker extends JFrame implements ActionListener {

    private JPanel mainPnl;
    private JPanel formPnl;
    private JPanel controlPnl;

    private JLabel idLbl;
    private JTextField idTF;

    private JLabel nameLbl;
    private JTextField nameTF;

    private JLabel descriptionLbl;
    private JTextField descriptionTF;

    private JLabel costLbl;
    private JTextField costTF;

    private JButton addBtn;
    private JButton quitBtn;

    private JLabel recordCountLbl;
    private JTextField recordCountTF;
    private int recordCount = 0;

    private RandomAccessFile raf;
    private final int RECORD_SIZE = 6 + 75 + 35 + 8; // Size of each record in bytes

    public RandProductMaker() {
        // **Main Frame Setup**
        setTitle("Random Product Maker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // **Main Panel Setup**
        mainPnl = new JPanel(new BorderLayout());

        // **Create Form Panel**
        createFormPanel();
        mainPnl.add(formPnl, BorderLayout.CENTER);

        // **Create Control Panel**
        createControlPanel();
        mainPnl.add(controlPnl, BorderLayout.SOUTH);

        // **Initialize RandomAccessFile**
        try {
            raf = new RandomAccessFile("products.txt", "rw");
            // Initialize record count based on file size
            recordCount = (int) (raf.length() / RECORD_SIZE);
            recordCountTF.setText(String.valueOf(recordCount));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error initializing file: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            // Consider disabling add functionality if file cannot be opened
        }

        // **Add Main Panel to Frame**
        add(mainPnl);
        setSize(400, 300);
        setLocationRelativeTo(null); // Center the frame
        setVisible(true);
    }

    private void createFormPanel() {
        formPnl = new JPanel(new GridLayout(5, 2, 5, 5)); // 5 rows, 2 columns, with gaps
        formPnl.setBorder(new TitledBorder(new EtchedBorder(), "Product Data Entry"));

        idLbl = new JLabel("Product ID (6 chars):");
        idTF = new JTextField(6);
        formPnl.add(idLbl);
        formPnl.add(idTF);

        nameLbl = new JLabel("Product Name (35 chars):");
        nameTF = new JTextField(35);
        formPnl.add(nameLbl);
        formPnl.add(nameTF);

        descriptionLbl = new JLabel("Description (75 chars):");
        descriptionTF = new JTextField(75);
        formPnl.add(descriptionLbl);
        formPnl.add(descriptionTF);

        costLbl = new JLabel("Cost:");
        costTF = new JTextField(10);
        formPnl.add(costLbl);
        formPnl.add(costTF);

        recordCountLbl = new JLabel("Record Count:");
        recordCountTF = new JTextField(10);
        recordCountTF.setEditable(false);
        recordCountTF.setText("0");
        formPnl.add(recordCountLbl);
        formPnl.add(recordCountTF);
    }

    private void createControlPanel() {
        controlPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));

        addBtn = new JButton("Add");
        addBtn.addActionListener(this);
        controlPnl.add(addBtn);

        quitBtn = new JButton("Quit");
        quitBtn.addActionListener((ActionEvent e) -> {
            try {
                if (raf != null) {
                    raf.close();
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error closing file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
            System.exit(0);
        });
        controlPnl.add(quitBtn);
    }

    private String padString(String text, int length) {
        if (text == null) {
            text = "";
        }
        if (text.length() < length) {
            StringBuilder sb = new StringBuilder(text);
            while (sb.length() < length) {
                sb.append(' ');
            }
            return sb.toString();
        } else if (text.length() > length) {
            return text.substring(0, length);
        }
        return text;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addBtn) {
            String id = idTF.getText().trim();
            String name = nameTF.getText().trim();
            String description = descriptionTF.getText().trim();
            String costStr = costTF.getText().trim();

            if (id.isEmpty() || name.isEmpty() || description.isEmpty() || costStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "**Please fill in all the fields.**", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (id.length() > 6 || name.length() > 35 || description.length() > 75) {
                JOptionPane.showMessageDialog(this, "**Some fields exceed the maximum allowed length.**\nID: 6, Name: 35, Description: 75", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double cost;
            try {
                cost = Double.parseDouble(costStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "**Please enter a valid number for the cost.**", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String paddedID = padString(id, 6);
                String paddedDescription = padString(description, 75);
                String paddedName = padString(name, 35);

                ByteBuffer buffer = ByteBuffer.allocate(RECORD_SIZE);
                buffer.put(paddedID.getBytes("UTF-8"));
                buffer.put(paddedDescription.getBytes("UTF-8"));
                buffer.put(paddedName.getBytes("UTF-8"));
                buffer.putDouble(cost);

                raf.seek(recordCount * RECORD_SIZE); // Move to the end of the current records
                raf.write(buffer.array());

                recordCount++;
                recordCountTF.setText(String.valueOf(recordCount));

                // Clear the input fields
                idTF.setText("");
                nameTF.setText("");
                descriptionTF.setText("");
                costTF.setText("");

                JOptionPane.showMessageDialog(this, "Product record added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error writing to file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        // Use SwingUtilities.invokeLater to ensure thread safety for GUI operations [1]
        SwingUtilities.invokeLater(() -> new RandProductMaker());
    }
}
