package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import java.util.Set;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import com.qualcomm.robotcore.util.ElapsedTime;
import android.graphics.Color;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

enum Detected_Color
{
    Blue,
    Red,
    Unknown
}

enum JewelArmPosition
{
    Up,
    Down,
}

enum ClampPosition
{
    Close,
    Open,
}

enum ArmPosition
{
    Close,
    Open,
    Flat,
    Back,
}

enum IntakeStatus
{
    Off,
    On,
}

public class RevRobotHardware
{
    HardwareMap hardwareMap  = null;
    Telemetry   telemetry = null;
    
    DcMotor  leftDrive   = null;
    DcMotor  rightDrive  = null;
    double leftDrivePower = 0;
    double rightDrivePower = 0;
    
    DcMotor  lift = null;
    double liftPower = 0;
    
    Servo    clampRight        = null;
    Servo    clampLeft        = null;
    ClampPosition clampPosition = ClampPosition.Close;
    
    
    Servo    jewelArm = null;
    JewelArmPosition jewelArmPosition = JewelArmPosition.Up;
    
    Servo    armLeft= null;
    ArmPosition armLeftPosition = ArmPosition.Back;
    Servo    armRight = null;
    ArmPosition armRightPosition = ArmPosition.Back;

    DcMotor  intakeLeft   = null;
    DcMotor  intakeRight  = null;
    IntakeStatus intakeStatus = IntakeStatus.Off;

    ColorSensor sensorColor = null;
    double colorRed = 0;
    double colorGreen = 0;
    double colorBlue = 0;
    double colorHue = 0;
    Detected_Color resultColor = Detected_Color.Unknown;
    
    DistanceSensor sensorDistance = null;


