
package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import java.util.logging.Logger;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "AutoBase", group = "plms")
@Disabled
public class AutoBase extends LinearOpMode
{
    RevRobotHardware robot = new RevRobotHardware(); 

    VuforiaLocalizer vuforia;
    VuforiaTrackable relicTemplate;
    RelicRecoveryVuMark vuMark;

    Detected_Color jewelColor = Detected_Color.Unknown;

    public AutoConfig autoConfig = null;

    static final double  EncoderDrive_Timeout_Second = 1;
    static final double  DRIVE_SPEED = .6;

    static final double     COUNTS_PER_MOTOR_REV    = 2240 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 3.5 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Init Start");
        telemetry.update();
        idle();

        robot.init(hardwareMap, telemetry);
        robot.autoInitMotor();

        int cameraMonitorViewId = 
            hardwareMap.appContext.getResources().getIdentifier(
                "cameraMonitorViewId", 
                "id", 
                hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "AfCVU/r/////AAAAGcI6mTR0nkXtrD/6Bc0gSR05evjxINGiz25TuUlBwgn2iYbfq9do4lhyKu3Z3zcgv5GAUZhb7cLJNp6ggMMb4grJsXiOswLFa/lQgJ0SGt4coKcZTE2CmvYAwAG7xdP2unWVmdXcW+x1LoBniJxScVRWf9l3lRIixAhgztnvRLjtnhfWiMzhWT3dnkR+vl/64kxzwlqlx2IOOWuMKx/ciOaf+d6cjwzEIKjVbIwHkI9eyzjhDVAhREu/rsJJjQKS0BMmB5n8iK/MPO3FqNGXFS4b+gyN36HwHaXHIm7lHT5Gy7mSy2YvmgIBpHKK4X95KgRUL7oBZW1fIdPfPmZL9dgSeYyvAIH0Bcf8h5eVzOjs ";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);
        
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        relicTemplate = relicTrackables.get(0);
        relicTrackables.activate();
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary
        telemetry.addData("Status", "Initialized");
        
        sleep(400);
        jewelColor = robot.GetColor();
        vuMark = RelicRecoveryVuMark.from(relicTemplate);
        telemetry.addData("Vision", "%s | %s", jewelColor, vuMark);                          
        telemetry.update();
        idle();

        // wait for the start button to be pressed.
        waitForStart();

        vuMark = RelicRecoveryVuMark.UNKNOWN;
        int vuMarkLoop = 0;
        for (vuMarkLoop = 0; vuMarkLoop<20; vuMarkLoop++)
        {
            vuMark = RelicRecoveryVuMark.from(relicTemplate);
            if (vuMark != RelicRecoveryVuMark.UNKNOWN) {
                break;
            }
            sleep(50);
        }

        telemetry.addData("Vision", "%s @%d", vuMark, vuMarkLoop);      
        telemetry.update();
        idle();

        Auto_Step step = Auto_Step.MoveArmDown;

