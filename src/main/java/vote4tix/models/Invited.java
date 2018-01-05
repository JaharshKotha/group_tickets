package vote4tix.models;


public class Invited {
    public String email;
    public String mobile;
    public String message;

    public String toString() {
        return email + " " + mobile;
    }
}