    public RevRobotHardware() {
    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap pHardwareMap, Telemetry pTelemetry ) {
        hardwareMap = pHardwareMap;
        telemetry = pTelemetry;

        // Motors
        leftDrive  = hardwareMap.get(DcMotor.class, "left_drive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        leftDrivePower = 0;
        leftDrive.setPower(leftDrivePower);
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        rightDrivePower = 0;
        rightDrive.setPower(rightDrivePower);        
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        lift = hardwareMap.get(DcMotor.class, "lift");
        lift.setDirection(DcMotor.Direction.REVERSE);
        liftPower = 0;
        lift.setPower(liftPower);
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        // Servos.
        clampRight = hardwareMap.get(Servo.class, "right_clamp");    
        clampLeft = hardwareMap.get(Servo.class, "left_clamp");
        ClampClose();
     
        armLeft = hardwareMap.get(Servo.class, "arm_left"); 
        armRight = hardwareMap.get(Servo.class, "arm_right");
        armLeftPosition = ArmPosition.Back;
        armRightPosition = ArmPosition.Back;
        ArmMoveToPosition();
        
        intakeLeft   = hardwareMap.get(DcMotor.class, "left_intake");
        intakeRight  = hardwareMap.get(DcMotor.class, "right_intake");
        intakeStatus = IntakeStatus.Off;
        IntakeSetPower();
        
        jewelArm = hardwareMap.get(Servo.class, "jewel_arm");
        JewelUp();
        
        sensorColor = hardwareMap.get(ColorSensor.class, "sensor_color");
        sensorDistance = hardwareMap.get(DistanceSensor.class, "sensor_color");
        
        UpdateTelemetry(); 
    }

    
    
    public void autoInitMotor()
    {
        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    
    public void teleopInitMotor()
    {
        leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        
    }
    
    void UpdateTelemetry()
    {
        telemetry.addData("Motors", "L:%.2f,R:%.2f,Lift:%.2f", leftDrivePower, rightDrivePower, liftPower);
        telemetry.addData("Color", "R:%.0f,G:%.0f,B:%.0f,H:%.2f=>%s", colorRed, colorGreen, colorBlue, colorHue, resultColor);     
        telemetry.addData("JewelArm", "%s", jewelArmPosition);
        telemetry.addData("Clamp", "%s", clampPosition);
        telemetry.addData("Arm", "Left:%s, Right:%s, Intake:%s", armLeftPosition, armRightPosition, intakeStatus);
        
    }
    
    
    public  Detected_Color GetColor()
    {
        double COLOR_SCALE_FACTOR = 255;
        
        float hsvValues[] = {0F, 0F, 0F};
        colorRed = sensorColor.red();
        colorGreen = sensorColor.green();
        colorBlue = sensorColor.blue();
        
        Color.RGBToHSV(
            (int) (colorRed * COLOR_SCALE_FACTOR),
            (int) (colorGreen * COLOR_SCALE_FACTOR),
            (int) (colorBlue * COLOR_SCALE_FACTOR),
            hsvValues);
        colorHue = hsvValues[0];
        if(colorHue > 180 && colorHue <240)
        {
            resultColor = Detected_Color.Blue;
        }
        else if (colorHue>340 || colorHue < 20)
        {
            resultColor = Detected_Color.Red;
        }
        else
        {
            resultColor = Detected_Color.Unknown;
        }
        return resultColor;
    }

    public void IntakeSwitchOnOff()
    {
        if (IntakeStatus.On == intakeStatus){
            intakeStatus = IntakeStatus.Off;
        } else {
            intakeStatus = IntakeStatus.On;
        }
        IntakeSetPower();
    }

    final static double INTAKE_ON_POWER  = -.5;
    public void IntakeSetPower()
    {
        if (IntakeStatus.On == intakeStatus){
            intakeLeft.setPower(INTAKE_ON_POWER);
            intakeRight.setPower(INTAKE_ON_POWER);
        }else {
            intakeLeft.setPower(0);
            intakeRight.setPower(0);
        }
    }


    final static double JEWELARM_UP  = 0;
    final static double JEWELARM_DOWN  = 0.5;
    public void JewelUp() {
        
         jewelArm.setPosition(JEWELARM_UP);
         jewelArmPosition = JewelArmPosition.Up;
    }
   
    public void JewelDown() {
         jewelArm.setPosition(JEWELARM_DOWN);
         jewelArmPosition = JewelArmPosition.Down;
    }
    
    public void LiftUp()
    {
        liftPower = 0.8;
        lift.setPower(liftPower);
    }
    
    public void LiftDown()
    {
        liftPower = -0.8;
        lift.setPower(liftPower);
    }
    
    public void LiftStop()
    {
        liftPower = 0;
        lift.setPower(liftPower);
    }
   
    public void TankDrive(double leftPower, double rightPower)
    {
        leftDrivePower = leftPower;
        leftDrive.setPower(leftDrivePower); 
        rightDrivePower = rightPower;
        rightDrive.setPower(rightPower);
    }
    
    
    static final double Clamp_Right_ClosePosition = .4;
    static final double Clamp_Left_ClosePosition = .6;    
    public void ClampClose() {
        clampRight.setPosition(Clamp_Right_ClosePosition);
        clampLeft.setPosition(Clamp_Left_ClosePosition);
        clampPosition = ClampPosition.Close;
    }
    
    
    static final double Clamp_Right_OpenPosition = .5;
    static final double Clamp_Left_OpenPosition = .47;     
    public void ClampOpen() {
        clampRight.setPosition(Clamp_Right_OpenPosition);
        clampLeft.setPosition(Clamp_Left_OpenPosition);
        clampPosition = ClampPosition.Open;
        
    }
    
    public void BothArmMoveBack()
    {
        armLeftPosition = ArmPosition.Back;
        armRightPosition = ArmPosition.Back;
        ArmMoveToPosition();
    }
    
    public void BothArmFlat()
    {
        armLeftPosition = ArmPosition.Flat;
        armRightPosition = ArmPosition.Flat;
        ArmMoveToPosition();
    }
    
    public void BothArmOpen()
    {
        armLeftPosition = ArmPosition.Open;
        armRightPosition = ArmPosition.Open;
        ArmMoveToPosition();
    }
    
    public void BothArmClose()
    {
        armLeftPosition = ArmPosition.Close;
        armRightPosition = ArmPosition.Close;
        ArmMoveToPosition();
    }
    
    public void ArmPushLeft()
    {
        armLeftPosition = ArmPosition.Open;
        armRightPosition = ArmPosition.Close;
        ArmMoveToPosition();
    }
    
    public void ArmPushRight()
    {
        armLeftPosition = ArmPosition.Close;
        armRightPosition = ArmPosition.Open;
        ArmMoveToPosition();
    }
    
    public void ArmOpenClose()
    {
        if (ArmPosition.Open == armLeftPosition && ArmPosition.Open == armRightPosition)
        {
            BothArmClose();
        }
        else {
            BothArmOpen();
        }
    }
    
    public void ArmPushLeftRight()
    {
        if (ArmPosition.Open == armLeftPosition && ArmPosition.Close == armRightPosition)
        {
            ArmPushRight();
        }
        else {
            ArmPushLeft();
        }
    }


    static final double Arm_Right_BackPosition = 0;
    static final double Arm_Right_FlatPosition = .4;
    static final double Arm_Right_OpenPosition = .75;
    static final double Arm_Right_ClosePosition = .85;
    
    static final double Arm_Left_BackPosition = 1;
    static final double Arm_Left_FlatPosition = .6;
    static final double Arm_Left_OpenPosition = .25;
    static final double Arm_Left_ClosePosition = .15;
    
    public void ArmMoveToPosition(){
        switch (armLeftPosition){
            case Back:
                armLeft.setPosition(Arm_Left_BackPosition);
                break;
            case Flat:
                armLeft.setPosition(Arm_Left_FlatPosition);
                break;
            case Close:
                armLeft.setPosition(Arm_Left_ClosePosition);
                break;
            case Open:
                armLeft.setPosition(Arm_Left_OpenPosition);
        }
        switch (armRightPosition){
            case Back:
                armRight.setPosition(Arm_Right_BackPosition);
                break;
            case Flat:
                armRight.setPosition(Arm_Right_FlatPosition);
                break;
            case Close:
                armRight.setPosition(Arm_Right_ClosePosition);
                break;
            case Open:
                armRight.setPosition(Arm_Right_OpenPosition);
        }        
    }
    
    
    public void DriveStop()
    {
        TankDrive(0,0);
    }
}
