package app;


public enum Cities {
    Atlanta(1), Boston(2), Charleston(3), Newark(4), Manchester(5), Raleigh(6), Jacksonville(7), Birmingham(8),
    Memphis(9), Louisville(10), StLouis(11), Topeka(12), Denver(13), Minneapolis(14), Cheyenne(15), Phonenix(16),
    Boise(17), Portland(18), Seattle(19), Helena(20), NewYork(21), Philadelphia(22), Austin(23), Miami(24), Chicago(25),
    Cleveland(26), Omaha(27), Dallas(28), Lansing(29), Columbus(30), Worcester(31), Providence(32);

    private final int cityCode;

    private Cities(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getCityCode() {
        return this.cityCode;
    }

    public static Cities valueOfCode(int code) {
        for (Cities e : values()) {
            if (e.cityCode == code) {
                return e;
            }
        }
        return null;
    }

}
