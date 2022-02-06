package org.firstinspires.ftc.teamcode.drive.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@Disabled
@TeleOp
public class HorizontalSlideTesting extends LinearOpMode {

    private DcMotorEx horizontalSlideMotor;
    @Override
    public void runOpMode() throws InterruptedException {
        while(opModeIsActive()) {
            horizontalSlideMotor = hardwareMap.get(DcMotorEx.class, "Horizontal Slide Motor");
            float horzSlideMotorSpeed = -gamepad1.left_stick_y;
            horizontalSlideMotor.setPower(horzSlideMotorSpeed);
        }
    }
}
