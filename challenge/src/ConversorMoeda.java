import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ConversorMoeda {

    private static final String API_KEY = "0502887dd825077d46fe8351";
    private static final List<String> MOEDAS_SUPORTADAS = List.of("ARS", "BOB", "BRL", "CLP", "COP", "USD");
    private static final Map<String, Double> taxas = new HashMap<>();

    public static void main(String[] args) {
        carregarTaxas();

        if (taxas.isEmpty()) {
            System.out.println("Não foi possível carregar as taxas de câmbio. Encerrando programa.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n====== CONVERSOR DE MOEDAS ====== \n");
            System.out.println("Moedas disponíveis: \n" +
                    "\nPeso argentino (ARS)" +
                    "\nBoliviano boliviano (BOB)" +
                    "\nReal brasileiro (BRL)" +
                    "\nPeso chileno (CLP)" +
                    "\nPeso colombiano (COP)" +
                    "\nDólar americano (USD)\n");

            System.out.println("1 - Converter moeda");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1 -> {
                    realizarConversao(scanner);
                    System.out.print("\nDeseja realizar outra conversão? (s/n): ");
                    String resposta = scanner.nextLine().trim().toLowerCase();
                    if (!resposta.equals("s")) {
                        continuar = false;
                        System.out.println("Encerrando...");
                    }
                }
                case 0 -> {
                    System.out.println("Encerrando...");
                    continuar = false;
                }
                default -> System.out.println("Opção inválida. Tente novamente.");
            }
        }

        scanner.close();
    }

    private static void carregarTaxas() {
        String url = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

            if (json.has("conversion_rates")) {
                JsonObject rates = json.getAsJsonObject("conversion_rates");

                for (String moeda : MOEDAS_SUPORTADAS) {
                    if (rates.has(moeda)) {
                        taxas.put(moeda, rates.get(moeda).getAsDouble());
                    }
                }
            } else {
                System.out.println("A resposta da API não contém as taxas.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao acessar a API: " + e.getMessage());
        }
    }

    private static void realizarConversao(Scanner scanner) {
        double valor;

        while (true) {
            try {
                System.out.print("Digite o valor a ser convertido: ");
                valor = scanner.nextDouble();
                scanner.nextLine();
                if (valor < 0) {
                    System.out.println("Por favor, insira um valor positivo.");
                } else {
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Valor inválido. Por favor, insira um número.");
                scanner.nextLine();
            }
        }

        System.out.println("Selecione a moeda de origem:");
        String origem = escolherMoeda(scanner);

        System.out.println("Selecione a moeda de destino:");
        String destino = escolherMoeda(scanner);

        double taxaOrigem = taxas.get(origem);
        double taxaDestino = taxas.get(destino);
        double resultado = valor * (taxaDestino / taxaOrigem);

        System.out.printf("%.2f %s equivalem a %.2f %s\n", valor, origem, resultado, destino);
    }

    private static String escolherMoeda(Scanner scanner) {
        while (true) {
            for (int i = 0; i < MOEDAS_SUPORTADAS.size(); i++) {
                System.out.printf("%d: %s\n", i + 1, MOEDAS_SUPORTADAS.get(i));
            }

            try {
                int escolha = scanner.nextInt();
                scanner.nextLine();
                if (escolha >= 1 && escolha <= MOEDAS_SUPORTADAS.size()) {
                    return MOEDAS_SUPORTADAS.get(escolha - 1);
                } else {
                    System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Digite um número.");
                scanner.nextLine();
            }
        }
    }
}






