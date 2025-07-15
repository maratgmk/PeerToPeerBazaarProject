public class WolframRule {
    public static void main(String[] args) {
        int n = 300;
        int[][] M = new int[n][n];

        // Устанавливаем значение 1 в центре первой строки
        M[0][n / 2] = 1;

        // Заполняем матрицу по заданной логике
        for (int i = 1; i < n; i++) {
            for (int j = 1; j < n - 1; j++) {
                M[i][j] = M[i - 1][j];

                // Проверяем условия и изменяем значение
                if (M[i - 1][j] == 0 && M[i - 1][j + 1] == 1) {
                    M[i][j] = 1;
                }
                if (M[i - 1][j] == 1 && M[i - 1][j + 1] == 1 && M[i - 1][j - 1] == 1) {
                    M[i][j] = 0;
                }
            }
        }

        // Выводим матрицу для проверки (опционально)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(M[i][j] + " ");
            }
            System.out.println();
        }

//        private static int applyRule ( int left, int center, int right){
//            // Применяем правило 110
//            if (left == 1 && center == 1 && right == 1) return 0;
//            if (left == 1 && center == 1 && right == 0) return 1;
//            if (left == 1 && center == 0 && right == 1) return 1;
//            if (left == 1 && center == 0 && right == 0) return 0;
//            if (left == 0 && center == 1 && right == 1) return 1;
//            if (left == 0 && center == 1 && right == 0) return 1;
//            if (left == 0 && center == 0 && right == 1) return 1;
//            if (left == 0 && center == 0 && right == 0) return 0;
//
//            // Если ни одно из условий не выполнено, это означает, что входные данные некорректны
//            throw new IllegalArgumentException("Invalid input states: " + left + ", " + center + ", " + right);
//
//        }
    }
}
