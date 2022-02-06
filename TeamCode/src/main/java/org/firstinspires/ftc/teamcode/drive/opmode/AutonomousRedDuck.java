package org.firstinspires.ftc.teamcode.drive.opmode;

import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.DR4B_Cap;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.DR4B_High;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.DR4B_Low;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.DR4B_Mid;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.DR4B_Rest;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.clawClosePos;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.clawOpenPos;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.horizontalSlideL1;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.horizontalSlideL2;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.horizontalSlideL3;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.odometerDownPos;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import java.util.List;

public class AutonomousRedDuck extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "FreightFrenzy_BCDM.tflite";
    private static final String[] LABELS = {
            "Ball",
            "Cube",
            "Duck",
            "Marker"
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

        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        Pose2d startPose = new Pose2d(-36,-64.625,Math.PI/2);

        drive.setPoseEstimate(startPose);

        horizontalSlide = hardwareMap.get(DcMotorEx.class, "Motor Horizontal Slide");
        horizontalSlide.setDirection(DcMotorSimple.Direction.FORWARD);
        horizontalSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        horizontalSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
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

        while (!isStarted() && !isStopRequested()){
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
                            if (updatedRecognitions.get(scanner).getLabel().equals(LABELS[2])) {
                                telemetry.addData("Recognitions", "ducky duck");
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
        if (opModeIsActive()){
            Trajectory traj1 = drive.trajectoryBuilder(startPose)
                    .lineToLinearHeading(
                            new Pose2d(-25, -38, Math.toRadians(-123)),
                            SampleMecanumDrive.getVelocityConstraint(DriveConstants.MAX_VEL/2, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                            SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL)
                            )
                    .addTemporalMarker(0, () -> {
                        if (dropLevel == 1 || dropLevel == 2){
                            setDR4BServo(DR4B_Mid);
                            telemetry.addData("Freight", "Ready to extend");
                            telemetry.update();
                        }
                        else if (dropLevel == 3){
                            setDR4BServo(DR4B_High);
                            telemetry.addData("Freight", "Ready to extend");
                            telemetry.update();
                        }
                    })
                    .addTemporalMarker(0.8, () ->{
                        if (dropLevel == 1){
                            setHorizontalSlide(horizontalSlideL1, 1);
                            telemetry.addData("Slide Status", "Extending");
                            telemetry.update();
                        }
                        else if (dropLevel == 2){
                            setHorizontalSlide(horizontalSlideL2, 1);
                            telemetry.addData("Slide Status", "Extending");
                            telemetry.update();
                        }
                        else if (dropLevel == 3){
                            setHorizontalSlide(horizontalSlideL3, 1);
                            telemetry.addData("Slide Status", "Extending");
                            telemetry.update();
                        }
                    })
                    .addTemporalMarker(1.2, () ->{
                        if (dropLevel == 1){
                            setDR4BServo(DR4B_Low);
                            telemetry.addData("Freight", "Low");
                            telemetry.update();

                        }
                        else if (dropLevel == 2){
                            setDR4BServo(DR4B_Mid);
                            telemetry.addData("Freight", "Mid");
                            telemetry.update();
                        }
                        else if (dropLevel == 3){
                            setDR4BServo(DR4B_High);
                            telemetry.addData("Freight", "High");
                            telemetry.update();
                        }
                    })
                    .build();


            Trajectory traj2 = drive.trajectoryBuilder(traj1.end())
                    .lineToSplineHeading(
                            new Pose2d(-54, -54, Math.toRadians(180))
                    )
                    .addTemporalMarker(0, () ->{
                        setHorizontalSlide(0, 1);
                        telemetry.addData("Slide", "Retracting");
                        telemetry.update();
                    })
                    .addTemporalMarker(0.4, () ->{
                        clawServo.setPosition(clawClosePos);
                        telemetry.addData("Claw", "Closed");
                        telemetry.update();
                    })
                    .build();


            Trajectory traj3 = drive.trajectoryBuilder(traj2.end())
                    .lineToLinearHeading(
                            new Pose2d(-57, -36, Math.toRadians(90))
                    )
                    .build();

            drive.followTrajectory(traj1);
            telemetry.addData("Path Status", "Going to Shipping Hub");
            telemetry.update();

            clawServo.setPosition(clawOpenPos);
            sleep(1500);
            telemetry.addData("Freight", "Dropped 😩");
            telemetry.update();

            drive.followTrajectory(traj2);
            telemetry.addData("Path Status", "Going to Duck Spinner");
            telemetry.update();

            setDR4BServo(DR4B_Low);
            telemetry.addData("Lift", "Rest");
            telemetry.update();

            duckSpinnerLeft.setPower(1);
            telemetry.addData("Duck", "Spinning");
            telemetry.update();
            sleep(4000);
            duckSpinnerLeft.setPower(0);

            drive.followTrajectory(traj3);
            telemetry.addData("Path Status", "Going to Storage Unit");
            telemetry.update();
        }
    }

    private void setHorizontalSlide(int position, double power){
        horizontalSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        horizontalSlide.setTargetPosition(position);
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
