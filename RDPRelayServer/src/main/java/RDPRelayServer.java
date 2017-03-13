import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by myrao_000 on 2017-03-12.
 */
public class RDPRelayServer
{
    private static RDPRelayServer uniqueInstance = null;

    private Socket toSoc = null;
    private Socket fromSoc = null;

    private int fromPort = 3000;
    private int toPort = 3001;

    public static RDPRelayServer getInstance()
    {
        if(uniqueInstance == null)
        {
            try
            {
                uniqueInstance = new RDPRelayServer();
            }
            catch (Exception e)
            {

            }
        }

        return uniqueInstance;
    }

    public void run() throws IOException
    {
        try
        {
            while (true)
            {
                System.out.println("중계 서버 시작");

                toSoc = null;
                fromSoc = null;

                FromService fromRunnable = new FromService();
                ToService toRunnable = new ToService();

                Thread fromThread = new Thread(fromRunnable);
                Thread toThread = new Thread(toRunnable);

                fromThread.start();
                toThread.start();

                fromThread.join();
                toThread.join();

                Thread.sleep(1000);
            }
        }
        catch (Exception e)
        {
            System.exit(1);
        }
    }

    class FromService implements Runnable {
        private ServerSocket serverFromSocket = null;

        private DataInputStream fromDI = null;
        private DataOutputStream toDO = null;

        private byte[] buffer = new byte[512];
        private int bytes_read;

        public FromService() throws IOException
        {
            serverFromSocket = new ServerSocket(fromPort);
        }

        public void run()
        {
            try{
                fromSoc = serverFromSocket.accept();
                fromDI = new DataInputStream(fromSoc.getInputStream());

                System.out.println("From Server");
                System.out.println(fromSoc.getInetAddress() + "로부터 접속입니다.");

                while(true)
                {
                    if(toSoc != null)
                    {
                        toDO = new DataOutputStream(toSoc.getOutputStream());
                        break;
                    }

                    Thread.sleep(100);
                }

                System.out.println("From 에서 To로 전달 시작");

                Thread.sleep(500);

                while(true)
                {
                    bytes_read = fromDI.read(buffer);
                    if(bytes_read == -1)
                        continue;
                    toDO.write(buffer, 0, bytes_read);
                }

            }catch (IOException e){
                System.out.print("FromService IOException");
                System.out.println(e);
            }catch (InterruptedException e){
                System.out.print("FromService InterruptedException");
                System.out.println(e);
            }finally {
                try {
                    serverFromSocket.close();
                    fromDI.close();
                    toDO.close();
                }catch (IOException e){

                }
            }
        }
    }

    class ToService implements Runnable {
        private ServerSocket serverToSocket = null;

        private DataInputStream toDI = null;
        private DataOutputStream fromDO = null;

        private byte[] buffer = new byte[512];
        private int bytes_read;

        public ToService() throws IOException
        {
            serverToSocket = new ServerSocket(toPort);
        }

        public void run()
        {
            try{
                toSoc = serverToSocket.accept();
                toDI = new DataInputStream(toSoc.getInputStream());

                System.out.println("To Server " + toSoc.getInetAddress() + "로부터 접속입니다.");

                while(true)
                {
                    if(fromSoc != null)
                    {
                        DataOutputStream DOS = new DataOutputStream(toSoc.getOutputStream());
                        DOS.writeUTF("Start");
                        DOS.flush();

                        fromDO = new DataOutputStream(fromSoc.getOutputStream());
                        break;
                    }

                    Thread.sleep(100);
                }

                System.out.println("To 에서 From으로 전달 시작");

                Thread.sleep(500);

                while(true)
                {
                    bytes_read = toDI.read(buffer);
                    if(bytes_read == -1)
                        continue;
                    fromDO.write(buffer, 0, bytes_read);
                }
            }catch (IOException e) {
                System.out.print("ToService IOException");
                System.out.println(e);
            }
            catch (InterruptedException e)
            {
                System.out.print("ToService InterruptedException");
                System.out.println(e);
            }finally {
                try {
                    serverToSocket.close();
                    toDI.close();
                    fromDO.close();
                }catch (IOException e){

                }
            }
        }
    }
}