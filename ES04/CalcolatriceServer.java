
import java.io.*;
import java.net.*;

public class CalcolatriceServer {
    private static final int PORTA = 8844;

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {

            System.out.println("Server avviato sulla porta " + PORTA);

            while (true) {

                Socket client = serverSocket.accept();

                System.out.println("Client connesso!");

                try (
                    BufferedReader in = new BufferedReader(
                        new InputStreamReader(client.getInputStream(), "UTF-8")
                    );
                    PrintWriter out = new PrintWriter(
                        client.getOutputStream(), true
                    )
                ) {

                    String richiesta;

                    while ((richiesta = in.readLine()) != null) {

                        if (richiesta.equalsIgnoreCase("QUIT")) {
                            out.println("Connessione chiusa");
                            break;
                        }

                        String risultato = calcolaRichiesta(richiesta);

                        out.println(risultato);
                    }

                } catch (IOException e) {
                    System.out.println("Errore client: " + e.getMessage());
                }

                client.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String calcolaRichiesta(String richiesta) {

        String[] parti = richiesta.trim().split("\\s+");

        if (parti.length != 3) {
            return "ERRORE: Formato non valido";
        }

        try {
            double num1 = Double.parseDouble(parti[0]);
            String operazione = parti[1];
            double num2 = Double.parseDouble(parti[2]);

            if (!Double.isFinite(num1) || !Double.isFinite(num2)) {
                return "ERRORE: Sono ammessi soltanto numeri finiti";
            }

            return String.valueOf(calcola(num1, operazione, num2));

        } catch (NumberFormatException e) {
            return "ERRORE: Numero non valido";
        } catch (ArithmeticException | IllegalArgumentException e) {
            return "ERRORE: " + e.getMessage();
        }
    }

    private static double calcola(double num1, String operazione, double num2) {

        switch (operazione) {
            case "+":
                return num1 + num2;
            case "-":
                return num1 - num2;
            case "*":
                return num1 * num2;
            case "/":
                if (num2 == 0)
                    throw new ArithmeticException("Divisione per zero");
                return num1 / num2;
            default:
                throw new IllegalArgumentException("Operazione non supportata");
        }
    }
}

