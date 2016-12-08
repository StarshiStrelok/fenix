package fenix.vo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name="type_of_goods")
public class Type {
	
	private int id;
	private String description;
	private List<Entry> entries = new ArrayList<Entry>(0);
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.AUTO)  
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Column(name="description", length=1000) 
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@OneToMany(fetch=FetchType.LAZY, targetEntity=Entry.class, mappedBy="type")
	@OrderBy(value="description_of_good")
	public List<Entry> getEntries() {
		return entries;
	}
	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}
}
