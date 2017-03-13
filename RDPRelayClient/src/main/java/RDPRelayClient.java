import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by myrao_000 on 2017-03-13.
 */
public class RDPRelayClient {
    private DataInputStream toDI = null;
    private DataOutputStream toDO = null;

    private DataInputStream rdpDI = null;
    private DataOutputStream rdpDO = null;

    private Socket rdpClientSoc = null;
    private Socket toSoc = null;

    private RDPRunnable rdpRunnable = new RDPRunnable();
    private ToRunnable toRunnable = new ToRunnable();

    public void run()
    {
        try
        {
            System.out.println("to 시작합니다.");

            toSoc = new Socket(InetAddress.getByName("192.168.21.3"), 3001);
            DataInputStream DIS = new DataInputStream(toSoc.getInputStream());

            while (true)
            {
                String command = DIS.readUTF();

                if(command.equals("Start"))
                {
                    System.out.println("Received :" + command);
                    rdpClientSoc = new Socket(InetAddress.getByName(null), 3389);
                    break;
                }

                Thread.sleep(100);
            }

            Thread th1 = new Thread(rdpRunnable);
            Thread th2 = new Thread(toRunnable);

            th1.start();
            th2.start();

            try{
                th1.join();
                th2.join();
            }catch (InterruptedException e)
            {
                System.out.println(e);
            }

        } catch (UnknownHostException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } catch (InterruptedException e) {
            System.out.println(e);
        } finally {
            try {
                toDI.close();
                toDO.close();
                rdpDI.close();
                rdpDI.close();
                rdpClientSoc.close();
                toSoc.close();
            } catch (Exception e){
                System.out.println(e);
            }
        }
    }

    class RDPRunnable implements Runnable{
        public void run()
        {
            try{

                //toSoc로 부터 받은 것을 rdp로 보내주는 역할
                rdpDO = new DataOutputStream(rdpClientSoc.getOutputStream());
                toDI = new DataInputStream(toSoc.getInputStream());

                System.out.println("RDPRunnable is Started");

                byte[] buffer = new byte[512];
                int bytes_read;

                while(true)
                {
                    bytes_read = toDI.read(buffer);
                    if(bytes_read == -1)
                        continue;
                    rdpDO.write(buffer, 0, bytes_read);
                }
            }catch (IOException e)
            {
                System.out.println(e);
            }

        }
    }

    class ToRunnable implements Runnable{
        public void run()
        {
            try{
                //rdp 부터 받은 것을 toSoc로 보내주는 역할
                toDO = new DataOutputStream(toSoc.getOutputStream());
                rdpDI = new DataInputStream(rdpClientSoc.getInputStream());

                System.out.println("ToRunnable is Started");

                byte[] buffer = new byte[512];
                int bytes_read;

                while(true)
                {
                    bytes_read = rdpDI.read(buffer);
                    if(bytes_read == -1)
                        continue;
                    toDO.write(buffer, 0, bytes_read);
                }
            }catch (IOException e)
            {
                System.out.println(e);
            }

        }
    }
}
