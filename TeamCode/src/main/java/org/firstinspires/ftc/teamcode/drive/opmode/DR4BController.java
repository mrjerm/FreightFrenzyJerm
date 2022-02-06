package org.firstinspires.ftc.teamcode.drive.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import static org.firstinspires.ftc.teamcode.drive.opmode.Constants.*;

@Disabled
@TeleOp
public class DR4BController extends OpMode {

    public Servo DR4BServo;

    @Override
    public void init() {
        DR4BServo = hardwareMap.get(Servo.class, "Servo DR4B");
        DR4BServo.setDirection(Servo.Direction.REVERSE);

    }

    int DR4BLevel = 0;
    double timeFrame = 0;

    @Override
    public void loop() {
        timeFrame = getRuntime();
        if (getRuntime() - timeFrame > buttonBufferTime && gamepad1.dpad_up && DR4BLevel <= 4){
            DR4BLevel ++;
        }
        else if (getRuntime() - timeFrame > buttonBufferTime && gamepad1.dpad_down && DR4BLevel >= 0) {
            DR4BLevel--;
        }


        setDR4BServo(DR4BLevel == 0 ? DR4B_Rest : DR4BLevel == 1 ? DR4B_Low : DR4BLevel == 2 ? DR4B_Mid : DR4BLevel == 3 ? DR4B_High : DR4BLevel == 4 ? DR4B_Cap : DR4BServo.getPosition());
    }

    public void setDR4BServo(double height){
        DR4BServo.setPosition(height);
    }
}