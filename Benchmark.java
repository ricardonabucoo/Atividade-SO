//Exercício 3
//
//▸Utilizando como base sua implementação thread safe do ArrayList, compare o
//desempenho com a versão original que não é thread safe utilizando apenas 1 thread
//OK ▸Faça a comparação para os métodos de inserção, busca e remoção, variando o
//tamanho da lista e mostrando o tempo necessário para a realizar a operação com os
//tamanhos variados da lista. Adicionalmente, informe quantas operações (inserção,
//busca e remoção separadamente) podem ser realizadas por segundo em ambas as
//listas
// OK ▸Repita os testes mas agora utilizando 16 threads para comparar sua implementação
//thread safe com a classe Vector
//OK (10000) ▸Cada thread realiza uma quantidade predefinida de operações de inserção, busca e
//remoção com valores aleatórios
//OK ▸Informe os valores obtidos nos testes realizados




import java.util.*;
import java.util.concurrent.*;

public class Benchmark {

    static final int[] SIZES = {10_000, 100_000, 500_000};
    static final int THREADS = 16;
    static final int OPERATIONS_PER_THREAD = 10_000;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==== TESTES COM 1 THREAD ====");
        for (int size : SIZES) {
            System.out.println("\nTamanho da lista: " + size);
            singleThreadTest(size);
        }

        System.out.println("\n==== TESTES COM 16 THREADS ====");
        multiThreadTest();
    }

    static void singleThreadTest(int size) {
        Random rand = new Random();

        // Teste com ArrayList padrão (não thread-safe)
        List<Integer> list = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < size; i++) list.add(i);
        long addTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < size; i++) list.get(rand.nextInt(list.size()));
        long getTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < size; i++) list.remove(list.size() - 1); // Correção: remove último elemento
        long removeTime = System.nanoTime() - start;

        System.out.printf("ArrayList -> Insercao: %.2f ms | %.0f ops/s | Busca: %.2f ms | %.0f ops/s | Remocao: %.2f ms | %.0f ops/s\n",
                addTime / 1e6, size / (addTime / 1e9),
                getTime / 1e6, size / (getTime / 1e9),
                removeTime / 1e6, size / (removeTime / 1e9));

        // Teste com MyArrayList
        MyArrayList<Integer> myList = new MyArrayList<>();
        start = System.nanoTime();
        for (int i = 0; i < size; i++) myList.add(i);
        addTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < size; i++) myList.get(rand.nextInt(myList.size())); // Correção: usa size atual
        getTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < size; i++) myList.remove(myList.size() - 1); // Correção: remove por índice
        removeTime = System.nanoTime() - start;

        System.out.printf("MyArrayList -> Insercao: %.2f ms | %.0f ops/s | Busca: %.2f ms | %.0f ops/s | Remocao: %.2f ms | %.0f ops/s\n",
                addTime / 1e6, size / (addTime / 1e9),
                getTime / 1e6, size / (getTime / 1e9),
                removeTime / 1e6, size / (removeTime / 1e9));
    }

    static void multiThreadTest() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        // Teste com Vector
        Vector<Integer> vector = new Vector<>();
        long start = System.nanoTime();
        runParallel(executor, () -> {
            Random rand = new Random();
            for (int i = 0; i < OPERATIONS_PER_THREAD; i++) {
                vector.add(rand.nextInt());
            }
            for (int i = 0; i < OPERATIONS_PER_THREAD; i++) {
                synchronized (vector) {
                    if (!vector.isEmpty()) {
                        int idx = rand.nextInt(vector.size());
                        vector.get(idx);
                    }
                }
            }
            for (int i = 0; i < OPERATIONS_PER_THREAD; i++) {
                synchronized (vector) {
                    if (!vector.isEmpty()) {
                        int idx = rand.nextInt(vector.size());
                        vector.remove(idx);
                    }
                }
            }
        });
        long vectorTime = System.nanoTime() - start;
        long totalOps = THREADS * OPERATIONS_PER_THREAD * 3L;
        System.out.printf("Vector -> Total: %.2f s | %.0f ops/s\n",
                vectorTime / 1e9, totalOps / (vectorTime / 1e9));

        // Teste com MyArrayList
        MyArrayList<Integer> myList = new MyArrayList<>();
        start = System.nanoTime();
        runParallel(executor, () -> {
            Random rand = new Random();
            for (int i = 0; i < OPERATIONS_PER_THREAD; i++) {
                myList.add(rand.nextInt());
            }
            for (int i = 0; i < OPERATIONS_PER_THREAD; i++) {
                synchronized (myList) { // Atomicidade para operações compostas
                    if (!myList.isEmpty()) {
                        int idx = rand.nextInt(myList.size());
                        myList.get(idx);
                    }
                }
            }
            for (int i = 0; i < OPERATIONS_PER_THREAD; i++) {
                synchronized (myList) { // Atomicidade para operações compostas
                    if (!myList.isEmpty()) {
                        int idx = rand.nextInt(myList.size());
                        myList.remove(idx); // Usa o novo método remove por índice
                    }
                }
            }
        });
        long myListTime = System.nanoTime() - start;
        System.out.printf("MyArrayList -> Total: %.2f s | %.0f ops/s\n",
                myListTime / 1e9, totalOps / (myListTime / 1e9));

        executor.shutdown();
    }

    static void runParallel(ExecutorService executor, Runnable task) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(THREADS);
        for (int i = 0; i < THREADS; i++) {
            executor.submit(() -> {
                try {
                    task.run();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
    }
}