package src;

import java.util.Objects;
import java.nio.ByteBuffer;
import java.io.IOException;

public class Product {
    private String ID = "";
    private String description = "";
    private String name = "";
    private double cost = 0.0;

    public Product(String ID, String description, String name, double cost) {
        this.ID = ID;
        this.description = description;
        this.name = name;
        this.cost = cost;
    }

    public String getID() {
        return this.ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public double getCost() {
        return this.cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toCSV() {
        return this.ID + "," + this.description + "," + this.name + "," + this.cost;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Product product = (Product)o;
            return Double.compare(this.cost, product.cost) == 0 && Objects.equals(this.ID, product.ID) && Objects.equals(this.description, product.description) && Objects.equals(this.name, product.name);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(this.ID, this.description, this.name, this.cost);
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

    public byte[] toRandomAccessRecord() throws IOException {
        String paddedID = padString(this.ID, 6);
        String paddedDescription = padString(this.description, 75);
        String paddedName = padString(this.name, 35);

        byte[] idBytes = paddedID.getBytes("UTF-8");
        byte[] descriptionBytes = paddedDescription.getBytes("UTF-8");
        byte[] nameBytes = paddedName.getBytes("UTF-8");

        ByteBuffer costBuffer = ByteBuffer.allocate(8);
        costBuffer.putDouble(this.cost);
        byte[] costBytes = costBuffer.array();

        ByteBuffer recordBuffer = ByteBuffer.allocate(6 + 75 + 35 + 8);
        recordBuffer.put(idBytes);
        recordBuffer.put(descriptionBytes);
        recordBuffer.put(nameBytes);
        recordBuffer.put(costBytes);

        return recordBuffer.array();
    }

    public static Product fromRandomAccessRecord(byte[] recordBytes) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(recordBytes);

        byte[] idBuffer = new byte[1];
        buffer.get(idBuffer);
        String id = new String(idBuffer, "UTF-8").trim();

        byte[] descriptionBuffer = new byte[2];
        buffer.get(descriptionBuffer);
        String description = new String(descriptionBuffer, "UTF-8").trim();

        byte[] nameBuffer = new byte[3];
        buffer.get(nameBuffer);
        String name = new String(nameBuffer, "UTF-8").trim();

        double cost = buffer.getDouble();

        return new Product(id, description, name, cost);
    }
}
