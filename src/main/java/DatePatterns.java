public enum DatePatterns {
    DD_MM_YYYY("dd/MM/yyyy"),
    MM_DD_YYYY("MM-dd-yyyy"),
    YYYY_MM_DD("yyyy-MM-dd"),
    DD_MM_YYYY_hh_mm("dd/MM/yyyy HH:mm"),
    YYYY_MM_DD_hh_mm("yyyy-MM-dd HH:mm");

    private String datePattern;

    DatePatterns(final String pattern) {
        this.datePattern = pattern;
    }

    public String getPattern() {
        return datePattern;
    }
    }
