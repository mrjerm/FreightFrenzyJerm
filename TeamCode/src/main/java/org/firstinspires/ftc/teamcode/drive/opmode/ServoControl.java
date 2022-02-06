package org.firstinspires.ftc.teamcode.drive.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@Disabled
@TeleOp
public class ServoControl extends OpMode {

    public Servo clawServo;

    @Override
    public void init() {
        clawServo = hardwareMap.get(Servo.class, "Servo Claw");
        clawServo.setDirection(Servo.Direction.FORWARD);
    }



    @Override
    public void loop() {
        clawServo.setPosition(-gamepad1.left_stick_y);
    }
}
