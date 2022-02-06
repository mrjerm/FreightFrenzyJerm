package org.firstinspires.ftc.teamcode.drive.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@Disabled
@TeleOp
public class ZeroServo extends OpMode {

    public Servo DR4BServo;

    @Override
    public void init() {
        DR4BServo = hardwareMap.get(Servo.class, "Servo DR4B");

        DR4BServo.setDirection(Servo.Direction.REVERSE);
    }

    @Override
    public void loop() {
        DR4BServo.setPosition(0);
    }
}
