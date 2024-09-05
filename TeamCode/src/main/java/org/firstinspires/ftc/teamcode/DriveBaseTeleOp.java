package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="DriveBaseTeleOp", group="Linear OpMode")
public class DriveBaseTeleOp extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFrontDrive;
    private DcMotor rightFrontDrive;
    private DcMotor rightBackDrive;
    private DcMotor leftBackDrive;

    private Servo armServo;

    private RevBlinkinLedDriver blinkin;

    private boolean isSlow = false;
    private boolean isArmSlow = false;

    @Override
    public void runOpMode() {
        // Get motors
        leftFrontDrive = hardwareMap.get(DcMotor.class, "lfd");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "rfd");
        rightBackDrive = hardwareMap.get(DcMotor.class, "rbd");
        leftBackDrive = hardwareMap.get(DcMotor.class, "lbd");

        armServo = hardwareMap.get(Servo.class, "armServo");

        blinkin = hardwareMap.get(RevBlinkinLedDriver.class, "blinkin");

        // Set motor directions
        leftFrontDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        armServo.setDirection(Servo.Direction.FORWARD);

        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.COLOR_WAVES_PARTY_PALETTE);

        // Display telemetry
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            runBot();
        }
    }

    private void runBot() {
        double max;

        // Get gamepad input
        double axial = -gamepad1.left_stick_y;
        double lateral = gamepad1.left_stick_x;
        double yaw = gamepad1.right_stick_x;

        double leftTrigger = gamepad1.left_trigger;
        double rightTrigger = gamepad1.right_trigger;

        boolean rightBumper = gamepad1.right_bumper;
        boolean leftBumper = gamepad1.left_bumper;
        boolean bButton = gamepad1.b;

        // Calculate motor powers
        double leftFrontPower = axial + lateral + yaw;
        double rightFrontPower = axial - lateral - yaw;
        double leftBackPower = axial - lateral + yaw;
        double rightBackPower = axial + lateral - yaw;

        // Make sure motors don't go above max power
        max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        if (leftTrigger >= 0.5) {
            isSlow = true;
            blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.YELLOW);
        }

        if (rightTrigger >= 0.5) {
            isSlow = false;
            blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.COLOR_WAVES_PARTY_PALETTE);
        }

        if (rightBumper) {
            armServo.setPosition(armServo.getPosition() + 0.01);
        } else if (leftBumper) {
            armServo.setPosition(armServo.getPosition() - 0.01);
        }



        if (isSlow) {
            leftFrontPower /= 2;
            rightFrontPower /= 2;
            leftBackPower /= 2;
            rightBackPower /= 2;
        }

        if (bButton) {
            if (isArmSlow) {
                isArmSlow = false;
            } else {
                isArmSlow = true;
            }
        }




        // Apply power
        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);

        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
        telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
        telemetry.update();
    }
}

