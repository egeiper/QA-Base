public enum TimePatterns {

    HH_MM("HH:mm"),
    HH_MM_SS("HH:mm:ss");

    private String timePattern;

    TimePatterns(final String pattern) {
        this.timePattern = pattern;
    }

    public String getPattern() {
        return timePattern;
    }
}
