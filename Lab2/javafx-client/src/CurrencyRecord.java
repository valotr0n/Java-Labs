class CurrencyRecord {
    private String date;
    private String value;  
    
    public CurrencyRecord(String date, String value) {
        this.date = date;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public String getValue() {
        return value;
    }
}