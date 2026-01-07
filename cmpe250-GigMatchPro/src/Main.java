import java.io.*;
import java.util.Locale;

public class Main {

    private static GigMatchPro platform = new GigMatchPro();

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        if (args.length != 2) {
            System.err.println("Usage: java Main <input_file> <output_file>");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                processCommand(line, writer);
            }

        } catch (IOException e) {
            System.err.println("Error reading/writing files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processCommand(String command, BufferedWriter writer)
            throws IOException {

        String[] parts = command.split("\\s+");
        String operation = parts[0];
        String result = "";

        try {
            switch (operation) {
                case "register_customer":
                    result = platform.registerCustomer(parts[1]);
                    break;

                case "register_freelancer":
                    int price = Integer.parseInt(parts[3]);
                    int t = Integer.parseInt(parts[4]);
                    int c = Integer.parseInt(parts[5]);
                    int r = Integer.parseInt(parts[6]);
                    int e = Integer.parseInt(parts[7]);
                    int a = Integer.parseInt(parts[8]);
                    result = platform.registerFreelancer(parts[1], parts[2], price, t, c, r, e, a);
                    break;

                case "request_job":
                    int k = Integer.parseInt(parts[3]);
                    result = platform.requestJob(parts[1], parts[2], k);
                    break;

                case "employ_freelancer":
                    result = platform.employFreelancer(parts[1], parts[2]);
                    break;

                case "complete_and_rate":
                    int rating = Integer.parseInt(parts[2]);
                    result = platform.completeAndRate(parts[1], rating);
                    break;

                case "cancel_by_freelancer":
                    result = platform.cancelByFreelancer(parts[1]);
                    break;

                case "cancel_by_customer":
                    result = platform.cancelByCustomer(parts[1], parts[2]);
                    break;

                case "blacklist":
                    result = platform.blacklist(parts[1], parts[2]);
                    break;

                case "unblacklist":
                    result = platform.unblacklist(parts[1], parts[2]);
                    break;

                case "change_service":
                    int newPrice = Integer.parseInt(parts[3]);
                    result = platform.changeService(parts[1], parts[2], newPrice);
                    break;

                case "simulate_month":
                    result = platform.simulateMonth();
                    break;

                case "query_freelancer":
                    result = platform.queryFreelancer(parts[1]);
                    break;

                case "query_customer":
                    result = platform.queryCustomer(parts[1]);
                    break;

                case "update_skill":
                    int t_u = Integer.parseInt(parts[2]);
                    int c_u = Integer.parseInt(parts[3]);
                    int r_u = Integer.parseInt(parts[4]);
                    int e_u = Integer.parseInt(parts[5]);
                    int a_u = Integer.parseInt(parts[6]);
                    result = platform.updateSkill(parts[1], t_u, c_u, r_u, e_u, a_u);
                    break;

                default:
                    result = "Unknown command: " + operation;
            }

        } catch (Exception e) {
            result = "Some error occurred in " + operation + ".";
        }

        writer.write(result);
        writer.newLine();
    }
}