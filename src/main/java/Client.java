import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class Client extends Thread {
    private String host = "localhost";

    private Client() {
    } // Клиент явно запускается для заданного хоста


    private static final Map<String, String> clientAnswers = Map.of(
            "Write your name", "John K.",
            "Are you child? (yes/no)", "no",
            "Write your age?", "25",
            "Write your city?", "Moscow");

    private long severQuestionCount = 0;
    private static final long MAX_SERVER_QUESTIONS = 10;

    public Client(String host) {
        if (host != null && !host.isEmpty()) {
            this.host = host;
        }
        start();
    }

    public void run() {
        System.out.println("Клиент запущен!\n");
        try (
                Socket socket = new Socket(host, Server.SERVER_PORT);
                PrintWriter clientWriter = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader clientReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            System.out.printf("Client: выполнено подключение к серверу <%s> на порту %d\n", host, Server.SERVER_PORT);
            while (true) {
                // завершение сеанса по счётчику вопросов
                severQuestionCount++;
                if (severQuestionCount > MAX_SERVER_QUESTIONS) {
                    System.out.println("Client: cеанс завершён по счётчику вопросов!");
                    return;
                }

                // Ответы на вопросы сервера
                String serverQuestion = clientReader.readLine(); //
                System.out.printf("Client: сервер задал вопрос : <%s>\n", serverQuestion);
                if (serverQuestion != null && serverQuestion.equals(Server.BYE_MSG)) {
                    System.out.println("   ------ Сервер закрыл соединение! ------   \n");
                    return;
                } else {
                    if (serverQuestion == null) {
                        System.out.println("Client: сервер прислал пустой вопрос!");
                        serverQuestion = "";
                    }
                    String clientAnswer = clientAnswers.get(serverQuestion);
                    if (clientAnswer == null) {
                        System.out.println("Client: неизвестный вопрос от сервера. Нет ответа на него! ");
                        clientAnswer = "Unknown question";
                    } else {
                        System.out.printf("Client: ответ на вопрос: <%s>\n", clientAnswer);
                    }
                    clientWriter.println(clientAnswer);
                    sleep(1000);
                }

                // Реакция сервера на ответы клиента
                String serverReaction = clientReader.readLine();
                System.out.println("Client: реакция сервера на ответы клиента: " + serverReaction + "\n");
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при запуске клиента: " + e);
            throw new RuntimeException(e);
        }
    }
}
