
import br.ufpe.cin.five.api.synthesizer.Synthesizer;
import br.ufpe.cin.five.api.synthesizer.SynthesizerException;
import java.io.IOException;

public class SynthesisTest {

    public static void main(String[] args) throws IOException, SynthesizerException {


     Synthesizer syn = new Synthesizer("C:\\wamp\\www\\vlSynthesizer");// Pasta de execução
    // syn.synthesize("frase sintetizada", "lis ou cid", "nome do arquivo", "MaryTTSEngine ou HtsEngine");
    syn.synthesize("Todos já chegaram então serviremos o jantar.", "cid", "ola", "HtsEngine"); 
    }
}
