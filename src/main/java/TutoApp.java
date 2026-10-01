public class TutoApp {

    public static void main(String[] args) {
        var members = Member.getAll();
        for (var m : members)
            System.out.println(m);
    }



}
