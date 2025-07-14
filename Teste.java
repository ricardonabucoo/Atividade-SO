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

public class Teste {

    private static final int[] TAMANHOS = {10_000, 100_000, 500_000, 1_000_000};
    private static final int THREADS = 16;
    private static final int OPS_POR_THREAD = 10_000;

    public static void main(String[] args) throws InterruptedException {


        // Testes com 1 thread
        System.out.println("TESTES SEQUENCIAIS (1 thread):");
        for (int tamanho : TAMANHOS) {
            System.out.printf("\nTamanho: %,d elementos\n", tamanho);
            monoThread(tamanho);
        }

        // Testes com 16 threads
        System.out.println("\n\nTESTES PARALELOS (16 threads):");
        multThread();
    }

    private static void monoThread(int tamanho) {
        Random rand = new Random(1);

        // Teste ArrayList
        ArrayList<Integer> arrayList = new ArrayList<>();
        long tempo = medirTempo(() -> {
            for (int i = 0; i < tamanho; i++) arrayList.add(i);
        });
        long tempoGet = medirTempo(() -> {
            for (int i = 0; i < tamanho; i++) arrayList.get(rand.nextInt(arrayList.size()));
        });
        long tempoRemove = medirTempo(() -> {
            while (!arrayList.isEmpty()) arrayList.removeLast();
        });

        System.out.printf("ArrayList\n    -> \n Add: %4.1f ms (%7.0f ops/s) |\n Get: %4.1f ms (%7.0f ops/s) |\n Remove: %4.1f ms (%7.0f ops/s)\n\n",
                tempo/1e6, tamanho/(tempo/1e9), tempoGet/1e6, tamanho/(tempoGet/1e9), tempoRemove/1e6, tamanho/(tempoRemove/1e9));

        // Teste MyArrayList
        MyArrayList<Integer> myList = new MyArrayList<>();
        rand.setSeed(1);
        tempo = medirTempo(() -> {
            for (int i = 0; i < tamanho; i++) myList.add(i);
        });
        tempoGet = medirTempo(() -> {
            for (int i = 0; i < tamanho; i++) myList.get(rand.nextInt(myList.size()));
        });
        tempoRemove = medirTempo(() -> {
            while (!myList.isEmpty()) myList.remove(myList.size() - 1);
        });

        System.out.printf("MyArrayList\n  -> \n Add: %4.1f ms (%7.0f ops/s) |\n Get: %4.1f ms (%7.0f ops/s) |\n Remove: %4.1f ms (%7.0f ops/s)\n\n",
                tempo/1e6, tamanho/(tempo/1e9), tempoGet/1e6, tamanho/(tempoGet/1e9), tempoRemove/1e6, tamanho/(tempoRemove/1e9));
    }

    private static void multThread() throws InterruptedException {
        // Teste Vector
        Vector<Integer> vector = new Vector<>();
        long tempoVector = medirParalelo(() -> executarOperacoesVector(vector));

        // Teste MyArrayList
        MyArrayList<Integer> myList = new MyArrayList<>();
        long tempoMyList = medirParalelo(() -> executarOperacoesMyList(myList));

        long totalOps = (long) THREADS * OPS_POR_THREAD * 3;
        System.out.printf("Vector      -> %5.2f segundos | %7.0f ops/s\n",
                tempoVector/1e9, totalOps/(tempoVector/1e9));
        System.out.printf("MyArrayList -> %5.2f segundos | %7.0f ops/s\n",
                tempoMyList/1e9, totalOps/(tempoMyList/1e9));

        double diferenca = Math.abs(tempoVector - tempoMyList) / (double) Math.min(tempoVector, tempoMyList);
        String maisRapido = tempoVector < tempoMyList ? "Vector" : "MyArrayList";
        System.out.printf("\n%s foi %.1fx mais rápido\n", maisRapido, diferenca + 1);
    }

    // Criação de threads modelo rafael
    private static long medirParalelo(Runnable tarefa) throws InterruptedException {
        Thread[] threads = new Thread[THREADS];
        long inicio = System.nanoTime();

        // Cria e inicia cada thread individualmente
        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(tarefa); // Cria uma nova thread
            threads[i].start(); // Inicia a thread
        }

        // Aguarda todas as threads terminarem
        for (int i = 0; i < THREADS; i++) {
            threads[i].join(); // Bloqueia até a thread terminar
        }

        return System.nanoTime() - inicio;
    }

    private static void executarOperacoesVector(Vector<Integer> vector) {
        Random rand = new Random();
        Integer TAMANHO = 10000;
        // Inserções
        for (int i = 0; i < OPS_POR_THREAD; i++) {
            vector.add(rand.nextInt(1000));
        }

        // Buscas
        for (int i = 0; i < OPS_POR_THREAD; i++) {
            vector.get(rand.nextInt(TAMANHO));
        }

        // Remoções

        for (int i = 0; i < OPS_POR_THREAD; i++) {
            try {
                vector.remove(rand.nextInt(TAMANHO));
            } catch(Exception e) {
                continue;
            }

        }
    }

    private static void executarOperacoesMyList(MyArrayList<Integer> myList) {
        Random rand = new Random();
        Integer TAMANHO = 10000;
        // Inserções
        for (int i = 0; i < OPS_POR_THREAD; i++) {
            myList.add(rand.nextInt(1));
        }

        // Buscas
        for (int i = 0; i < OPS_POR_THREAD; i++) {
            myList.get(rand.nextInt(TAMANHO));
        }

        // Remoções
        for (int i = 0; i < OPS_POR_THREAD; i++) {
            myList.remove(rand.nextInt(TAMANHO));

        }
    }

    private static long medirTempo(Runnable operacao) {
        long inicio = System.nanoTime();
        operacao.run();
        return System.nanoTime() - inicio;
    }
}