public class Main {
    public static final String HOST = "netology.homework";

    public static void main(String[] args) throws Exception {
        System.out.println("------ Client-server demo application! ------");
        Server server = new Server();
        Client client = new Client(HOST);
    }
}
