package Models;

public class Food {
    private String Name;
    private int Quantity;
    private int Price;

    public Food(String name, int quantity, int price) {
        Name = name;
        Quantity = quantity;
        Price = price;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }
}
