
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;



@Autonomous(name = "BlueCorner", group = "plms")
public class BlueCorner extends AutoBase {
    public BlueCorner()
    {
        autoConfig = new AutoConfig();
        autoConfig.sideColor = Detected_Color.Blue;
        autoConfig.positionMiddle = false;

        autoConfig.cornerFirstMoveDistance = -15;
        autoConfig.cornerLeftTurnDistance = 5;

        autoConfig.cornerLeftDistance = 2;
        autoConfig.cornerCenterDistance = 7;
        autoConfig.cornerRightDistance = 10;

        autoConfig.faceCrypoBoxRightTurnDistance = -5;
    }
}
