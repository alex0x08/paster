package uber.paste.skypebot;

import com.skype.Application;
import com.skype.ApplicationAdapter;
import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.Stream;
import com.skype.StreamAdapter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import uber.paste.ws.generated.PasterWS;

/**
 *
 * @author achernyshev
 */
public class SkypeBot {

    private PasterWS ws;
    private Application app;
    private XMPPConnection connection;

    private void init() throws SkypeException, XMPPException {

        
         // connect to server
        connection = new XMPPConnection("jabber.org");
        connection.connect();
        connection.login("botbo678", "1q2w3e4r"); // TODO: change user and pass

        System.out.println("_login");
        
       
        
       
        
        ws = new PasterWS();

           System.out.println("_ws");
      
        
        //  Skype.setDebug(true);
        Skype.setDaemon(false);
        app = Skype.addApplication(this.getClass().getName());


        Skype.addChatMessageListener(new ChatMessageAdapter() {
            @Override
            public void chatMessageReceived(ChatMessage received) throws SkypeException {
                if (received.getType().equals(ChatMessage.Type.SAID)) {
                    System.out.println("received:" + received.getContent());
                    // received.getSender().send("I'm working. Please, wait a moment.");



                 //   ws.getPasteWebserviceImplPort().addPaste("XXXXXXXXXXXXXXXX" + received.getId(), "XXXXXXXXXXXXXXXX" + received.getContent(), "SKYPE", "plain");

                }
            }
        });

        app.addApplicationListener(new ApplicationAdapter() {
            @Override
            public void connected(Stream stream) throws SkypeException {
                System.out.println("connected:" + stream.getId());
                stream.addStreamListener(new StreamAdapter() {
                    @Override
                    public void textReceived(String receivedText) throws SkypeException {
                        System.out.println("received:" + receivedText);
                    }
                });
            }
        });


           System.out.println("_skype");
      
           
            // register listeners
        ChatManager chatmanager = connection.getChatManager();
        connection.getChatManager().addChatListener(new ChatManagerListener() {
            public void chatCreated(final Chat chat, final boolean createdLocally) {
                chat.addMessageListener(new MessageListener() {
                    public void processMessage(Chat chat, Message message) {
                        try {
                            System.out.println("Received message: "
                                    + (message != null ? message.getBody() : "NULL"));
                            
                            
                            Skype.chat("ntairov").send((message != null ? message.getBody() : "NULL"));
                             
                        } catch (SkypeException ex) {
                           ex.printStackTrace();
                        }
                    }
                });
            }
        });

       
      /*  connection.disconnect();
    */

    }

    public static void main(String[] args) throws Exception {

        // PasterWS ws = new PasterWS();

        new SkypeBot().init();


    }
}
