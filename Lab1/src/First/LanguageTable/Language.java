package First.LanguageTable;

import javafx.beans.property.*;

public class Language {

    private StringProperty name;
    private IntegerProperty year;
    private StringProperty author;

    public Language(String name, String author, int year) {
        this.name = new SimpleStringProperty(name);
        this.author = new SimpleStringProperty(author);
        this.year = new SimpleIntegerProperty(year);
    }

    public StringProperty nameProperty(){
        return name;
    }

    public StringProperty authorProperty() {
        return author;
    }

    public IntegerProperty yearProperty() {
        return year;
    }
}
