import java.util.concurrent.*;
import java.util.Random;

public class SleepingBarber extends Thread
{
    Random rand = new Random();
    public static Semaphore customers = new Semaphore(0);
    public static Semaphore barbers = new Semaphore(0);
    public static Semaphore accessSeat = new Semaphore(1);

    /*  the number of chairs in this barbershop is 5. */
    public static final int NUMBER_OF_SEATS = 10;
    public static int availableSeats = NUMBER_OF_SEATS;


    class Customer extends Thread
    {
        int id;
        boolean notCut = true;

        public Customer(int i)
        {
            id = i;
        }

        public void run()
        {
            while (notCut)
            {
                try
                {
                    accessSeat.acquire();
                    if (availableSeats > 0)
                    {
                        System.out.println("Customer "+this.id +" has arrived.");
                        availableSeats--;  //Ocupa a cadeira
                        customers.release();  //Notifica o barbeiro
                        accessSeat.release();  // Libera as cadeiras

                        try
                        {
                            barbers.acquire();
                            notCut = false;
                            this.getHaircut();
                        }
                        catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else
                    {
                        System.out.println("There are no available chairs. Customer " + this.id + " left the barbershop.");
                        accessSeat.release();
                        notCut=false;
                    }
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public void getHaircut()
        {
            System.out.println("Customer " + this.id + " is cutting his hair");
            try
            {
                sleep(rand.nextInt(5, 11) * 1000);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class Barber extends Thread
    {
        private final int id;
        public Barber(int id) {
            this.id = id;
        }

        public void run()
        {
            while(true)
            {
                try
                {
                    customers.acquire();
                    accessSeat.release();
                    availableSeats++;
                    barbers.release();
                    accessSeat.release();
                    this.cutHair();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        public void cutHair()
        {
            System.out.println("The barber " + id + " is cutting hair");
            try
            {
                sleep(rand.nextInt(5, 11) * 1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


    public static void main(String[] args)
    {
        SleepingBarber barberShop = new SleepingBarber();
        barberShop.start();
    }

    public void run()
    {
        Barber b1 = new Barber(1);
        Barber b2 = new Barber(2);
        b1.start();
        b2.start();


        int i = 0;
        while (true)
        {
            i++;
            Customer aCustomer = new Customer(i);
            aCustomer.start();
            try
            {
                sleep(rand.nextInt(4, 7) * 1000);
            }
            catch(InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
