
public class Main {
    
    public static void main(String[] args) {

        MyArrayList<Integer> myList = new MyArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            myList.add(0); 
        }
        
        Thread[] horses = new Thread[10];

        for (int i = 0; i < 10; i++) {
            final int index = i;
            horses[i] = new Thread(() -> { 
                for(int j = 0; j < 10000; j++) {
                    int elem = myList.get(index); 
                    myList.set(index, elem+1); 
                }
                myList.printIndexAndAll(index);
            });
        }

        for (var h : horses)
            h.start();
        
        for (var h : horses) {
            try {
                h.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