        while (opModeIsActive()) {
            if (Auto_Step.MoveArmDown == step) {
                robot.JewelDown();
                telemetry.addData("jewel", "down");
                telemetry.update();
                sleep(500);
                telemetry.addData("jewel", "down finished");
                telemetry.update();
                
                step = Auto_Step.SenseAndTurnToHitJewel;
            }

            if (Auto_Step.SenseAndTurnToHitJewel == step){
                jewelColor = Detected_Color.Unknown;
                int colorLoop = 0;
                for (colorLoop =0; colorLoop<10; colorLoop++)
                {
                    jewelColor = robot.GetColor();
                    if (jewelColor != Detected_Color.Unknown) {
                        break;
                    }
                    sleep(50);
                }

                telemetry.addData("Color", "%s @%d", jewelColor, colorLoop);  
                telemetry.update();
                idle();

                if (Detected_Color.Unknown == jewelColor)
                {
                    // do not move if could not identify color
                } else if (jewelColor == autoConfig.sideColor){
                    SwingBackword();
                } else {
                    SwingForward();
                }
                
                step = Auto_Step.MoveArmUp;
            }
            
            if (Auto_Step.MoveArmUp == step) {
                robot.JewelUp();
                telemetry.addData("jewel", "up");
                telemetry.update();
                sleep(500);
                telemetry.addData("jewel", "up done");
                telemetry.update();
                
                step = Auto_Step.TurnBack;
                break;
            }
            
            if (Auto_Step.TurnBack == step){
                if (Detected_Color.Unknown == jewelColor)
                {
                    // do not move if could not identify color
                } else if (jewelColor == autoConfig.sideColor){
                    SwingForward();
                } else {
                    SwingBackword();
                }
                
                encoderDrive(DRIVE_SPEED, -6.5 , 6.5, "Turn");
                
                step = Auto_Step.MoveToPosition;
            }
            
            if (Auto_Step.MoveToPosition == step){
                if (autoConfig.positionMiddle) {
                    // forward
                    if (vuMark == RelicRecoveryVuMark.LEFT){
                        encoderDrive(DRIVE_SPEED, autoConfig.middleLeftDistance, autoConfig.middleLeftDistance, "MoveToPosition");
                    }
                    else if (vuMark == RelicRecoveryVuMark.RIGHT){
                        encoderDrive(DRIVE_SPEED, autoConfig.middleRightDistance, autoConfig.middleRightDistance, "MoveToPosition");
                    }
                    else{
                        encoderDrive(DRIVE_SPEED, autoConfig.middleCenterDistance, autoConfig.middleCenterDistance, "MoveToPosition");
                    }
                }
                else 
                {   // Corner
                    // first move
                    encoderDrive(DRIVE_SPEED, autoConfig.cornerFirstMoveDistance, autoConfig.cornerFirstMoveDistance, "CornerFirstMove");

                    // turn left
                    encoderDrive(0.3, -autoConfig.cornerLeftTurnDistance, autoConfig.cornerLeftTurnDistance, "MoveToPosition turn left");
                    
                    // forward
                    if (vuMark == RelicRecoveryVuMark.LEFT){
                        encoderDrive(DRIVE_SPEED, autoConfig.cornerLeftDistance, autoConfig.cornerLeftDistance, "CornerMoveToPosition");
                    }
                    else if (vuMark == RelicRecoveryVuMark.RIGHT){
                        encoderDrive(DRIVE_SPEED, autoConfig.cornerRightDistance, autoConfig.cornerRightDistance, "CornerMoveToPosition");
                    }
                    else{
                        encoderDrive(DRIVE_SPEED, autoConfig.cornerCenterDistance, autoConfig.cornerCenterDistance, "CornerMoveToPosition");
                    }
                }

                step = Auto_Step.TurnToCrypoBox;
            }
            
            if (Auto_Step.TurnToCrypoBox == step){
                encoderDrive(0.3, autoConfig.faceCrypoBoxRightTurnDistance, -autoConfig.faceCrypoBoxRightTurnDistance, "TurnToCrypoBox");
 
                step = Auto_Step.MoveForwardRelease;
            }
            
            if (Auto_Step.MoveForwardRelease == step){
                encoderDrive(DRIVE_SPEED, 10 , 10, "MoveForwardRelease");
                
                robot.ClampOpen();

                
                step = Auto_Step.PushIn;
            }
            
            if (Auto_Step.PushIn == step){
                encoderDrive(.3, -2, -2, "PushIn 1 back");
                encoderDrive(.3,  2,  -2, "PushIn 1 turn right");
                encoderDrive(.2, 4, 4, "PushIn 1 forward");

                encoderDrive(.3, -2, -2, "PushIn 2 back");
                encoderDrive(.3,  -3,  3, "PushIn 2 turn left");
                encoderDrive(.2, 4, 4, "PushIn 2 forward");
                
                step = Auto_Step.Backoff;
            }
                       
            if (Auto_Step.Backoff == step){
                encoderDrive(0.3,  -2 , -2, "Backoff");
            
                step = Auto_Step.EndStop;
            }
            telemetry.addData("step", "%s", step);
        }
    }

    void SwingForward()
    {
        encoderDrive(0.3, .5, -.5, "SwingForward");
    }
    
    void SwingBackword()
    {
        encoderDrive(0.3, -.5, .5, "SwingBackword");
    }


    public void encoderDrive(double speed,
                             double leftInches, 
                             double rightInches,
                             String name) 
    {
        // Determine new target position, and pass to motor controller
        int newLeftTarget = robot.leftDrive.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
        int newRightTarget = robot.rightDrive.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
        robot.leftDrive.setTargetPosition(newLeftTarget);
        robot.rightDrive.setTargetPosition(newRightTarget);

        // Turn On RUN_TO_POSITION
        robot.leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // reset the timeout time and start motion.
        ElapsedTime     localTimer = new ElapsedTime();
        localTimer.reset();
        robot.leftDrive.setPower(Math.abs(speed));
        robot.rightDrive.setPower(Math.abs(speed));

        // keep looping while we are still active, and there is time left, and both motors are running.
        // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
        // its target position, the motion will stop.  This is "safer" in the event that the robot will
        // always end the motion as soon as possible.
        // However, if you require that BOTH motors have finished their moves before the robot continues
        // onto the next step, use (isBusy() || isBusy()) in the loop test.
        while (localTimer.seconds() < EncoderDrive_Timeout_Second 
        && (robot.leftDrive.isBusy() || robot.rightDrive.isBusy())) {

            // Display it for the driver.
            telemetry.addData("", "%s @ %s", name,  localTimer.toString());
            telemetry.addData("To",  "%7d :%7d", newLeftTarget,  newRightTarget);
            telemetry.addData("At",  "%7d :%7d",
                                        robot.leftDrive.getCurrentPosition(),
                                        robot.rightDrive.getCurrentPosition());
            telemetry.update();
            idle();
        }

        // Stop all motion;
        robot.leftDrive.setPower(0);
        robot.rightDrive.setPower(0);

        // Turn off RUN_TO_POSITION
        robot.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

}
