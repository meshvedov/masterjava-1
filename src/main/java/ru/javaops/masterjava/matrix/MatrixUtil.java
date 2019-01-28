package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        int cores = Runtime.getRuntime().availableProcessors();
        int range = matrixSize / cores;
        final CompletionService<Integer> service = new ExecutorCompletionService<>(executor);

        ArrayList<Future<Integer>> futures = new ArrayList<>();
        int thatColumn[] = new int[matrixSize];
        for (int j = 0; j < matrixSize; j++) {
            for (int k = 0; k < matrixSize; k++) {
                thatColumn[k] = matrixB[k][j];
            }
            for (int i = 0; i < matrixSize; i++) {
                int thisRow[] = matrixA[i];
                int summand = 0;
                Future<Integer> future = service.submit(() -> mult(thisRow, thatColumn));
                futures.add(future);
                while (!futures.isEmpty()){
                    Future<Integer> future1 = service.poll(1, TimeUnit.SECONDS );
                    futures.remove(future1);
                    summand += future1.get();
                }
                matrixC[i][j] = summand;
            }
        }
        return matrixC;
    }

    private static int mult(int[] arrA, int[] arrB){
        int sum = 0;
        int arrSize = arrA.length;
        for (int k = 0; k < arrSize; k++) {
            sum += arrA[k] * arrB[k];
        }
        return sum;
    }
    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

//        for (int i = 0; i < matrixSize; i++) {
//            for (int j = 0; j < matrixSize; j++) {
//                int sum = 0;
//                for (int k = 0; k < matrixSize; k++) {
//                    sum += matrixA[i][k] * matrixB[k][j];
//                }
//                matrixC[i][j] = sum;
//            }
//        }
//        return matrixC;
        int thatColumn[] = new int[matrixSize];
        for (int j = 0; j < matrixSize; j++) {
            for (int k = 0; k < matrixSize; k++) {
                thatColumn[k] = matrixB[k][j];
            }
            for (int i = 0; i < matrixSize; i++) {
                int thisRow[] = matrixA[i];
                int summand = 0;
                for (int k = 0; k < matrixSize; k++) {
                    summand += thisRow[k] * thatColumn[k];
                }
                matrixC[i][j] = summand;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
