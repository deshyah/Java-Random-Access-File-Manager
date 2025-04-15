package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RandProductSearch extends JFrame {

    private JPanel mainPnl;
    private JLabel searchPromptLbl;
    private JTextField searchInputTF;
    private JButton searchBtn;
    private JTextArea resultsTA;
    private JScrollPane resultsSP;

    private final int ID_SIZE = 6;
    private final int NAME_SIZE = 35;
    private final int DESCRIPTION_SIZE = 75;
    private final int DOUBLE_SIZE = 8;
    private final int RECORD_SIZE = ID_SIZE + NAME_SIZE + DESCRIPTION_SIZE + DOUBLE_SIZE;
    private final String DATA_FILE = "products.txt";

    public RandProductSearch() {
        setTitle("Product Search");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        mainPnl = new JPanel(new BorderLayout());

        // Create components
        searchPromptLbl = new JLabel("Enter partial product name:");
        searchInputTF = new JTextField(20);
        searchBtn = new JButton("Search");
        resultsTA = new JTextArea(10, 40);
        resultsTA.setEditable(false);
        resultsSP = new JScrollPane(resultsTA);

        // Create a panel for the input components
        JPanel inputPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPnl.add(searchPromptLbl);
        inputPnl.add(searchInputTF);
        inputPnl.add(searchBtn);

        // Add components to the main panel
        mainPnl.add(inputPnl, BorderLayout.NORTH);
        mainPnl.add(resultsSP, BorderLayout.CENTER);

        // Add action listener to the search button
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String partialName = searchInputTF.getText().trim().toLowerCase();
                resultsTA.setText(""); // Clear previous results

                try (RandomAccessFile raf = new RandomAccessFile(DATA_FILE, "r")) {
                    int numberOfRecords = (int) (raf.length() / RECORD_SIZE);

                    if (numberOfRecords == 0) {
                        resultsTA.append("No product records found in the file.\n");
                        return;
                    }

                    for (int i = 0; i < numberOfRecords; i++) {
                        raf.seek(i * RECORD_SIZE);

                        ByteBuffer buffer = ByteBuffer.allocate(RECORD_SIZE);
                        raf.read(buffer.array());

                        byte[] idBytes = new byte[ID_SIZE];
                        buffer.get(idBytes);
                        String id = new String(idBytes, StandardCharsets.UTF_8).trim();

                        byte[] nameBytes = new byte[NAME_SIZE];
                        buffer.get(nameBytes);
                        String name = new String(nameBytes, StandardCharsets.UTF_8).trim().toLowerCase();

                        byte[] descriptionBytes = new byte[DESCRIPTION_SIZE];
                        buffer.get(descriptionBytes);
                        String description = new String(descriptionBytes, StandardCharsets.UTF_8).trim();

                        double cost = buffer.getDouble();

                        if (name.contains(partialName)) {
                            String productInfo = String.format("ID: %s\nName: %s\nDescription: %s\nCost: $%.2f\n\n", id, name, description, cost);
                            resultsTA.append(productInfo);
                        }
                    }

                    if (resultsTA.getText().isEmpty()) {
                        resultsTA.append("No products found containing \"" + partialName + "\".\n");
                    }

                } catch (IOException ex) {
                    resultsTA.append("Error reading the data file: " + ex.getMessage() + "\n");
                }
            }
        });

        add(mainPnl);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RandProductSearch());
    }
}