
import br.ufpe.cin.five.api.synthesizer.Synthesizer;
import br.ufpe.cin.five.api.synthesizer.SynthesizerException;

/**
 *
 * @author Alexandre
 */
public class SynthesisTest {

    public static void main(String[] args) {

        try {
            String enginePath = "C:\\Users\\Alexandre\\Desktop\\vlSynthesizer\\engine";
            
            Synthesizer syn = new Synthesizer(enginePath);
            
            String phrase = "funcionará";
            String speaker = "cid";
            
            syn.synthesize(phrase, speaker, false);                        
                        
        } catch (SynthesizerException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
