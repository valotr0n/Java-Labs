package lab6.web;

public final class Html {
    private Html() {
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public static String page(String title, String body) {
        return "<!doctype html><html lang=\"ru\"><head>"
                + "<meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                + "<title>" + escape(title) + "</title>"
                + "<link rel=\"stylesheet\" href=\"resources/css/style.css\">"
                + "</head><body><main class=\"container\">"
                + body
                + "</main></body></html>";
    }

    public static String value(String value) {
        return " value=\"" + escape(value) + "\"";
    }
}