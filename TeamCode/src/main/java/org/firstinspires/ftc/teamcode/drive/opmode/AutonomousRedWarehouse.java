package org.firstinspires.ftc.teamcode.drive.opmode;

import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.DR4B_High;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.DR4B_Low;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.DR4B_Mid;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.DR4B_Rest;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.clawClosePos;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.clawOpenPos;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.clawRestPos;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.horizontalSlideClear;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.horizontalSlideL1;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.horizontalSlideL2;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.horizontalSlideL3;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.odometerDownPos;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import java.util.List;

@TeleOp
public class AutonomousRedWarehouse extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "//sdcard//FIRST//tflitemodels//TSE.tflite";
    private static final String[] LABELS = {
            "TSE"
    };

    private static final String VUFORIA_KEY =
            "AaUAmHT/////AAABmQL+ceRLoET2hku+clLNd6BvmbZSle08MZLdzABXX4GKMlcRRBQrwbDvB3q+r5GR3htBm+3qzUNxoEaXXIf9OsDFNSbI8LvcFqQBjImS5C0lrylUrPWb/XxIhtUvxDgC9cpgrXPcdxJmlVJ3IIjVZ9vGODHbC8IekNOlNcF9Wpnnbv0YcJBqUOzdhA5YlY3Q5cE59qt5e1CZOnGoWeFo60S1L2zxtlRGgP4eTwD3pMSl9vZVQrq5WHvUuJTaTnJPqbkRWtdvmgm9b80hPeeY72DUJtDMqGSB5yrXAMhBLk6CuTxkgLQJ9YulzV+0DiaSx2RNK7NXQncFKcWfvPcmazGY/GVixkERJV8ONjqOfQq6";

    private VuforiaLocalizer vuforia;

    private TFObjectDetector tfod;

    private int dropLevel = 0;

    public DcMotorEx horizontalSlide;
    public DcMotorEx intake;

    public Servo DR4BServo;

    public CRServo duckSpinnerLeft;
    public CRServo duckSpinnerRight;

    public Servo clawServo;
    public Servo odometerYL;
    public Servo odometerYR;
    public Servo odometerX;


    @Override
    public void runOpMode() throws InterruptedException {

        initVuforia();
        initTfod();

        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(1, 16.0 / 9.0);
        }

        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        Pose2d startPose = new Pose2d(7.8,-61.625,Math.toRadians(90));

        drive.setPoseEstimate(startPose);

        horizontalSlide = hardwareMap.get(DcMotorEx.class, "Motor Horizontal Slide");
        horizontalSlide.setDirection(DcMotorSimple.Direction.FORWARD);
        horizontalSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        horizontalSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intake = hardwareMap.get(DcMotorEx.class, "Motor Intake");
        intake.setDirection(DcMotorSimple.Direction.FORWARD);

        DR4BServo = hardwareMap.get(Servo.class, "Servo DR4B");
        DR4BServo.setDirection(Servo.Direction.FORWARD);

        duckSpinnerLeft = hardwareMap.get(CRServo.class, "Servo Duck Spinner Left");
        duckSpinnerRight = hardwareMap.get(CRServo.class, "Servo Duck Spinner Right");
        duckSpinnerLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        duckSpinnerRight.setDirection(DcMotorSimple.Direction.REVERSE);

        clawServo = hardwareMap.get(Servo.class, "Servo Claw");
        clawServo.setDirection(Servo.Direction.FORWARD);
        clawServo.setPosition(clawClosePos);

        odometerYL = hardwareMap.get(Servo.class, "Servo Odometer YL");
        odometerYL.setDirection(Servo.Direction.REVERSE);

        odometerYR = hardwareMap.get(Servo.class, "Servo Odometer YR");
        odometerYR.setDirection(Servo.Direction.FORWARD);

        odometerX = hardwareMap.get(Servo.class, "Servo Odometer X");
        odometerX.setDirection(Servo.Direction.REVERSE);

        odometerYL.setPosition(odometerDownPos);
        odometerYR.setPosition(odometerDownPos);
        odometerX.setPosition(odometerDownPos);

        telemetry.addData("Status", "Ready!");
        telemetry.update();

        ElapsedTime elapsedTime = new ElapsedTime(ElapsedTime.Resolution.SECONDS);
        double timeStamp = elapsedTime.time();

        while (!isStarted() && !isStopRequested() && (elapsedTime.time() - timeStamp < 4)){
            if (tfod != null) {
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    if (updatedRecognitions.isEmpty()) {
                        dropLevel = 3;
                        telemetry.addData("Drop Level", dropLevel);
                        telemetry.update();
                    } else {
                        for (int scanner = 0; scanner < updatedRecognitions.size(); scanner++) {
                            if (updatedRecognitions.get(scanner).getLabel() == LABELS[0]) {
                                if (updatedRecognitions.get(scanner).getLeft() <= 320) {
                                    dropLevel = 1;
                                }
                                else if (updatedRecognitions.get(scanner).getLeft() >= 320) {
                                    dropLevel = 2;
                                }
                                telemetry.addData("Left", updatedRecognitions.get(scanner).getLeft());
                                telemetry.addData("Drop Level", dropLevel);
                                telemetry.update();
                            }
                        }
                    }
                }
            }
        }
        timeStamp = elapsedTime.time();
        TrajectorySequence driveTraj = drive.trajectorySequenceBuilder(startPose)
                .addDisplacementMarker(() -> {
                    if (dropLevel == 1 || dropLevel == 2){
                        setDR4BServo(DR4B_Mid);
                    }
                    else if (dropLevel == 3){
                        setDR4BServo(DR4B_High);
                    }
                })
                .UNSTABLE_addTemporalMarkerOffset(0.4, () -> {
                    setHorizontalSlide(horizontalSlideClear, 1);
                })
                .UNSTABLE_addTemporalMarkerOffset(0.7, () -> {
                    if (dropLevel == 1){
                        setDR4BServo(DR4B_Low);
                    }
                })
                .addTemporalMarker(pathTime -> pathTime * 0.8, () -> {
                    if (dropLevel == 1){
                        setHorizontalSlide(horizontalSlideL1, 1);
                    }
                    else if (dropLevel == 2){
                        setHorizontalSlide(horizontalSlideL2, 1);
                    }
                    else if (dropLevel == 3){
                        setHorizontalSlide(horizontalSlideL3, 1);
                    }
                })

                //go to shipping hub
                .lineToLinearHeading(new Pose2d(2, -34, Math.toRadians(-30)))
                .addDisplacementMarker(() -> {
                    clawServo.setPosition(clawOpenPos);
                })
                .waitSeconds(0.5)

                .addDisplacementMarker(() -> {
                    if (dropLevel == 1){
                        setDR4BServo(DR4B_Mid);
                    }
                    if (dropLevel == 2 || dropLevel == 3) {
                        setHorizontalSlide(0, 1);
                    }
                })
                .UNSTABLE_addTemporalMarkerOffset(0.3, () -> {
                    clawServo.setPosition(clawRestPos);
                    if (dropLevel == 1){
                        setHorizontalSlide(0, 1);
                    }
                })
                .UNSTABLE_addTemporalMarkerOffset(1.2, () -> {
                    if (dropLevel == 2 || dropLevel == 3){
                        setDR4BServo(DR4B_Rest);
                    }
                })
                .UNSTABLE_addTemporalMarkerOffset(1.5, () -> {
                    if (dropLevel == 1){
                        setDR4BServo(DR4B_Rest);
                    }
                })

                //go to warehouse
                .lineToLinearHeading(new Pose2d(8, -64, Math.toRadians(0)))
                .addDisplacementMarker(() -> {
                    intake.setPower(0.8);
                })
                .lineToLinearHeading(new Pose2d(50, -64, Math.toRadians(0)))
                .addDisplacementMarker(() -> {
                    intake.setPower(0);
                    clawServo.setPosition(clawClosePos);
                })
                .UNSTABLE_addTemporalMarkerOffset(0.2, () -> {
                    intake.setPower(-0.6);
                    setDR4BServo(DR4B_High);
                })
                .UNSTABLE_addTemporalMarkerOffset(0.8, () -> {
                    intake.setPower(0);
                })
                .UNSTABLE_addTemporalMarkerOffset(1.2, () -> {
                    setHorizontalSlide(horizontalSlideL3, 1);
                })

                //go to shipping hub
                .lineToLinearHeading(new Pose2d(8, -64, Math.toRadians(0)))
                .lineToLinearHeading(new Pose2d(2, -34, Math.toRadians(-30)))
                .addDisplacementMarker(() -> {
                    clawServo.setPosition(clawOpenPos);
                })
                .waitSeconds(0.5)

                .addDisplacementMarker(() -> {
                    setHorizontalSlide(0, 1);
                })
                .UNSTABLE_addTemporalMarkerOffset(0.3, () -> {
                    clawServo.setPosition(clawRestPos);
                })
                .UNSTABLE_addTemporalMarkerOffset(1.2, () -> {
                    setDR4BServo(DR4B_Rest);
                })

                //go to warehouse
                .lineToLinearHeading(new Pose2d(8, -64, Math.toRadians(0)))
                .lineToLinearHeading(new Pose2d(40, -64, Math.toRadians(0)))
                .build();
        telemetry.addData("calc time", elapsedTime.time() - timeStamp);
        telemetry.addData("big pog", "😩😩😩😩😩😩😩😩");
        telemetry.update();

        if (opModeIsActive()){

            drive.followTrajectorySequence(driveTraj);
        }
    }

    private void setHorizontalSlide(int position, double power){
        horizontalSlide.setTargetPosition(position);
        horizontalSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        horizontalSlide.setPower(power);
    }

    private void setDR4BServo(double position){
        DR4BServo.setPosition(position);
    }

    /**
     * Initialize the Vuforia localization engine.
     */

    private void initVuforia () {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia eng1ine.
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
        tfod.loadModelFromFile(TFOD_MODEL_ASSET, LABELS);
    }
}
