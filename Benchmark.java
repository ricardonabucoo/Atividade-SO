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
        for (int i = 0; i < size; i++) list.remove(list.size() - 1);
        long removeTime = System.nanoTime() - start;

        System.out.printf("ArrayList -> Insercao: %.2f ms | %.0f ops/s | Busca: %.2f ms | %.0f ops/s | Remocao: %.2f ms | %.0f ops/s\n",
                addTime / 1e6, size / (addTime / 1e9),
                getTime / 1e6, size / (getTime / 1e9),
                removeTime / 1e6, size / (removeTime / 1e9));

        // Teste com MyArrayList (thread-safe)
        MyArrayList<Integer> myList = new MyArrayList<>();
        start = System.nanoTime();
        for (int i = 0; i < size; i++) myList.add(i);
        addTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < size; i++) myList.get(rand.nextInt(size));
        getTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = size - 1; i >= 0; i--) myList.remove(i);
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
            for (int i = 0; i < OPERATIONS_PER_THREAD; i++) vector.add(rand.nextInt());
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

        // Teste com MyArrayList (thread-safe com ReentrantLock)
        MyArrayList<Integer> myList = new MyArrayList<>();
        start = System.nanoTime();
        runParallel(executor, () -> {
            Random rand = new Random();
            for (int i = 0; i < OPERATIONS_PER_THREAD; i++) myList.add(rand.nextInt());
            for (int i = 0; i < OPERATIONS_PER_THREAD; i++) myList.get(rand.nextInt(OPERATIONS_PER_THREAD));
            for (int i = 0; i < OPERATIONS_PER_THREAD; i++) myList.remove(rand.nextInt(OPERATIONS_PER_THREAD));
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
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });

        }
        latch.await();
    }
}
