/**
 * Created by myrao_000 on 2017-03-14.
 */
public class Main {
    public static void main(String[] args)
    {
        while(true)
        {
            RDPRelayClient rdpRelayClient = new RDPRelayClient();
            rdpRelayClient.run();

            try{
                System.out.println("RDPRelayClient가 5초 뒤 재시작 합니다.");
                Thread.sleep(5000);
            }catch (Exception e)
            {

            }
        }
    }
}
