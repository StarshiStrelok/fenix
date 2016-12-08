package fenix.vo;

public enum TypeEntry {
	TOOTHPASTE(0, "Зубная паста"),
	SOAP(1, "Мыло"),
	COSMETICS(2, "Косметика"),
	COLORS(3, "Краски"),
	DEODORANT(4, "Дезодорант"),
	SHAMPOO(5, "Шампунь"),
	DETERGENTS(6, "СМС"),
	STRIP(7, "Прокладки"),
	PARFUME(8, "Парфюм"),
	BELITA(9, "Белита"),
	VITEX(10, "Витекс"),
	CLEANING(11, "Чистящие средства"),
	HOUSEHOLD_GOODS(12, "Хозяйственные товары"),
	NIVEJA(13, "Нивея");
	
	private int number;
	private String description;
	
	private TypeEntry(int number, String description) {
		this.number = number;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public int getNumber() {
		return number;
	}
}
