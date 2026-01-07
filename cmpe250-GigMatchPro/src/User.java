public abstract class User {
    public final String id;

    public User(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}