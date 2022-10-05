package dao;

public class UserObj {
    private String id;
    private String name;
    private String ts;

    public UserObj(String id, String name, String ts) {
        this.name = name;
        this.id = id;
        this.ts = ts;
    }

    public String getId() { return this.id; }
    public String getName() { return this.name; }
    public String getTs() { return this.ts; }
}
