


import br.ufpe.cin.five.api.recognizer.Recognizer;
import br.ufpe.cin.five.api.recognizer.RecognizerException;
import java.io.File;

/**
 *
 * @author Alexandre
 */
public class RecognizerTest { 

    public static void main(String[] args) {

        try {
            String enginePath = "/home/vocallab-pablo/vlRecognizer/engines/comandos";
            Recognizer rec = new Recognizer(enginePath);
            
            File audio = new File("/home/vocallab-pablo/vlRecognizer/samples/vlRecognizer_1_14_2798.wav");
            
            //rec.StartRecognition();                        
            System.out.println(rec.recognize(audio).getAnswer());

        } catch (RecognizerException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
