import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class RecoText {
    Tesseract ts;

    public RecoText() {
        ts = new Tesseract();
        ts.setDatapath("");
        ts.setLanguage("eng");
        try {
            String text = ts.doOCR(getImage("img location"));
            System.out.println(text);

            // Call the text-to-speech method to read the extracted text
            convertTextToSpeech(text);
        } catch (TesseractException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage getImage(String imgPath) throws IOException {
        // Read image
        Mat mat = Imgcodecs.imread(imgPath);

        if (mat.empty()) {
            throw new IOException("Image not loaded properly");
        }

        // Convert to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);

        // Resize image
        Mat resized = new Mat();
        Size size = new Size(mat.width() * 1.9, mat.height() * 1.9);
        Imgproc.resize(gray, resized, size);

        // Convert to buffered image
        MatOfByte mof = new MatOfByte();
        Imgcodecs.imencode(".png", resized, mof);
        byte[] imageByte = mof.toArray();

        return ImageIO.read(new ByteArrayInputStream(imageByte));
    }

    private void convertTextToSpeech(String text) {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        Voice voice = VoiceManager.getInstance().getVoice("kevin16");

        if (voice != null) {
            voice.allocate();
            AudioPlayer aplayer = new SingleFileAudioPlayer("D:\\audio\\mywave2", javax.sound.sampled.AudioFileFormat.Type.WAVE);
            voice.setAudioPlayer(aplayer);
            boolean status = voice.speak(text);
            System.out.println("Status: " + status);
            aplayer.close();
            voice.deallocate();
        } else {
            System.out.println("Error in getting Voices");
        }
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new RecoText();
    }
}
