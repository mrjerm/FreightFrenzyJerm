package org.firstinspires.ftc.teamcode.drive.opmode;

import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.DR4B_Cap;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.DR4B_Clear;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.DR4B_High;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.DR4B_Low;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.DR4B_Mid;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.DR4B_Rest;
import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.buttonBufferTime;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@Disabled
@TeleOp
public class DR4BLogicTesting extends LinearOpMode {
    private Servo DR4BServo;
    private int DR4BLevel = 0;
    private double timeStamp = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        DR4BServo = hardwareMap.get(Servo.class, "Servo DR4B");
        DR4BServo.setDirection(Servo.Direction.REVERSE);
        telemetry.addData("Status", "Ready");
        telemetry.update();
        waitForStart();
        if (opModeIsActive()) {
            telemetry.clearAll();
            while (opModeIsActive()) {
                timeStamp = getRuntime();

                if (gamepad2.dpad_up){
                    if (getRuntime() - timeStamp >= buttonBufferTime){
                        DR4BLevel += 1;
                    }
                }
                if (gamepad2.dpad_down){
                    if (getRuntime() - timeStamp >= buttonBufferTime){
                        DR4BLevel -= 1;
                    }
                }

                if (DR4BLevel == 0){
                    DR4BServo.setPosition(DR4B_Rest);
                }
                if (DR4BLevel == 1){
                    DR4BServo.setPosition(DR4B_Low);
                }
                if (DR4BLevel == 2){
                    DR4BServo.setPosition(DR4B_Mid);
                }
                if (DR4BLevel == 3){
                    DR4BServo.setPosition(DR4B_High);
                }
                if (DR4BLevel == 4){
                    DR4BServo.setPosition(DR4B_Cap);
                }
                if (DR4BLevel == 5){
                    DR4BServo.setPosition(DR4B_Clear);
                }

                telemetry.addData("Runtime", getRuntime());
                telemetry.addData("Timestamp", getRuntime());
                telemetry.addData("DR4B Level", DR4BLevel);
                telemetry.addData("DR4B Position", DR4BServo.getPosition());
                telemetry.addData("DR4B Servo Info", DR4BServo);
                telemetry.update();
            }
        }
    }
}
