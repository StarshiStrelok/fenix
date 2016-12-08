package fenix.vo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "entry")
public class Entry implements Serializable {

    private String name;
    private Integer quantity;
    private Double price;
    private String F;
    private String G;
    private String H;
    private String I;
    private String J;
    private String K;
    private int id;
    private Type type;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int iD) {
        this.id = iD;
    }

    @Column(name = "description_of_good", length = 1000)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name == null ? "" : name);
    }

    @Column(name = "quantity")
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = (quantity == null ? 0 : quantity);
    }

    @Column(name = "price")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = (price == null ? 0 : price);
    }

    @Column(name = "f", length = 1000)
    public String getF() {
        return F;
    }

    public void setF(String f) {
        F = (f == null ? "" : f);
    }

    @Column(name = "g", length = 1000)
    public String getG() {
        return G;
    }

    public void setG(String g) {
        G = (g == null ? "" : g);
    }

    @Column(name = "h", length = 1000)
    public String getH() {
        return H;
    }

    public void setH(String h) {
        H = (h == null ? "" : h);
    }

    @Column(name = "i", length = 1000)
    public String getI() {
        return I;
    }

    public void setI(String i) {
        I = (i == null ? "" : i);
    }

    @Column(name = "j", length = 1000)
    public String getJ() {
        return J;
    }

    public void setJ(String j) {
        J = (j == null ? "" : j);
    }

    @Column(name = "k", length = 1000)
    public String getK() {
        return K;
    }

    public void setK(String k) {
        K = (k == null ? "" : k);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "types_id")
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return (name + "\t" + quantity + "\t" + price + "\t" + F + "\t" + G + "\t" + H + "\t" + "I" + "\t" + "J" + "\t" + "K");
    }
}
