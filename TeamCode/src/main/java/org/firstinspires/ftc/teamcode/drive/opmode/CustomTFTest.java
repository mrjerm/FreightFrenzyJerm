package org.firstinspires.ftc.teamcode.drive.opmode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

/**
 * This 2020-2021 OpMode illustrates the basics of using the TensorFlow Object Detection API to
 * determine the position of the Freight Frenzy game elements.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 *
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 */
@TeleOp(name = "Concept: TensorFlow Object Detection Webcam", group = "Concept")
public class CustomTFTest extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "E:\\FTC\\Programming\\roadrunnerfreightfrenzy\\ FtcRobotController\\src\\main\\assets\\TSE.tflite";
    private static final String[] LABELS = {
            "TSE"
    };

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY =
            "AaUAmHT/////AAABmQL+ceRLoET2hku+clLNd6BvmbZSle08MZLdzABXX4GKMlcRRBQrwbDvB3q+r5GR3htBm+3qzUNxoEaXXIf9OsDFNSbI8LvcFqQBjImS5C0lrylUrPWb/XxIhtUvxDgC9cpgrXPcdxJmlVJ3IIjVZ9vGODHbC8IekNOlNcF9Wpnnbv0YcJBqUOzdhA5YlY3Q5cE59qt5e1CZOnGoWeFo60S1L2zxtlRGgP4eTwD3pMSl9vZVQrq5WHvUuJTaTnJPqbkRWtdvmgm9b80hPeeY72DUJtDMqGSB5yrXAMhBLk6CuTxkgLQJ9YulzV+0DiaSx2RNK7NXQncFKcWfvPcmazGY/GVixkERJV8ONjqOfQq6";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    private int dropLevel = 0;

    @Override
    public void runOpMode() {
        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();
        initTfod();

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(2.5, 16.0 / 9.0);
        }

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            while (opModeIsActive()) {
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        if (updatedRecognitions.isEmpty()) {
                            telemetry.addData("Recognitions", "none");
                            telemetry.update();
                        } else {
                            for (int scanner = 0; scanner < updatedRecognitions.size(); scanner++) {
                                if (updatedRecognitions.get(scanner).getLabel().equals(LABELS[0])) {
                                    telemetry.addData("Recognitions", "TSE");
                                    telemetry.addData("Left", updatedRecognitions.get(scanner).getLeft());
                                    telemetry.addData("Right", updatedRecognitions.get(scanner).getRight());
                                    telemetry.update();
                                    if (updatedRecognitions.get(scanner).getLeft() <= 292.75) {
                                        dropLevel = 1;
                                    } else if (updatedRecognitions.get(scanner).getLeft() >= 292.75 && updatedRecognitions.get(0).getLeft() <= 815.125) {
                                        dropLevel = 2;
                                    } else if (updatedRecognitions.get(scanner).getLeft() >= 815.125) {
                                        dropLevel = 3;
                                    }
                                    telemetry.addData("Drop Level", dropLevel);
                                    telemetry.update();
                                }
                                telemetry.addData("Recognitions", updatedRecognitions);
                                telemetry.update();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Initialize the Vuforia localization engine.
     */

    private void initVuforia () {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.8f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 320;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
    }
}

