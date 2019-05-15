package webserver.resource;

public enum MimeType {
    TEXT("text/plain", ".*\\.txt"),
    ICON("application/x-icon", ".*\\.ico$"),
    HTML("text/html", ".*\\.html$"),
    JS("application/x-javascript", ".*\\.js$"),
    JPEG("image/jpeg", ".*\\.(jpeg|jpg)$"),
    BINARY("application/octet-stream", ".*");

    private String name;
    private String pattern;

    MimeType(String name, String extension) {
        this.name = name;
        this.pattern = extension;
    }

    public String getName() {
        return name;
    }

    public String getPattern() {
        return pattern;
    }

    public static MimeType fromString(String s) {
        for (MimeType type : MimeType.values()) {
            if (s.matches(type.getPattern())) {
                return type;
            }
        }
        return MimeType.BINARY;
    }
}
