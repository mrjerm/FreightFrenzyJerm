package org.firstinspires.ftc.teamcode.drive.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Disabled
@TeleOp
public class driveThing extends LinearOpMode {

    public DcMotorEx frontLeft;
    public DcMotorEx backLeft;
    public DcMotorEx frontRight;
    public DcMotorEx backRight;

    @Override
    public void runOpMode() throws InterruptedException {
        while(opModeIsActive()) {
            frontLeft = hardwareMap.get(DcMotorEx.class, "Motor FL");
            backLeft = hardwareMap.get(DcMotorEx.class, "Motor BL");
            frontRight = hardwareMap.get(DcMotorEx.class, "Motor FR");
            backRight = hardwareMap.get(DcMotorEx.class, "Motor BR");

            frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
            backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

            float x1 = gamepad1.left_stick_x;
            float y1 = gamepad1.left_stick_y;
            float x2 = gamepad2.left_stick_x;
            double fl = -x1 + y1 + x2;
            double bl = x1 + y1 + x2;
            double fr = x1 + y1 - x2;
            double br = -x1 + y1 - x2;

            frontLeft.setPower(fl);
            backLeft.setPower(bl);
            frontRight.setPower(fr);
            backRight.setPower(br);
        }
    }
}
