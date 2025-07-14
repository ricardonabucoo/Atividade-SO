/* O seu desafio é implementar um ArrayList que seja thread safe. Lembre-se que as
operações de consulta não causam condição de corrida umas com as outras,
entretanto, as inserções e remoções causam condição de corrida entre elas.
 */


import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyArrayList<T> {
    private final ArrayList<T> arrayList;
    private final ReentrantReadWriteLock rwLock;
    private final Lock readLock;
    private final Lock writeLock;


    public MyArrayList() {
        arrayList = new ArrayList<>();
        rwLock = new ReentrantReadWriteLock(true);
        readLock = rwLock.readLock();
        writeLock = rwLock.writeLock();
    }

    public void add(T element) {
        writeLock.lock();
        try {
            arrayList.add(element);
        } finally {
            writeLock.unlock();
        }
    }

    public void remove(T element) {
        writeLock.lock();
        try {
            arrayList.remove(element);
        } finally {
            writeLock.unlock();
        }
    }

    public T get(int index) {
        readLock.lock();
        try {
            try {
                return arrayList.get(index);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        } finally {
            readLock.unlock();
        }
    }

    public void printIndexAndAll(int index) {
        readLock.lock();
        try {
            System.out.println("Horse " + (index+1) + " finished");
            for (T element : arrayList) 
                System.out.print(element + " ");
            System.out.println();
        } finally {
            readLock.unlock();
        }
    }   

    public void set(int index, T element) {
        writeLock.lock();
        try {
            arrayList.set(index, element);
        } finally {
            writeLock.unlock();
        }
    }

    public int size() {
        readLock.lock();
        try {
            return arrayList.size();
        } finally {
            readLock.unlock();
        }
    }

    public boolean contains(T element) {
        readLock.lock();
        try {
            return arrayList.contains(element);
        } finally {
            readLock.unlock();
        }
    }

    public void clear() {
        writeLock.lock();
        try {
            arrayList.clear();
        } finally {
            writeLock.unlock();
        }
    }

    public boolean isEmpty() {
        readLock.lock();
        try {
            return arrayList.isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    /////adicionei isso para ficar mais justo com a remoção em O(1)
    public T remove(int index) {
        writeLock.lock();
        try {
            try {
                return arrayList.remove(index);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        } finally {
            writeLock.unlock();
        }
    }

}

