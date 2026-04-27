package einfprog;

public class Date {

    private int year;
    private int month;
    private int day;

    public Date(int year, int month, int day) {
        if (year >= 2022 && year <= 9999 && month >= 1 && month <= 12 && day >= 1 && day <= 31) {
            this.year = year;
            this.month = month;
            this.day = day;
        } else
            throw new IllegalArgumentException("Invalid date");
    }

    public int compareTo(Date d) {
        int ySign = Integer.signum(this.year - d.year);
        int mSign = Integer.signum(this.month - d.month);
        int dSign = Integer.signum(this.day - d.day);

        if (ySign != 0)
            return ySign;
        if (mSign != 0)
            return mSign;
        return dSign;
    }

    public String toString() {
        return String.format("%d-%02d-%02d", year, month, day);
    }
}
