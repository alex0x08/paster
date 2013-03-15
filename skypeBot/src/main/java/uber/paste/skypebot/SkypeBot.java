package uber.paste.skypebot;

import com.skype.Application;
import com.skype.ApplicationAdapter;
import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.Stream;
import com.skype.StreamAdapter;
import uber.paste.ws.generated.PasterWS;

/**
 *
 * @author achernyshev
 */
public class SkypeBot {

        private PasterWS ws;
        
        private Application app;
        
    
  
    
    private void init() throws SkypeException {
    
        ws = new PasterWS();
        
       //  Skype.setDebug(true);
        Skype.setDaemon(false);
        app = Skype.addApplication(this.getClass().getName());
        
      
         Skype.addChatMessageListener(new ChatMessageAdapter() {
            @Override
            public void chatMessageReceived(ChatMessage received) throws SkypeException {
                if (received.getType().equals(ChatMessage.Type.SAID)) {
                     System.out.println("received:" + received.getContent());
                   // received.getSender().send("I'm working. Please, wait a moment.");
                     
                     
                     
                      ws.getPasteWebserviceImplPort().addPaste("XXXXXXXXXXXXXXXX"+received.getId(),"XXXXXXXXXXXXXXXX"+received.getContent(),"SKYPE","plain");
                     
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
        
        
    }
    
    
    public static void main(String[] args) throws Exception {
    
       // PasterWS ws = new PasterWS();
        
        new SkypeBot().init();
       
        
    }
    
}
