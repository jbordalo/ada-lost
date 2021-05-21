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
            solve(input, i+1);

        input.close();
    }

    public static void solve(BufferedReader input, int test_case) throws IOException {
        String[] tokens = input.readLine().split(" ");
        int rows = Integer.parseInt(tokens[0]);
        int columns = Integer.parseInt(tokens[1]);
        int magicWheels = Integer.parseInt(tokens[2]);

        Lost lost = new Lost(rows, columns, magicWheels);

        for (int r = 0; r < rows; r++) {
            String c = input.readLine();
        }

        for (int m = 0; m < magicWheels; m++) {
            tokens = input.readLine().split(" ");
            // Magic wheel's destination cell
            int r_i = Integer.parseInt(tokens[0]);
            int c_i = Integer.parseInt(tokens[1]);
            // Magic wheel's time travelled
            int t_i = Integer.parseInt(tokens[2]);
        }

        tokens = input.readLine().split(" ");
        // John's position
        int r_j = Integer.parseInt(tokens[0]);
        int c_j = Integer.parseInt(tokens[1]);
        lost.setJohn(r_j, c_j);
        // Kate's position
        int r_k = Integer.parseInt(tokens[2]);
        int c_k = Integer.parseInt(tokens[3]);
        lost.setKate(r_k, c_k);

        int[] solution = lost.solve();

        System.out.printf("Case #%d%n", test_case);

        // PRINT SOLUTION, MAYBE THROW EXCEPTIONS
    }
}
