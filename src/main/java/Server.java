import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class Server extends Thread {
    public static final Integer SERVER_PORT = 8080;
    public static final String BYE_MSG = "bye";

    private static final List<String> serverQuestion = List.of(
            "Write your name",
            "Are you child? (yes/no)",
            "Write your age?",
            "Write your city?",
            "Write your sex? (male/female)",
            BYE_MSG
    );

    private String clientName;
    private boolean isChild;
    private Integer clientAge;
    private String clientCity;
    private String clientSex;


    public Server() {
        start();
    }

    ;

    // запуск сервера
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Cервер запущен!\n");
            while (true) {
                Socket clientSoket = serverSocket.accept();
                try (PrintWriter clientWriter = new PrintWriter(clientSoket.getOutputStream(), true);
                     BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSoket.getInputStream()))
                ) {
                    System.out.println("Server: клиент подключился к серверу!");

                    for (int i = 0; i < serverQuestion.size(); i++) {
                        // отправка вопросов клиенту
                        String question = serverQuestion.get(i);
                        System.out.println("Sever: клиету отправлен вопрос: " + question);
                        clientWriter.println(question);
                        sleep(1000);

                        // получение ответа от клиента
                        String clientAnswer = clientReader.readLine();

                        // реакция на ответ клиента
                        String serverReactionOnAnswer = "";
                        switch (i) {
                            case 0:
                                clientName = clientAnswer;
                                serverReactionOnAnswer = "Welcome to the server, " + clientName + "!";
                                break;
                            case 1:
                                if (clientAnswer.equals("yes")) {
                                    isChild = true;
                                    serverReactionOnAnswer = String.format("Welcome to the kids area, %s! Let's play!", clientName);
                                } else {
                                    if (clientAnswer.equals("no")) {
                                        isChild = false;
                                        serverReactionOnAnswer = String.format("Welcome to the adult zone, %s! Have a good rest, or a good working day!", clientName);
                                    } else {
                                        isChild = true;
                                        serverReactionOnAnswer = String.format("Your answer is incorrect!");
                                    }
                                }
                                break;
                            case 2:
                                clientAge = Integer.parseInt(clientAnswer);
                                serverReactionOnAnswer = String.format("Your age is %d", clientAge);
                                break;
                            case 3:
                                clientCity = clientAnswer;
                                serverReactionOnAnswer = String.format("Your city is %s", clientCity);
                                break;
                            case 4:
                                if (clientAnswer.equals("male")) {
                                    clientSex = "male";
                                } else {
                                    if (clientAnswer.equals("female")) {
                                        clientSex = "female";
                                    } else {
                                        clientSex = "unknown";
                                    }
                                }
                                serverReactionOnAnswer = String.format("Your sex is %s", clientSex);
                                break;
                            default:
                                // TODO
                        }
                        clientWriter.println(serverReactionOnAnswer);
                        sleep(1000);
                    }
                } catch (IOException | InterruptedException e) {
                    System.out.println("Ошибка при получении потоков ввода/вывода клиента:" + e);
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при запуске сервера: " + e);
            throw new RuntimeException(e);
        }
    }
}
