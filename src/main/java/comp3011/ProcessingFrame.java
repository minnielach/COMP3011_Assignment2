package comp3011;

import org.bytedeco.javacv.*;

// keep the video frame together and the information together
public record ProcessingFrame(Frame frame, InfoFrame info) {


}
