package org.firstinspires.ftc.teamcode;

enum Auto_Step
{
    MoveArmDown,
    SenseAndTurnToHitJewel,
    MoveArmUp,
    TurnBack,
    
    MoveToPosition,
    TurnToCrypoBox,
    MoveForwardRelease,
    PushIn,
    Backoff,
    
    EndStop,
}

public class AutoConfig {
    public Detected_Color sideColor;
    public boolean positionMiddle;

    public double middleLeftDistance;
    public double middleCenterDistance;
    public double middleRightDistance;

    public double cornerFirstMoveDistance;    
    public double cornerLeftTurnDistance;
    public double cornerLeftDistance;
    public double cornerCenterDistance;
    public double cornerRightDistance;

    public double faceCrypoBoxRightTurnDistance; 
}
