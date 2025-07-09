
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class MyArrayList<T> {
    private final ArrayList<T> arrayList;
    private final ReentrantLock lock;


    public MyArrayList() {
        this.arrayList = new ArrayList<>();
        this.lock = new ReentrantLock(true);
    }

    public void add(T element) {
        lock.lock();
        try {
            arrayList.add(element);
        } finally {
            lock.unlock();
        }
    }

    public void remove(T element) {
        lock.lock();
        try {
            arrayList.remove(element);
        } finally {
            lock.unlock();
        }
    }

    public T get(int index) {
        lock.lock();
        try {
            return arrayList.get(index);
        } finally {
            lock.unlock();
        }
    }

    public void printIndexAndAll(int index) {
        lock.lock();
        try {
            System.out.println("Horse " + (index+1) + " finished");
            for (T element : arrayList) 
                System.out.print(element + " ");
            System.out.println();
        } finally {
            lock.unlock();
        }
    }   

    public void set(int index, T element) {
        lock.lock();
        try {
            arrayList.set(index, element);
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return arrayList.size();
        } finally {
            lock.unlock();
        }
    }

    public boolean contains(T element) {
        lock.lock();
        try {
            return arrayList.contains(element);
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        lock.lock();
        try {
            arrayList.clear();
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return arrayList.isEmpty();
        } finally {
            lock.unlock();
        }
    }

}

/* O seu desafio é implementar um ArrayList que seja thread safe. Lembre-se que as
operações de consulta não causam condição de corrida umas com as outras,
entretanto, as inserções e remoções causam condição de corrida entre elas.
 */