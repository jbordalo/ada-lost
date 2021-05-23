import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public final static String UNREACHABLE = "Unreachable";
    public final static String LOST_IN_TIME = "Lost in Time";

    public static void main(String[] args) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        int testCases = Integer.parseInt(input.readLine());

        for (int i = 0; i < testCases; i++)
            solve(input, i + 1);

        input.close();
    }

    public static void solve(BufferedReader input, int test_case) throws IOException {
        String[] tokens = input.readLine().split(" ");
        int rows = Integer.parseInt(tokens[0]);
        int columns = Integer.parseInt(tokens[1]);
        int magicWheels = Integer.parseInt(tokens[2]);

        Lost lost = new Lost(rows, columns, magicWheels);

        char[][] rowsInput = new char[rows][columns];

        for (int r = 0; r < rows; r++) {
            char[] row = input.readLine().toCharArray();
            rowsInput[r] = row;
        }
        lost.addRows(rowsInput);

        for (int m = 0; m < magicWheels; m++) {
            tokens = input.readLine().split(" ");
            // Magic wheel's destination cell
            int r_i = Integer.parseInt(tokens[0]);
            int c_i = Integer.parseInt(tokens[1]);
            // Magic wheel's time travelled
            int t_i = Integer.parseInt(tokens[2]);

            lost.addMagicWheel(r_i, c_i, t_i, m);
        }

        System.out.printf("Case #%d%n", test_case);

        try {
            tokens = input.readLine().split(" ");
            // John's initial position
            int r_j = Integer.parseInt(tokens[0]);
            int c_j = Integer.parseInt(tokens[1]);
            int john = lost.solveJohn(r_j, c_j);
            printResult("John", john);
        } catch (NegativeWeightCycleException e) {
            System.out.printf("John %s%n", LOST_IN_TIME);
        }
        int r_k = Integer.parseInt(tokens[2]);
        int c_k = Integer.parseInt(tokens[3]);
        int kate = lost.solveKate(r_k, c_k);
        printResult("Kate", kate);
    }

    public static void printResult(String character, int result) {
        if (result < Lost.INF) {
            System.out.printf("%s %d%n", character, result);
        } else {
            System.out.printf("%s %s%n", character, UNREACHABLE);
        }
    }
}
