public class Movie {
    int id;
    String title;
    String category;
    int year;
    String synopsis;

    public Movie(int id, String title, String category, int year, String synopsis) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.year = year;
        this.synopsis = synopsis;
    }
    
    @Override
    public String toString() {
        return "[" + id + ", " + title + " (" + category + ")]";
    }
}