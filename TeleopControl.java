package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@TeleOp(name="TeleOp", group="plms")
public class TeleopControl extends LinearOpMode {

    /* Declare OpMode members. */
    RevRobotHardware   robot           = new RevRobotHardware();             
                
    @Override
    public void runOpMode() {
        robot.init(hardwareMap, telemetry);
        robot.teleopInitMotor();
        idle();
        
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        
        while (opModeIsActive()) {
            
            robot.TankDrive(0.5*-gamepad1.left_stick_y, 0.5*-gamepad1.right_stick_y);
            
            if (gamepad2.a){
                robot.JewelDown();
            }
            else if (gamepad2.y){
                robot.JewelUp();
        
            }
            
            if(gamepad1.x){
                robot.ClampClose();
            }
            else if(gamepad1.b){
                robot.ClampOpen();
            }
            if (gamepad1.y){
                robot.LiftUp();
            }
            else if (gamepad1.a){
                robot.LiftDown();
            }   
            else{
              robot.LiftStop();  
            }


            if(gamepad1.dpad_down){
                robot.BothArmMoveBack();
            }
            
            if(gamepad1.dpad_right){
                robot.IntakeSwitchOnOff();
                while (gamepad1.dpad_right)
                {
                    idle();
                }
            }
            
            if(gamepad1.left_trigger > .2){
                robot.BothArmFlat();
            }
            
            if(gamepad1.right_trigger > .2){
                robot.ArmOpenClose();
                while (gamepad1.right_trigger > .2)
                {
                    idle();
                }
            }
            
            robot.GetColor();
            robot.UpdateTelemetry();
            telemetry.update();
            idle();
        }
    }
}
